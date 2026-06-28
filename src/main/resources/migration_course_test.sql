-- 测试题目与答卷
CREATE TABLE IF NOT EXISTS course_test_question (
    id BIGINT NOT NULL AUTO_INCREMENT,
    activity_id BIGINT NOT NULL COMMENT '关联 course_activity.id，type=test',
    question_type VARCHAR(16) NOT NULL COMMENT 'choice/short',
    stem TEXT NOT NULL COMMENT '题干',
    option_a VARCHAR(512) NULL,
    option_b VARCHAR(512) NULL,
    option_c VARCHAR(512) NULL,
    option_d VARCHAR(512) NULL,
    correct_option CHAR(1) NULL COMMENT '选择题正确答案 A/B/C/D',
    score INT NOT NULL DEFAULT 5,
    sort_order INT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    INDEX idx_activity_id (activity_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS course_test_submission (
    id BIGINT NOT NULL AUTO_INCREMENT,
    activity_id BIGINT NOT NULL,
    account VARCHAR(64) NOT NULL,
    submit_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_activity_account (activity_id, account),
    INDEX idx_activity_id (activity_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS course_test_answer (
    id BIGINT NOT NULL AUTO_INCREMENT,
    submission_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    answer TEXT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_submission_question (submission_id, question_id),
    INDEX idx_submission_id (submission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
