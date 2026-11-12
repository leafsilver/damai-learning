package com.damai.learning.program.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.damai.learning.common.exception.BusinessException;
import com.damai.learning.common.exception.CommonErrorCode;
import com.damai.learning.common.page.PageResult;
import com.damai.learning.program.dto.ProgramCreateRequest;
import com.damai.learning.program.dto.ProgramUpdateRequest;
import com.damai.learning.program.entity.Program;
import com.damai.learning.program.entity.ProgramSession;
import com.damai.learning.program.entity.TicketCategory;
import com.damai.learning.program.mapper.ProgramMapper;
import com.damai.learning.program.mapper.ProgramSessionMapper;
import com.damai.learning.program.mapper.TicketCategoryMapper;
import com.damai.learning.program.message.model.ProgramChangedMessage;
import com.damai.learning.program.vo.ProgramDetailVO;
import com.damai.learning.program.vo.ProgramListVO;
import com.damai.learning.program.vo.ProgramSessionVO;
import com.damai.learning.program.vo.TicketCategoryVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 节目业务服务，负责节目查询、维护及详情缓存管理。
 */
@Service
public class ProgramService {

    private static final Logger log = LoggerFactory.getLogger(ProgramService.class);

    private static final String PROGRAM_DETAIL_KEY_PREFIX = "damai:program:detail:";

    private static final Duration PROGRAM_DETAIL_TTL = Duration.ofMinutes(10);

    /**
     * 数据不存在时存入Redis的特殊标记。
     */
    private static final String NULL_CACHE_VALUE = "__NULL__";

    /**
     * 空值缓存时间要短一些，避免数据库后来有数据了，
     * Redis却长时间仍然认为数据不存在。
     */
    private static final Duration NULL_CACHE_TTL = Duration.ofMinutes(2);

    @Autowired
    private ProgramMapper programMapper;

    @Autowired
    private ProgramSessionMapper programSessionMapper;

    @Autowired
    private TicketCategoryMapper ticketCategoryMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * 分页查询已上架且未删除的节目。
     */
    public PageResult<ProgramListVO> pageOnSalePrograms(long pageNum, long pageSize) {
        if (pageNum < 1 || pageSize < 1 || pageSize > 100) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        Page<Program> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Program> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper
                .eq(Program::getProgramStatus, 1)
                .eq(Program::getStatus, 1)
                .orderByDesc(Program::getCreateTime);

        Page<Program> programPage = programMapper.selectPage(page, queryWrapper);

        List<ProgramListVO> records = programPage.getRecords().stream()
                .map(this::toProgramListVO)
                .toList();

        return new PageResult<>(
                programPage.getCurrent(),
                programPage.getSize(),
                programPage.getTotal(),
                programPage.getPages(),
                records
        );
    }

    /**
     * 查询节目详情，优先读取Redis，缓存不可用时回退到MySQL。
     */
    public ProgramDetailVO getProgramDetail(Long id) {
        String cacheKey = buildProgramDetailCacheKey(id);
        ProgramDetailVO cachedProgram = getProgramDetailFromCache(cacheKey);

        if (cachedProgram != null) {
            log.info("节目详情缓存命中，id={}", id);
            return cachedProgram;
        }

        log.info("节目详情缓存未命中，查询MySQL，id={}", id);

        Program program = programMapper.selectById(id);

        if (program == null
                || !Objects.equals(program.getStatus(), 1)
                || !Objects.equals(program.getProgramStatus(), 1)) {
            cacheProgramNotExist(cacheKey);
            throw new BusinessException(CommonErrorCode.PROGRAM_NOT_EXIST);
        }

        ProgramDetailVO programDetailVO = toProgramDetailVO(program);
        cacheProgramDetail(cacheKey, programDetailVO);

        return programDetailVO;
    }

    private ProgramListVO toProgramListVO(Program program) {
        ProgramListVO programListVO = new ProgramListVO();

        programListVO.setId(program.getId());
        programListVO.setTitle(program.getTitle());
        programListVO.setActor(program.getActor());
        programListVO.setPlace(program.getPlace());
        programListVO.setItemPicture(program.getItemPicture());

        return programListVO;
    }

