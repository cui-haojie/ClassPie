CREATE TABLE IF NOT EXISTS course_activity_reply (
    id BIGINT NOT NULL AUTO_INCREMENT,
    activity_id BIGINT NOT NULL COMMENT '关联 course_activity.id',
    account VARCHAR(64) NOT NULL,
    content TEXT NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_activity_id (activity_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
