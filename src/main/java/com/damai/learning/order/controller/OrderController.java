package com.damai.learning.order.controller;

import com.damai.learning.common.result.Result;
import com.damai.learning.order.dto.OrderCancelRequest;
import com.damai.learning.order.dto.OrderCreateRequest;
import com.damai.learning.order.dto.OrderPayRequest;
import com.damai.learning.order.service.OrderService;
import com.damai.learning.order.vo.OrderCreateVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单接口。
 */
@Tag(name = "订单接口")
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Operation(summary = "创建待支付订单")
    @PostMapping("/create")
    public Result<OrderCreateVO> create(@Valid @RequestBody OrderCreateRequest request) {
        return Result.success(orderService.createOrder(request));
    }

    @Operation(summary = "取消待支付订单")
    @PutMapping("/cancel")
    public Result<Void> cancel(@Valid @RequestBody OrderCancelRequest request) {
        orderService.cancelOrder(request);
        return Result.success();
    }

    @Operation(summary = "模拟支付订单")
    @PutMapping("/pay")
    public Result<Void> pay(@Valid @RequestBody OrderPayRequest request) {
        orderService.payOrder(request);
        return Result.success();
    }
}