    private ProgramDetailVO toProgramDetailVO(Program program) {
        ProgramDetailVO programDetailVO = new ProgramDetailVO();

        programDetailVO.setId(program.getId());
        programDetailVO.setTitle(program.getTitle());
        programDetailVO.setActor(program.getActor());
        programDetailVO.setPlace(program.getPlace());
        programDetailVO.setItemPicture(program.getItemPicture());

        return programDetailVO;
    }

    /**
     * 新增节目，默认设置为上架且未删除状态。
     */
    @Transactional
    public Long createProgram(ProgramCreateRequest request) {
        Program program = new Program();

        program.setTitle(request.getTitle());
        program.setActor(request.getActor());
        program.setPlace(request.getPlace());
        program.setItemPicture(request.getItemPicture());

        program.setProgramStatus(1);
        program.setStatus(1);

        programMapper.insert(program);

        applicationEventPublisher.publishEvent(
                new ProgramChangedMessage(program.getId())
        );

        return program.getId();
    }

    /**
     * 编辑节目并删除详情缓存，缓存删除失败时回滚数据库修改。
     */
    @Transactional
    public void updateProgram(Long id, ProgramUpdateRequest request) {
        LambdaUpdateWrapper<Program> updateWrapper = new LambdaUpdateWrapper<>();

        updateWrapper
                .eq(Program::getId, id)
                .eq(Program::getStatus, 1)
                .set(Program::getTitle, request.getTitle())
                .set(Program::getActor, request.getActor())
                .set(Program::getPlace, request.getPlace())
                .set(Program::getItemPicture, request.getItemPicture());

        int affectedRows = programMapper.update(null, updateWrapper);

        if (affectedRows != 1) {
            throw new BusinessException(CommonErrorCode.PROGRAM_NOT_EXIST);
        }

        deleteProgramDetailCache(id);

        applicationEventPublisher.publishEvent(
                new ProgramChangedMessage(id)
        );
    }

    /**
     * 下架节目并删除详情缓存，缓存删除失败时回滚数据库修改。
     */
    @Transactional
    public void offSaleProgram(Long id) {
        LambdaUpdateWrapper<Program> updateWrapper = new LambdaUpdateWrapper<>();

        updateWrapper
                .eq(Program::getId, id)
                .eq(Program::getStatus, 1)
                .eq(Program::getProgramStatus, 1)
                .set(Program::getProgramStatus, 0);

        int affectedRows = programMapper.update(null, updateWrapper);

        if (affectedRows != 1) {
            throw new BusinessException(CommonErrorCode.PROGRAM_CANNOT_OFF_SALE);
        }

        deleteProgramDetailCache(id);

        applicationEventPublisher.publishEvent(
                new ProgramChangedMessage(id)
        );
    }

    /**
     * 查询指定节目下启用的场次及票档。
     */
    public List<ProgramSessionVO> listProgramSessions(Long programId) {
        Program program = programMapper.selectById(programId);

        if (program == null
                || !Objects.equals(program.getStatus(), 1)
                || !Objects.equals(program.getProgramStatus(), 1)) {
            throw new BusinessException(CommonErrorCode.PROGRAM_NOT_EXIST);
        }

        LambdaQueryWrapper<ProgramSession> sessionQueryWrapper = new LambdaQueryWrapper<>();

        sessionQueryWrapper
                .eq(ProgramSession::getProgramId, programId)
                .eq(ProgramSession::getSessionStatus, 1)
                .eq(ProgramSession::getStatus, 1)
                .orderByAsc(ProgramSession::getShowTime);

        List<ProgramSession> sessions = programSessionMapper.selectList(sessionQueryWrapper);

        if (sessions.isEmpty()) {
            return List.of();
        }

        List<Long> sessionIds = sessions.stream()
                .map(ProgramSession::getId)
                .toList();

        LambdaQueryWrapper<TicketCategory> ticketQueryWrapper = new LambdaQueryWrapper<>();

        ticketQueryWrapper
                .in(TicketCategory::getSessionId, sessionIds)
                .eq(TicketCategory::getTicketStatus, 1)
                .eq(TicketCategory::getStatus, 1)
                .orderByAsc(TicketCategory::getPrice);

        List<TicketCategory> ticketCategories = ticketCategoryMapper.selectList(ticketQueryWrapper);

        Map<Long, List<TicketCategory>> ticketCategoryMap = ticketCategories.stream()
                .collect(Collectors.groupingBy(TicketCategory::getSessionId));

        return sessions.stream()
                .map(session -> toProgramSessionVO(
                        session, ticketCategoryMap.getOrDefault(session.getId(), List.of())))
                .toList();
    }

