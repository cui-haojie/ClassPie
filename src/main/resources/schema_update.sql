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
