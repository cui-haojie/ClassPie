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

CREATE TABLE IF NOT EXISTS course_school_class (
    course_id BIGINT NOT NULL,
    school_class_id INT NOT NULL,
    PRIMARY KEY (course_id, school_class_id)
);
