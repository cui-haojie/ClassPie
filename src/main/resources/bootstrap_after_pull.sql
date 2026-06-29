-- 拉取最新代码后，在 MySQL t_class 库执行本脚本（可重复执行）
USE t_class;

CREATE TABLE IF NOT EXISTS teacher_prep_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    teacher_account VARCHAR(64) NOT NULL,
    kind VARCHAR(32) NOT NULL COMMENT 'homework/topic/material/announcement/test',
    title VARCHAR(255) NOT NULL,
    content TEXT NULL,
    attachment_url VARCHAR(512) NULL,
    attachment_name VARCHAR(255) NULL,
    meta_json TEXT NULL COMMENT 'kind-specific fields JSON',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_teacher_kind (teacher_account, kind)
);

CREATE TABLE IF NOT EXISTS teacher_prep_test_question (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    prep_item_id BIGINT NOT NULL,
    question_type VARCHAR(16) NOT NULL COMMENT 'choice/short',
    stem TEXT NOT NULL,
    option_a VARCHAR(512) NULL,
    option_b VARCHAR(512) NULL,
    option_c VARCHAR(512) NULL,
    option_d VARCHAR(512) NULL,
    correct_option CHAR(1) NULL,
    score INT NOT NULL DEFAULT 5,
    sort_order INT NOT NULL DEFAULT 0,
    stem_image_url VARCHAR(512) NULL COMMENT '题干配图',
    INDEX idx_prep_item (prep_item_id)
);

-- 若列已存在会报错，可忽略
ALTER TABLE teacher_prep_test_question ADD COLUMN stem_image_url VARCHAR(512) NULL COMMENT '题干配图';
ALTER TABLE course_test_question ADD COLUMN stem_image_url VARCHAR(512) NULL COMMENT '题干配图';
