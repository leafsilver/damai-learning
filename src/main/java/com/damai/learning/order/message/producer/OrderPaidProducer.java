package com.damai.learning.order.message.producer;

import com.damai.learning.order.message.model.OrderPaidMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * 订单支付成功消息生产者。
 */
@Component
public class OrderPaidProducer {

    @Autowired
    private KafkaTemplate<String, OrderPaidMessage> kafkaTemplate;

    /**
     * 发送订单支付成功消息，并返回异步发送结果。
     */
    public CompletableFuture<SendResult<String, OrderPaidMessage>> send(
            String topic,
            String messageKey,
            OrderPaidMessage message
    ) {
        return kafkaTemplate.send(
                topic,
                messageKey,
                message
        );
    }
}