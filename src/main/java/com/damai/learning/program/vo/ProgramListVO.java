package com.damai.learning.program.vo;

import lombok.Data;

/**
 * 节目列表响应。
 */
@Data
public class ProgramListVO {

    /** 节目id。 */
    private Long id;

    /** 节目标题。 */
    private String title;

    /** 艺人名称。 */
    private String actor;

    /** 演出地点。 */
    private String place;

    /** 节目图片地址。 */
    private String itemPicture;
}
