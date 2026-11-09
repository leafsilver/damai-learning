package com.damai.learning.order.message.producer;

import com.damai.learning.order.message.OrderTopic;
import com.damai.learning.order.message.model.OrderPaidMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * 订单支付成功消息生产者。
 */
@Slf4j
@Component
public class OrderPaidProducer {

    @Autowired
    private KafkaTemplate<String, OrderPaidMessage> kafkaTemplate;

    /**
     * 发送订单支付成功消息。
     */
    public void send(OrderPaidMessage message) {
        kafkaTemplate.send(
                OrderTopic.ORDER_PAID,
                message.getOrderNo(),
                message
        ).whenComplete((sendResult, exception) -> {
            if (exception != null) {
                log.error(
                        "订单支付成功消息发送失败，messageId={}，orderNo={}",
                        message.getMessageId(),
                        message.getOrderNo(),
                        exception
                );
                return;
            }

            log.info(
                    "订单支付成功消息发送成功，messageId={}，orderNo={}，partition={}，offset={}",
                    message.getMessageId(),
                    message.getOrderNo(),
                    sendResult.getRecordMetadata().partition(),
                    sendResult.getRecordMetadata().offset()
            );
        });
    }
}
