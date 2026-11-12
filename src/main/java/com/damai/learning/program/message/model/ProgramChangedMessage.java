package com.damai.learning.program.message.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 节目数据变更消息。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramChangedMessage {

    /** 发生变化的节目id。 */
    private Long programId;
}