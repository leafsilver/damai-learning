USE damai_learning;

CREATE TABLE IF NOT EXISTS d_program_session
(
    id                BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键id',
    program_id        BIGINT       NOT NULL COMMENT '节目id，逻辑关联d_program.id',
    session_name      VARCHAR(100) NOT NULL COMMENT '场次名称',
    show_time         DATETIME     NOT NULL COMMENT '演出时间',
    sale_start_time   DATETIME     NOT NULL COMMENT '开售时间',
    sale_end_time     DATETIME     NOT NULL COMMENT '停售时间',
    session_status    TINYINT      NOT NULL DEFAULT 1 COMMENT '场次状态：1启用，0停用',
    create_time       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    edit_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    status            TINYINT      NOT NULL DEFAULT 1 COMMENT '数据状态：1正常，0删除',
    PRIMARY KEY (id),
    KEY idx_program_status (program_id, status),
    KEY idx_show_time (show_time)
    ) ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COMMENT = '节目场次表';


CREATE TABLE IF NOT EXISTS d_ticket_category
(
    id                BIGINT         NOT NULL AUTO_INCREMENT COMMENT '主键id',
    session_id        BIGINT         NOT NULL COMMENT '场次id，逻辑关联d_program_session.id',
    category_name     VARCHAR(100)   NOT NULL COMMENT '票档名称',
    price             DECIMAL(10, 2) NOT NULL COMMENT '票价',
    total_stock       INT            NOT NULL COMMENT '总库存',
    available_stock   INT            NOT NULL COMMENT '可用库存',
    ticket_status     TINYINT        NOT NULL DEFAULT 1 COMMENT '票档状态：1启用，0停用',
    create_time       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    edit_time         DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    status            TINYINT        NOT NULL DEFAULT 1 COMMENT '数据状态：1正常，0删除',
    PRIMARY KEY (id),
    KEY idx_session_status (session_id, status)
    ) ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COMMENT = '票档表';


INSERT IGNORE INTO d_program_session
    (
        id,
        program_id,
        session_name,
        show_time,
        sale_start_time,
        sale_end_time
    )
VALUES
    (
        1,
        1,
        '2026年12月20日 19:30场',
        '2026-12-20 19:30:00',
        '2026-11-10 10:00:00',
        '2026-12-20 18:00:00'
    ),
    (
        2,
        1,
        '2026年12月21日 19:30场',
        '2026-12-21 19:30:00',
        '2026-11-10 10:00:00',
        '2026-12-21 18:00:00'
    ),
    (
        3,
        2,
        '2027年1月10日 19:30场',
        '2027-01-10 19:30:00',
        '2026-11-15 10:00:00',
        '2027-01-10 18:00:00'
    );


INSERT IGNORE INTO d_ticket_category
    (
        id,
        session_id,
        category_name,
        price,
        total_stock,
        available_stock
    )
VALUES
    (1, 1, '看台票', 380.00, 1000, 1000),
    (2, 1, '内场票', 880.00, 500, 500),
    (3, 1, 'VIP票', 1280.00, 100, 100),
    (4, 2, '看台票', 380.00, 1000, 1000),
    (5, 2, '内场票', 880.00, 500, 500),
    (6, 3, '普通票', 280.00, 800, 800);