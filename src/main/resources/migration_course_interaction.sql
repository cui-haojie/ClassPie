-- 课程互动：投票 / 意见征集
ALTER TABLE course_activity
    ADD COLUMN interaction_kind VARCHAR(16) NULL COMMENT 'vote/opinion',
    ADD COLUMN interaction_options TEXT NULL COMMENT '投票选项 JSON 数组';

CREATE TABLE IF NOT EXISTS course_interaction_response (
    id BIGINT NOT NULL AUTO_INCREMENT,
    activity_id BIGINT NOT NULL,
    account VARCHAR(64) NOT NULL,
    option_index INT NULL COMMENT '投票选项下标，从 0 开始',
    content TEXT NULL COMMENT '意见内容',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_activity_account (activity_id, account),
    INDEX idx_activity_id (activity_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
