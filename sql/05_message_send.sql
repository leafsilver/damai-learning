USE damai_learning;

CREATE TABLE IF NOT EXISTS d_message_send_record
(
    id            BIGINT       NOT NULL AUTO_INCREMENT
    COMMENT '主键id',
    message_id    VARCHAR(64)  NOT NULL
    COMMENT '消息唯一标识',
    topic         VARCHAR(100) NOT NULL
    COMMENT '消息Topic',
    message_key   VARCHAR(100) NOT NULL
    COMMENT '消息Key',
    message_body  TEXT         NOT NULL
    COMMENT '消息JSON内容',
    send_status   TINYINT      NOT NULL DEFAULT 0
    COMMENT '发送状态：0待发送，1已发送',
    retry_count   INT          NOT NULL DEFAULT 0
    COMMENT '重试次数',
    last_error    VARCHAR(500)          DEFAULT NULL
    COMMENT '最近一次发送失败原因',
    send_time     DATETIME              DEFAULT NULL
    COMMENT '发送成功时间',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
    COMMENT '创建时间',
    edit_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_message_id (message_id),
    KEY idx_send_status_create_time (send_status, create_time)
    ) ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COMMENT = '消息发送记录表';