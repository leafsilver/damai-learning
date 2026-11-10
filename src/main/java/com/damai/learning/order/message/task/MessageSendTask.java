package com.damai.learning.order.message.task;

import com.damai.learning.order.message.entity.MessageSendRecord;
import com.damai.learning.order.message.service.MessageSendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 定时发送本地消息表中的待发送消息。
 */
@Slf4j
@Component
public class MessageSendTask {

    @Autowired
    private MessageSendService messageSendService;

    /**
     * 每次任务执行完成后，等待配置的时间再扫描。
     */
    @Scheduled(
            fixedDelayString =
                    "${order.message.send.scan-delay-ms:5000}"
    )
    public void sendPendingMessages() {
        List<MessageSendRecord> pendingMessages =
                messageSendService.findPendingMessages();

        if (pendingMessages.isEmpty()) {
            return;
        }

        log.info(
                "发现{}条待发送消息",
                pendingMessages.size()
        );

        for (MessageSendRecord sendRecord : pendingMessages) {
            messageSendService.sendMessage(sendRecord);
        }
    }
}