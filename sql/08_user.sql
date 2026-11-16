USE damai_learning;

CREATE TABLE IF NOT EXISTS d_user
(
    id          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键id',
    username    VARCHAR(50) NOT NULL COMMENT '登录用户名',
    password    VARCHAR(100) NOT NULL COMMENT '密码哈希',
    nickname    VARCHAR(50) NOT NULL COMMENT '用户昵称',
    user_status TINYINT     NOT NULL DEFAULT 1
    COMMENT '用户状态：1正常，0禁用',
    create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP
    COMMENT '创建时间',
    edit_time   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    status      TINYINT     NOT NULL DEFAULT 1
    COMMENT '数据状态：1正常，0删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    KEY idx_user_status (user_status, status)
    ) ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COMMENT = '用户表';