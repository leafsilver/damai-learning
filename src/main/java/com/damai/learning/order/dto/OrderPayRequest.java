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
    
    /** 需要支付的订单号。 */
    @NotBlank(message = "订单号不能为空")
    @Size(max = 64, message = "订单号长度不能超过64个字符")
    private String orderNo;
}