USE damai_learning;

CREATE TABLE IF NOT EXISTS d_order
(
    id                   BIGINT         NOT NULL AUTO_INCREMENT COMMENT '主键id',
    order_no             VARCHAR(64)    NOT NULL COMMENT '订单号',
    user_id              BIGINT         NOT NULL COMMENT '用户id',
    program_id           BIGINT         NOT NULL COMMENT '节目id',
    session_id           BIGINT         NOT NULL COMMENT '场次id',
    ticket_category_id   BIGINT         NOT NULL COMMENT '票档id',
    quantity             INT            NOT NULL COMMENT '购买数量',
    unit_price           DECIMAL(10, 2) NOT NULL COMMENT '下单时单价',
    total_amount         DECIMAL(12, 2) NOT NULL COMMENT '订单总金额',
    order_status         TINYINT        NOT NULL DEFAULT 1
        COMMENT '订单状态：1待支付，2已支付，3已取消',
    create_time          DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP
        COMMENT '创建时间',
    edit_time            DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    status               TINYINT        NOT NULL DEFAULT 1
        COMMENT '数据状态：1正常，0删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_user_status (user_id, order_status),
    KEY idx_ticket_category_id (ticket_category_id),
    KEY idx_create_time (create_time)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '订单表';
