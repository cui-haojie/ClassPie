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
