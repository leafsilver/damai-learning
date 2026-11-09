USE damai_learning;
CREATE TABLE IF NOT EXISTS d_message_consume_record
(
    id            BIGINT       NOT NULL AUTO_INCREMENT
        COMMENT '主键id',
    message_id    VARCHAR(64)  NOT NULL
        COMMENT '消息唯一标识',
    topic         VARCHAR(100) NOT NULL
        COMMENT '消息Topic',
    business_no   VARCHAR(64)  NOT NULL
        COMMENT '业务编号',
    consume_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
        COMMENT '消费时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_message_id (message_id),
    KEY idx_business_no (business_no)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '消息消费记录表';
