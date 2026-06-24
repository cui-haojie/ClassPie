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
