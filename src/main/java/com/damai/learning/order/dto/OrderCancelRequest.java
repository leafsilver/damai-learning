package com.damai.learning.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 取消订单请求参数。
 */
@Data
public class OrderCancelRequest {

    /** 需要取消的订单号。 */
    @NotBlank(message = "订单号不能为空")
    @Size(max = 64, message = "订单号长度不能超过64个字符")
    private String orderNo;
}