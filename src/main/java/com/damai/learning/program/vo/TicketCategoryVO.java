package com.damai.learning.program.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 票档响应。
 */
@Data
public class TicketCategoryVO {

    /** 票档id。 */
    private Long id;

    /** 票档名称。 */
    private String categoryName;

    /** 票价。 */
    private BigDecimal price;

    /** 当前可用库存。 */
    private Integer availableStock;
}