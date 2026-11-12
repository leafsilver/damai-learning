package com.damai.learning.program.vo;

import lombok.Data;

/**
 * 节目搜索结果。
 */
@Data
public class ProgramSearchVO {

    /** 节目id。 */
    private Long id;

    /** 原始节目标题。 */
    private String title;

    /** 原始艺人名称。 */
    private String actor;

    /** 原始演出地点。 */
    private String place;

    /** 节目图片地址。 */
    private String itemPicture;

    /** 包含关键词高亮标签的节目标题。 */
    private String highlightedTitle;

    /** 包含关键词高亮标签的艺人名称。 */
    private String highlightedActor;

    /** 包含关键词高亮标签的演出地点。 */
    private String highlightedPlace;
}