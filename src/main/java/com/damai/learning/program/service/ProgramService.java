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
import com.damai.learning.program.mapper.ProgramMapper;
import com.damai.learning.program.vo.ProgramDetailVO;
import com.damai.learning.program.vo.ProgramListVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

@Service
public class ProgramService {

    private static final Logger log =
            LoggerFactory.getLogger(ProgramService.class);

    private static final String PROGRAM_DETAIL_KEY_PREFIX =
            "damai:program:detail:";

    private static final Duration PROGRAM_DETAIL_TTL =
            Duration.ofMinutes(10);

    /**
     * 数据不存在时存入Redis的特殊标记。
     */
    private static final String NULL_CACHE_VALUE =
            "__NULL__";

    /**
     * 空值缓存时间要短一些，避免数据库后来有数据了，
     * Redis却长时间仍然认为数据不存在。
     */
    private static final Duration NULL_CACHE_TTL =
            Duration.ofMinutes(2);

    @Autowired
    private ProgramMapper programMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public PageResult<ProgramListVO> pageOnSalePrograms(
            long pageNum,
            long pageSize
    ) {
        if (pageNum < 1
                || pageSize < 1
                || pageSize > 100) {
            throw new BusinessException(
                    CommonErrorCode.INVALID_PARAMETER
            );
        }

        Page<Program> page =
                new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<Program> queryWrapper =
                new LambdaQueryWrapper<>();

        queryWrapper
                .eq(Program::getProgramStatus, 1)
                .eq(Program::getStatus, 1)
                .orderByDesc(Program::getCreateTime);

        Page<Program> programPage =
                programMapper.selectPage(
                        page,
                        queryWrapper
                );

        List<ProgramListVO> records =
                programPage.getRecords()
                        .stream()
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

    public ProgramDetailVO getProgramDetail(Long id) {
        String cacheKey =
                buildProgramDetailCacheKey(id);

        ProgramDetailVO cachedProgram =
                getProgramDetailFromCache(cacheKey);

        if (cachedProgram != null) {
            log.info(
                    "节目详情缓存命中，id={}",
                    id
            );

            return cachedProgram;
        }

        log.info(
                "节目详情缓存未命中，查询MySQL，id={}",
                id
        );

        Program program = programMapper.selectById(id);

        if (program == null
                || !Objects.equals(program.getStatus(), 1)
                || !Objects.equals(
                program.getProgramStatus(),
                1
        )) {
            cacheProgramNotExist(cacheKey);

            throw new BusinessException(
                    CommonErrorCode.PROGRAM_NOT_EXIST
            );
        }

        ProgramDetailVO programDetailVO =
                toProgramDetailVO(program);

        cacheProgramDetail(
                cacheKey,
                programDetailVO
        );

        return programDetailVO;
    }

    private ProgramListVO toProgramListVO(Program program) {
        ProgramListVO programListVO = new ProgramListVO();

        programListVO.setId(program.getId());
        programListVO.setTitle(program.getTitle());
        programListVO.setActor(program.getActor());
        programListVO.setPlace(program.getPlace());
        programListVO.setItemPicture(
                program.getItemPicture()
        );

        return programListVO;
    }

    private ProgramDetailVO toProgramDetailVO(
            Program program
    ) {
        ProgramDetailVO programDetailVO =
                new ProgramDetailVO();

        programDetailVO.setId(program.getId());
        programDetailVO.setTitle(program.getTitle());
        programDetailVO.setActor(program.getActor());
        programDetailVO.setPlace(program.getPlace());
        programDetailVO.setItemPicture(
                program.getItemPicture()
        );

        return programDetailVO;
    }

    public Long createProgram(
            ProgramCreateRequest request
    ) {
        Program program = new Program();

        program.setTitle(request.getTitle());
        program.setActor(request.getActor());
        program.setPlace(request.getPlace());
        program.setItemPicture(
                request.getItemPicture()
        );

        program.setProgramStatus(1);
        program.setStatus(1);

        programMapper.insert(program);

        return program.getId();
    }

    @Transactional
    public void updateProgram(
            Long id,
            ProgramUpdateRequest request
    ) {
        LambdaUpdateWrapper<Program> updateWrapper =
                new LambdaUpdateWrapper<>();

        updateWrapper
                .eq(Program::getId, id)
                .eq(Program::getStatus, 1)
                .set(
                        Program::getTitle,
                        request.getTitle()
                )
                .set(
                        Program::getActor,
                        request.getActor()
                )
                .set(
                        Program::getPlace,
                        request.getPlace()
                )
                .set(
                        Program::getItemPicture,
                        request.getItemPicture()
                );

        int affectedRows =
                programMapper.update(
                        null,
                        updateWrapper
                );

        if (affectedRows != 1) {
            throw new BusinessException(
                    CommonErrorCode.PROGRAM_NOT_EXIST
            );
        }

        deleteProgramDetailCache(id);
    }

    @Transactional
    public void offSaleProgram(Long id) {
        LambdaUpdateWrapper<Program> updateWrapper =
                new LambdaUpdateWrapper<>();

        updateWrapper
                .eq(Program::getId, id)
                .eq(Program::getStatus, 1)
                .eq(Program::getProgramStatus, 1)
                .set(Program::getProgramStatus, 0);

        int affectedRows =
                programMapper.update(
                        null,
                        updateWrapper
                );

        if (affectedRows != 1) {
            throw new BusinessException(
                    CommonErrorCode.PROGRAM_CANNOT_OFF_SALE
            );
        }

        deleteProgramDetailCache(id);
    }

    private String buildProgramDetailCacheKey(Long id) {
        return PROGRAM_DETAIL_KEY_PREFIX + id;
    }

    private ProgramDetailVO getProgramDetailFromCache(
            String cacheKey
    ) {
        String json;

        try {
            json = stringRedisTemplate
                    .opsForValue()
                    .get(cacheKey);
        } catch (DataAccessException exception) {
            log.warn(
                    "读取节目详情缓存失败，回退到MySQL，cacheKey={}",
                    cacheKey,
                    exception
            );

            return null;
        }

        if (json == null) {
            return null;
        }

        if (NULL_CACHE_VALUE.equals(json)) {
            log.info(
                    "节目详情空值缓存命中，cacheKey={}",
                    cacheKey
            );

            throw new BusinessException(
                    CommonErrorCode.PROGRAM_NOT_EXIST
            );
        }

        try {
            return objectMapper.readValue(
                    json,
                    ProgramDetailVO.class
            );
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(
                    "节目详情缓存解析失败",
                    exception
            );
        }
    }

    private void cacheProgramDetail(
            String cacheKey,
            ProgramDetailVO programDetailVO
    ) {
        try {
            String json =
                    objectMapper.writeValueAsString(
                            programDetailVO
                    );

            stringRedisTemplate
                    .opsForValue()
                    .set(
                            cacheKey,
                            json,
                            PROGRAM_DETAIL_TTL
                    );
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(
                    "节目详情转换JSON失败",
                    exception
            );
        }
        catch (DataAccessException exception) {
            log.warn(
                    "写入节目详情缓存失败，忽略缓存写入，cacheKey={}",
                    cacheKey,
                    exception
            );
        }
    }

    private void cacheProgramNotExist(
            String cacheKey
    ) {
        try {
            stringRedisTemplate
                    .opsForValue()
                    .set(
                            cacheKey,
                            NULL_CACHE_VALUE,
                            NULL_CACHE_TTL
                    );
        } catch (DataAccessException exception) {
            log.warn(
                    "写入节目空值缓存失败，忽略缓存写入，cacheKey={}",
                    cacheKey,
                    exception
            );
        }
    }

    private void deleteProgramDetailCache(Long id) {
        String cacheKey =
                buildProgramDetailCacheKey(id);

        stringRedisTemplate.delete(cacheKey);
    }
}