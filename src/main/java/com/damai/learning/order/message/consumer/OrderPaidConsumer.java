package com.damai.learning.order.message.consumer;

import com.damai.learning.order.message.OrderTopic;
import com.damai.learning.order.message.entity.MessageConsumeRecord;
import com.damai.learning.order.message.mapper.MessageConsumeRecordMapper;
import com.damai.learning.order.message.model.OrderPaidMessage;
import com.damai.learning.ticket.service.ElectronicTicketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 订单支付成功消息消费者。
 */
@Slf4j
@Component
public class OrderPaidConsumer {

    @Autowired
    private MessageConsumeRecordMapper messageConsumeRecordMapper;

    @Autowired
    private ElectronicTicketService electronicTicketService;

    /**
     * 消费订单支付成功消息。
     */
    @Transactional
    @KafkaListener(
            topics = OrderTopic.ORDER_PAID
    )
    public void consume(OrderPaidMessage message) {
        MessageConsumeRecord consumeRecord =
                new MessageConsumeRecord();

        consumeRecord.setMessageId(
                message.getMessageId()
        );
        consumeRecord.setTopic(
                OrderTopic.ORDER_PAID
        );
        consumeRecord.setBusinessNo(
                message.getOrderNo()
        );

        try {
            messageConsumeRecordMapper.insert(
                    consumeRecord
            );
        } catch (DuplicateKeyException exception) {
            log.info(
                    "订单支付成功消息已处理，跳过重复消息，messageId={}，orderNo={}",
                    message.getMessageId(),
                    message.getOrderNo()
            );
            return;
        }

        electronicTicketService.issueTickets(
                message.getOrderNo()
        );

        log.info(
                "订单支付成功消息处理完成，messageId={}，orderNo={}，paidTime={}",
                message.getMessageId(),
                message.getOrderNo(),
                message.getPaidTime()
        );
    }
}
