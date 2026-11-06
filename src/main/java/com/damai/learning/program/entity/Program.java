package com.damai.learning.program.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 节目实体，对应数据库表d_program。
 */
@Data
@TableName("d_program")
public class Program {

    /** 节目主键。 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 节目标题。 */
    private String title;

    /** 艺人名称。 */
    private String actor;

    /** 演出地点。 */
    private String place;

    /** 节目图片地址。 */
    private String itemPicture;

    /** 节目状态：1上架，0下架。 */
    private Integer programStatus;

    /** 创建时间。 */
    private LocalDateTime createTime;

    /** 最后修改时间。 */
    private LocalDateTime editTime;

    /** 数据状态：1正常，0删除。 */
    private Integer status;
}