    /**
     * 将场次实体转换为场次响应。
     */
    private ProgramSessionVO toProgramSessionVO(ProgramSession session, List<TicketCategory> ticketCategories) {
        ProgramSessionVO sessionVO = new ProgramSessionVO();

        sessionVO.setId(session.getId());
        sessionVO.setSessionName(session.getSessionName());
        sessionVO.setShowTime(session.getShowTime());
        sessionVO.setSaleStartTime(session.getSaleStartTime());
        sessionVO.setSaleEndTime(session.getSaleEndTime());

        List<TicketCategoryVO> ticketCategoryVOList = ticketCategories.stream()
                .map(this::toTicketCategoryVO)
                .toList();

        sessionVO.setTicketCategories(ticketCategoryVOList);

        return sessionVO;
    }

    /**
     * 将票档实体转换为票档响应。
     */
    private TicketCategoryVO toTicketCategoryVO(TicketCategory ticketCategory) {
        TicketCategoryVO ticketCategoryVO = new TicketCategoryVO();

        ticketCategoryVO.setId(ticketCategory.getId());
        ticketCategoryVO.setCategoryName(ticketCategory.getCategoryName());
        ticketCategoryVO.setPrice(ticketCategory.getPrice());
        ticketCategoryVO.setAvailableStock(ticketCategory.getAvailableStock());

        return ticketCategoryVO;
    }

    private String buildProgramDetailCacheKey(Long id) {
        return PROGRAM_DETAIL_KEY_PREFIX + id;
    }

    /**
     * 读取并解析节目详情缓存，空值标记表示节目不存在。
     */
    private ProgramDetailVO getProgramDetailFromCache(String cacheKey) {
        String json;

        try {
            json = stringRedisTemplate.opsForValue().get(cacheKey);
        } catch (DataAccessException exception) {
            log.warn("读取节目详情缓存失败，回退到MySQL，cacheKey={}", cacheKey, exception);
            return null;
        }

        if (json == null) {
            return null;
        }

        if (NULL_CACHE_VALUE.equals(json)) {
            log.info("节目详情空值缓存命中，cacheKey={}", cacheKey);
            throw new BusinessException(CommonErrorCode.PROGRAM_NOT_EXIST);
        }

        try {
            return objectMapper.readValue(json, ProgramDetailVO.class);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("节目详情缓存解析失败", exception);
        }
    }

    /**
     * 写入节目详情缓存，缓存异常不影响数据库查询结果。
     */
    private void cacheProgramDetail(String cacheKey, ProgramDetailVO programDetailVO) {
        try {
            String json = objectMapper.writeValueAsString(programDetailVO);
            stringRedisTemplate.opsForValue().set(cacheKey, json, PROGRAM_DETAIL_TTL);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("节目详情转换JSON失败", exception);
        } catch (DataAccessException exception) {
            log.warn("写入节目详情缓存失败，忽略缓存写入，cacheKey={}", cacheKey, exception);
        }
    }

    /**
     * 缓存短期空值，避免不存在的节目反复访问数据库。
     */
    private void cacheProgramNotExist(String cacheKey) {
        try {
            stringRedisTemplate.opsForValue().set(cacheKey, NULL_CACHE_VALUE, NULL_CACHE_TTL);
        } catch (DataAccessException exception) {
            log.warn("写入节目空值缓存失败，忽略缓存写入，cacheKey={}", cacheKey, exception);
        }
    }

    /**
     * 删除指定节目的详情缓存。
     */
    private void deleteProgramDetailCache(Long id) {
        String cacheKey = buildProgramDetailCacheKey(id);
        stringRedisTemplate.delete(cacheKey);
    }
}
