package com.damai.learning.order.message.consumer;

import com.damai.learning.order.message.OrderTopic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 订单支付成功消息消费者。
 */
@Slf4j
@Component
public class OrderPaidConsumer {

    /**
     * 消费订单支付成功消息。
     */
    @KafkaListener(
            topics = OrderTopic.ORDER_PAID
    )
    public void consume(String orderNo) {
        log.info(
                "收到订单支付成功消息，orderNo={}",
                orderNo
        );
    }
}