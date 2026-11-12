USE damai_learning;

CREATE TABLE IF NOT EXISTS d_electronic_ticket
(
    id                   BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键id',
    ticket_no            VARCHAR(64) NOT NULL COMMENT '电子票号',
    order_no             VARCHAR(64) NOT NULL COMMENT '订单号',
    ticket_index         INT         NOT NULL COMMENT '订单中的第几张票',
    user_id              BIGINT      NOT NULL COMMENT '购票用户id',
    program_id           BIGINT      NOT NULL COMMENT '节目id',
    session_id           BIGINT      NOT NULL COMMENT '场次id',
    ticket_category_id   BIGINT      NOT NULL COMMENT '票档id',
    ticket_status        TINYINT     NOT NULL DEFAULT 1
    COMMENT '电子票状态：1有效，2已使用，3已作废',
    create_time          DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP
    COMMENT '创建时间',
    edit_time            DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_ticket_no (ticket_no),
    UNIQUE KEY uk_order_ticket_index (order_no, ticket_index),
    KEY idx_user_id (user_id),
    KEY idx_session_id (session_id)
    ) ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COMMENT = '电子票表';