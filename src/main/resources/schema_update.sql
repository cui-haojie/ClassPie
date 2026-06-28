-- 在 MySQL 数据库 t_class 中执行（仅需执行一次）
USE t_class;

ALTER TABLE account_course ADD COLUMN is_archived TINYINT NOT NULL DEFAULT 0;

CREATE TABLE IF NOT EXISTS notification (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account VARCHAR(64) NOT NULL,
    class_id INT NULL,
    homework_id INT NULL,
    type VARCHAR(32) NOT NULL,
    message VARCHAR(512) NOT NULL,
    is_read TINYINT NOT NULL DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS school_class (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    mechanism VARCHAR(128) NULL,
    teacher_account VARCHAR(64) NULL
);

CREATE TABLE IF NOT EXISTS student_class (
    account VARCHAR(64) NOT NULL,
    school_class_id INT NOT NULL,
    PRIMARY KEY (account, school_class_id)
);

ALTER TABLE courses ADD COLUMN school_class_id INT NULL;

ALTER TABLE courses ADD COLUMN semester VARCHAR(64) NULL COMMENT '学年学期';

UPDATE courses SET semester = '2024-2025 第一学期' WHERE semester IS NULL OR semester = '';

ALTER TABLE account_course ADD COLUMN sort_order INT NOT NULL DEFAULT 0;

UPDATE account_course SET sort_order = class_id WHERE sort_order = 0;

ALTER TABLE accounts ADD COLUMN avatar_url VARCHAR(512) NULL COMMENT '头像路径';

ALTER TABLE content ADD COLUMN attachment_url VARCHAR(512) NULL COMMENT '附件路径';
ALTER TABLE content ADD COLUMN attachment_name VARCHAR(255) NULL COMMENT '附件原名';

ALTER TABLE content ADD COLUMN is_graded TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已批阅';
UPDATE content SET is_graded = 1 WHERE score IS NOT NULL AND score > 0;

CREATE TABLE IF NOT EXISTS course_school_class (
    course_id BIGINT NOT NULL,
    school_class_id INT NOT NULL,
    PRIMARY KEY (course_id, school_class_id)
);

CREATE TABLE IF NOT EXISTS course_activity (
    id BIGINT NOT NULL AUTO_INCREMENT,
    class_id BIGINT NOT NULL COMMENT '课程 ID',
    type VARCHAR(32) NOT NULL COMMENT 'interaction/topic/material/test/announcement',
    title VARCHAR(255) NOT NULL,
    content TEXT NULL,
    attachment_url VARCHAR(512) NULL,
    attachment_name VARCHAR(255) NULL,
    deadline DATETIME NULL,
    creator_account VARCHAR(64) NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_class_type (class_id, type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS course_activity_reply (
    id BIGINT NOT NULL AUTO_INCREMENT,
    activity_id BIGINT NOT NULL COMMENT '关联 course_activity.id',
    account VARCHAR(64) NOT NULL,
    content TEXT NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_activity_id (activity_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE courses MODIFY COLUMN class_time VARCHAR(512) NOT NULL COMMENT '上课时间，可多段';

ALTER TABLE course_activity_reply
    ADD COLUMN attachment_url VARCHAR(512) NULL COMMENT '回复附件路径',
    ADD COLUMN attachment_name VARCHAR(255) NULL COMMENT '回复附件原名';

ALTER TABLE homework
    ADD COLUMN attachment_url VARCHAR(512) NULL COMMENT '作业附件路径',
    ADD COLUMN attachment_name VARCHAR(255) NULL COMMENT '作业附件原名';

ALTER TABLE course_activity
    ADD COLUMN start_time DATETIME NULL COMMENT '测试开始时间';

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

ALTER TABLE course_activity
    ADD COLUMN publish_status VARCHAR(16) NOT NULL DEFAULT 'published' COMMENT 'draft/published';
