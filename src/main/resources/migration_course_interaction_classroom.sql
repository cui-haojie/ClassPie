-- 课堂互动：实时问答 + 随机点名
USE t_class;

ALTER TABLE course_interaction_response
    ADD COLUMN round_num INT NOT NULL DEFAULT 1 COMMENT '问答轮次';

ALTER TABLE course_interaction_response
    DROP INDEX uk_activity_account;

ALTER TABLE course_interaction_response
    ADD UNIQUE KEY uk_activity_account_round (activity_id, account, round_num);

CREATE TABLE IF NOT EXISTS course_interaction_pick (
    id BIGINT NOT NULL AUTO_INCREMENT,
    activity_id BIGINT NOT NULL,
    account VARCHAR(64) NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_activity_id (activity_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
