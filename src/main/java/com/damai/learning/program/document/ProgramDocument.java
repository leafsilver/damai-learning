package com.damai.learning.program.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;

/**
 * 节目搜索文档，对应Elasticsearch索引damai_program。
 */
@Data
@Document(
        indexName = "damai_program",
        createIndex = false,
        writeTypeHint = WriteTypeHint.FALSE
)
public class ProgramDocument {

    /** ES文档id，与MySQL节目id保持一致。 */
    @Id
    @Field(type = FieldType.Long)
    private Long id;

    /** 节目标题，支持中文全文搜索和精确匹配。 */
    @MultiField(
            mainField = @Field(
                    type = FieldType.Text,
                    analyzer = "ik_max_word",
                    searchAnalyzer = "ik_smart"
            ),
            otherFields = {
                    @InnerField(
                            suffix = "keyword",
                            type = FieldType.Keyword
                    )
            }
    )
    private String title;

    /** 艺人名称，支持中文全文搜索和精确匹配。 */
    @MultiField(
            mainField = @Field(
                    type = FieldType.Text,
                    analyzer = "ik_max_word",
                    searchAnalyzer = "ik_smart"
            ),
            otherFields = {
                    @InnerField(
                            suffix = "keyword",
                            type = FieldType.Keyword
                    )
            }
    )
    private String actor;

    /** 演出地点，支持中文全文搜索和精确匹配。 */
    @MultiField(
            mainField = @Field(
                    type = FieldType.Text,
                    analyzer = "ik_max_word",
                    searchAnalyzer = "ik_smart"
            ),
            otherFields = {
                    @InnerField(
                            suffix = "keyword",
                            type = FieldType.Keyword
                    )
            }
    )
    private String place;

    /** 节目图片，只返回给前端，不参与搜索。 */
    @Field(
            type = FieldType.Keyword,
            index = false
    )
    private String itemPicture;

    /** 节目状态：1上架，0下架。 */
    @Field(type = FieldType.Integer)
    private Integer programStatus;

    /** 节目创建时间。 */
    @Field(
            type = FieldType.Date,
            format = {},
            pattern = "uuuu-MM-dd HH:mm:ss"
    )
    private LocalDateTime createTime;
}