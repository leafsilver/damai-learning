package com.damai.learning.program.message;

/**
 * 节目相关Kafka Topic。
 */
public final class ProgramTopic {

    /** 节目数据变更Topic。 */
    public static final String PROGRAM_CHANGED =
            "damai-program-changed";

    private ProgramTopic() {
    }
}