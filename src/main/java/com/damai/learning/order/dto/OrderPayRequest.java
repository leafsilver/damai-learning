package com.damai.learning.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 模拟支付订单请求参数。
 */
@Data
public class OrderPayRequest {

    /**
     * 当前学习阶段暂时由请求传入。
     * 接入登录后改为从登录上下文获取。
     */
    @NotNull(message = "用户id不能为空")
    @Positive(message = "用户id格式错误")
    private Long userId;

    /** 需要支付的订单号。 */
    @NotBlank(message = "订单号不能为空")
    @Size(max = 64, message = "订单号长度不能超过64个字符")
    private String orderNo;
}