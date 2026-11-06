package com.damai.learning.order.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * 创建订单请求参数。
 */
@Data
public class OrderCreateRequest {

    /**
     * 当前学习阶段暂时由请求传入。
     * 后续接入登录后，改为从登录上下文获取。
     */
    @NotNull(message = "用户id不能为空")
    @Positive(message = "用户id格式错误")
    private Long userId;

    /** 用户选择的票档id。 */
    @NotNull(message = "票档id不能为空")
    @Positive(message = "票档id格式错误")
    private Long ticketCategoryId;

    /** 单次购买数量，暂时限制为1到6张。 */
    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量不能小于1")
    @Max(value = 6, message = "单次最多购买6张")
    private Integer quantity;
}