package com.damai.learning.program.message.consumer;

import com.damai.learning.program.message.ProgramTopic;
import com.damai.learning.program.message.model.ProgramChangedMessage;
import com.damai.learning.program.service.ProgramSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 节目数据变更消息消费者。
 */
@Slf4j
@Component
public class ProgramChangedConsumer {

    @Autowired
    private ProgramSearchService programSearchService;

    /**
     * 消费节目数据变更消息并同步ES。
     */
    @KafkaListener(
            topics = ProgramTopic.PROGRAM_CHANGED,
            groupId = "damai-learning-program-search"
    )
    public void consume(ProgramChangedMessage message) {
        programSearchService.syncProgram(
                message.getProgramId()
        );

        log.info(
                "节目数据已同步到ES，programId={}",
                message.getProgramId()
        );
    }
}