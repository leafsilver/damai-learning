CREATE DATABASE IF NOT EXISTS damai_learning
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE damai_learning;

CREATE TABLE IF NOT EXISTS d_program
(
    id             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键id',
    title          VARCHAR(200) NOT NULL COMMENT '节目标题',
    actor          VARCHAR(100)          DEFAULT NULL COMMENT '艺人',
    place          VARCHAR(100) NOT NULL COMMENT '演出地点',
    item_picture   VARCHAR(500)          DEFAULT NULL COMMENT '节目图片',
    program_status TINYINT      NOT NULL DEFAULT 1 COMMENT '节目状态：1上架，0下架',
    create_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    edit_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    status         TINYINT      NOT NULL DEFAULT 1 COMMENT '数据状态：1正常，0删除',
    PRIMARY KEY (id),
    KEY idx_program_status (program_status),
    KEY idx_create_time (create_time)
    ) ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COMMENT = '节目表';

INSERT IGNORE INTO d_program
    (id, title, actor, place, program_status)
VALUES
    (1, '周杰伦演唱会', '周杰伦', '北京体育馆', 1),
    (2, '经典音乐剧演出', NULL, '上海大剧院', 1);