package com.damai.learning.program.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 票档实体，对应数据库表d_ticket_category。
 */
@Data
@TableName("d_ticket_category")
public class TicketCategory {

    /** 票档主键。 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属场次id。 */
    private Long sessionId;

    /** 票档名称。 */
    private String categoryName;

    /** 票价。 */
    private BigDecimal price;

    /** 总库存。 */
    private Integer totalStock;

    /** 当前可用库存。 */
    private Integer availableStock;

    /** 票档状态：1启用，0停用。 */
    private Integer ticketStatus;

    /** 创建时间。 */
    private LocalDateTime createTime;

    /** 最后修改时间。 */
    private LocalDateTime editTime;

    /** 数据状态：1正常，0删除。 */
    private Integer status;
}