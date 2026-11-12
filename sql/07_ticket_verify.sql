USE damai_learning;

ALTER TABLE d_electronic_ticket
    ADD COLUMN verify_time DATETIME DEFAULT NULL
        COMMENT '验票时间'
        AFTER ticket_status;
