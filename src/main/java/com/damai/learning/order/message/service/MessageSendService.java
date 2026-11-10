package com.damai.learning.order.message.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.damai.learning.order.message.entity.MessageSendRecord;
import com.damai.learning.order.message.enums.MessageSendStatus;
import com.damai.learning.order.message.mapper.MessageSendRecordMapper;
import com.damai.learning.order.message.model.OrderPaidMessage;
import com.damai.learning.order.message.producer.OrderPaidProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 消息发送服务，负责查询并发送待发送消息。
 */
@Slf4j
@Service
public class MessageSendService {

    @Autowired
    private MessageSendRecordMapper messageSendRecordMapper;

    @Autowired
    private OrderPaidProducer orderPaidProducer;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 分批查询待发送消息。
     */
    public List<MessageSendRecord> findPendingMessages() {
        LambdaQueryWrapper<MessageSendRecord> queryWrapper =
                new LambdaQueryWrapper<>();

        queryWrapper
                .eq(
                        MessageSendRecord::getSendStatus,
                        MessageSendStatus.PENDING.getCode()
                )
                .orderByAsc(MessageSendRecord::getCreateTime)
                .last("LIMIT 20");

        return messageSendRecordMapper.selectList(queryWrapper);
    }

    /**
     * 发送一条待发送消息。
     */
    public void sendMessage(MessageSendRecord sendRecord) {
        try {
            OrderPaidMessage message = objectMapper.readValue(
                    sendRecord.getMessageBody(),
                    OrderPaidMessage.class
            );

            SendResult<String, OrderPaidMessage> sendResult =
                    orderPaidProducer.send(
                            sendRecord.getTopic(),
                            sendRecord.getMessageKey(),
                            message
                    ).get(10, TimeUnit.SECONDS);

            markAsSent(sendRecord.getId());

            log.info(
                    "订单支付消息发送成功，messageId={}，partition={}，offset={}",
                    sendRecord.getMessageId(),
                    sendResult.getRecordMetadata().partition(),
                    sendResult.getRecordMetadata().offset()
            );
        } catch (InterruptedException exception) {
            // 恢复线程中断状态，避免吞掉中断信号。
            Thread.currentThread().interrupt();
            markAsFailed(sendRecord.getId(), exception);

            log.error(
                    "订单支付消息发送线程被中断，messageId={}",
                    sendRecord.getMessageId(),
                    exception
            );
        } catch (Exception exception) {
            markAsFailed(sendRecord.getId(), exception);

            log.error(
                    "订单支付消息发送失败，messageId={}",
                    sendRecord.getMessageId(),
                    exception
            );
        }
    }

    /**
     * 将消息记录更新为已发送。
     */
    private void markAsSent(Long recordId) {
        LambdaUpdateWrapper<MessageSendRecord> updateWrapper =
                new LambdaUpdateWrapper<>();

        updateWrapper
                .eq(MessageSendRecord::getId, recordId)
                .eq(
                        MessageSendRecord::getSendStatus,
                        MessageSendStatus.PENDING.getCode()
                )
                .set(
                        MessageSendRecord::getSendStatus,
                        MessageSendStatus.SENT.getCode()
                )
                .set(MessageSendRecord::getSendTime, LocalDateTime.now())
                .set(MessageSendRecord::getLastError, null);

        messageSendRecordMapper.update(null, updateWrapper);
    }

    /**
     * 记录发送失败信息，等待下一次定时任务重试。
     */
    private void markAsFailed(
            Long recordId,
            Exception exception
    ) {
        LambdaUpdateWrapper<MessageSendRecord> updateWrapper =
                new LambdaUpdateWrapper<>();

        updateWrapper
                .eq(MessageSendRecord::getId, recordId)
                .eq(
                        MessageSendRecord::getSendStatus,
                        MessageSendStatus.PENDING.getCode()
                )
                .setSql("retry_count = retry_count + 1")
                .set(
                        MessageSendRecord::getLastError,
                        getErrorMessage(exception)
                );

        messageSendRecordMapper.update(null, updateWrapper);
    }

    /**
     * 截取可存入数据库的异常信息。
     */
    private String getErrorMessage(Exception exception) {
        Throwable actualException = exception.getCause() == null
                ? exception
                : exception.getCause();

        String errorMessage = actualException.getMessage();

        if (errorMessage == null || errorMessage.isBlank()) {
            errorMessage = actualException.getClass().getSimpleName();
        }

        return errorMessage.length() <= 500
                ? errorMessage
                : errorMessage.substring(0, 500);
    }
}