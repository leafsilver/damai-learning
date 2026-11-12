package com.damai.learning.program.message.producer;

import com.damai.learning.program.message.ProgramTopic;
import com.damai.learning.program.message.model.ProgramChangedMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 节目数据变更消息生产者。
 */
@Slf4j
@Component
public class ProgramChangedProducer {

    @Autowired
    private KafkaTemplate<String, ProgramChangedMessage>
            kafkaTemplate;

    /**
     * MySQL事务提交成功后发送节目变更消息。
     */
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void send(ProgramChangedMessage message) {
        String messageKey =
                message.getProgramId().toString();

        kafkaTemplate.send(
                ProgramTopic.PROGRAM_CHANGED,
                messageKey,
                message
        ).whenComplete((sendResult, exception) -> {
            if (exception != null) {
                log.error(
                        "节目变更消息发送失败，programId={}",
                        message.getProgramId(),
                        exception
                );
                return;
            }

            log.info(
                    "节目变更消息发送成功，programId={}，partition={}，offset={}",
                    message.getProgramId(),
                    sendResult.getRecordMetadata().partition(),
                    sendResult.getRecordMetadata().offset()
            );
        });
    }
}