package com.damai.learning.order.task;

import com.damai.learning.order.entity.TicketOrder;
import com.damai.learning.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时扫描并取消超时未支付订单。
 */
@Slf4j
@Component
public class OrderTimeoutTask {

    @Autowired
    private OrderService orderService;

    /**
     * 每次任务完成后，等待配置的时间再执行下一次扫描。
     */
    @Scheduled(fixedDelayString = "${order.timeout.scan-delay-ms:60000}")
    public void cancelExpiredOrders() {
        List<TicketOrder> expiredOrders =
                orderService.findExpiredPendingOrders(LocalDateTime.now());

        if (expiredOrders.isEmpty()) {
            return;
        }

        log.info("发现{}笔超时待支付订单", expiredOrders.size());

        for (TicketOrder ticketOrder : expiredOrders) {
            try {
                orderService.cancelExpiredOrder(ticketOrder.getId());
            } catch (Exception exception) {
                // 单笔失败只记录日志，不能阻止后续订单继续处理。
                log.error(
                        "自动取消超时订单失败，orderId={}，orderNo={}",
                        ticketOrder.getId(),
                        ticketOrder.getOrderNo(),
                        exception
                );
            }
        }
    }
}