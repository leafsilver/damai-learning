package com.damai.learning.program.repository;

import com.damai.learning.program.document.ProgramDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 节目搜索文档数据访问接口。
 */
public interface ProgramSearchRepository
        extends ElasticsearchRepository<ProgramDocument, Long> {
}