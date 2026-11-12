package com.damai.learning.program.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.damai.learning.common.exception.BusinessException;
import com.damai.learning.common.exception.CommonErrorCode;
import com.damai.learning.common.page.PageResult;
import com.damai.learning.program.document.ProgramDocument;
import com.damai.learning.program.entity.Program;
import com.damai.learning.program.mapper.ProgramMapper;
import com.damai.learning.program.repository.ProgramSearchRepository;
import com.damai.learning.program.vo.ProgramSearchVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 节目搜索服务，负责节目搜索数据的同步和查询。
 */
@Service
public class ProgramSearchService {

    @Autowired
    private ProgramMapper programMapper;

    @Autowired
    private ProgramSearchRepository programSearchRepository;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    /**
     * 根据MySQL中的有效节目重新构建ES节目索引。
     *
     * @return 写入ES的节目数量
     */
    public int rebuildProgramIndex() {
        LambdaQueryWrapper<Program> queryWrapper =
                new LambdaQueryWrapper<>();

        queryWrapper.eq(Program::getStatus, 1);

        // 先读取MySQL，避免查询失败时提前清空ES。
        List<Program> programs =
                programMapper.selectList(queryWrapper);

        List<ProgramDocument> documents = programs.stream()
                .map(this::toProgramDocument)
                .toList();

        // 清理可能已经失效的旧搜索文档，再写入当前有效数据。
        programSearchRepository.deleteAll();
        programSearchRepository.saveAll(documents);

        return documents.size();
    }

    /**
     * 根据关键词分页搜索已上架节目。
     */
    public PageResult<ProgramSearchVO> searchPrograms(
            String keyword,
            long pageNum,
            long pageSize
    ) {
        if (keyword == null
                || keyword.isBlank()
                || pageNum < 1
                || pageNum > Integer.MAX_VALUE
                || pageSize < 1
                || pageSize > 100) {
            throw new BusinessException(
                    CommonErrorCode.INVALID_PARAMETER
            );
        }

        HighlightParameters highlightParameters =
                HighlightParameters.builder()
                        .withPreTags("<em>")
                        .withPostTags("</em>")
                        .withEncoder("html")
                        .withNumberOfFragments(0)
                        .build();

        Highlight highlight = new Highlight(
                highlightParameters,
                List.of(
                        new HighlightField("title"),
                        new HighlightField("actor"),
                        new HighlightField("place")
                )
        );

        HighlightQuery highlightQuery = new HighlightQuery(
                highlight,
                ProgramDocument.class
        );

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(query -> query
                        .bool(bool -> bool
                                .must(must -> must
                                        .multiMatch(multiMatch -> multiMatch
                                                .query(keyword.trim())
                                                .fields(
                                                        "title^3",
                                                        "actor^2",
                                                        "place"
                                                )
                                        )
                                )
                                .filter(filter -> filter
                                        .term(term -> term
                                                .field("programStatus")
                                                .value(1)
                                        )
                                )
                        )
                )
                .withHighlightQuery(highlightQuery)
                .withPageable(
                        PageRequest.of(
                                (int) (pageNum - 1),
                                (int) pageSize
                        )
                )
                .build();

        SearchHits<ProgramDocument> searchHits =
                elasticsearchOperations.search(
                        searchQuery,
                        ProgramDocument.class
                );

        List<ProgramSearchVO> records = searchHits
                .getSearchHits()
                .stream()
                .map(this::toProgramSearchVO)
                .toList();

        long total = searchHits.getTotalHits();
        long pages = (total + pageSize - 1) / pageSize;

        return new PageResult<>(
                pageNum,
                pageSize,
                total,
                pages,
                records
        );
    }

    /**
     * 根据MySQL当前数据同步单个节目到ES。
     */
    public void syncProgram(Long programId) {
        Program program = programMapper.selectById(programId);

        if (program == null
                || !Objects.equals(program.getStatus(), 1)) {
            programSearchRepository.deleteById(programId);
            return;
        }

        ProgramDocument document =
                toProgramDocument(program);

        programSearchRepository.save(document);
    }

    /**
     * 将MySQL节目实体转换为ES搜索文档。
     */
    private ProgramDocument toProgramDocument(Program program) {
        ProgramDocument document = new ProgramDocument();

        document.setId(program.getId());
        document.setTitle(program.getTitle());
        document.setActor(program.getActor());
        document.setPlace(program.getPlace());
        document.setItemPicture(program.getItemPicture());
        document.setProgramStatus(program.getProgramStatus());
        document.setCreateTime(program.getCreateTime());

        return document;
    }

    /**
     * 将ES搜索命中结果转换为搜索响应。
     */
    private ProgramSearchVO toProgramSearchVO(
            SearchHit<ProgramDocument> searchHit
    ) {
        ProgramDocument document = searchHit.getContent();
        ProgramSearchVO searchVO = new ProgramSearchVO();

        searchVO.setId(document.getId());
        searchVO.setTitle(document.getTitle());
        searchVO.setActor(document.getActor());
        searchVO.setPlace(document.getPlace());
        searchVO.setItemPicture(document.getItemPicture());

        searchVO.setHighlightedTitle(
                getHighlightText(
                        searchHit,
                        "title",
                        document.getTitle()
                )
        );
        searchVO.setHighlightedActor(
                getHighlightText(
                        searchHit,
                        "actor",
                        document.getActor()
                )
        );
        searchVO.setHighlightedPlace(
                getHighlightText(
                        searchHit,
                        "place",
                        document.getPlace()
                )
        );

        return searchVO;
    }

    /**
     * 获取字段高亮内容，没有高亮时返回原始值。
     */
    private String getHighlightText(
            SearchHit<ProgramDocument> searchHit,
            String fieldName,
            String originalText
    ) {
        List<String> highlightTexts =
                searchHit.getHighlightField(fieldName);

        if (highlightTexts == null
                || highlightTexts.isEmpty()) {
            return originalText;
        }

        return String.join("", highlightTexts);
    }
}
