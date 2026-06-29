CREATE TABLE IF NOT EXISTS course_attendance_session (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_id BIGINT NOT NULL,
    teacher_account VARCHAR(64) NOT NULL,
    code VARCHAR(8) NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'open',
    duration_minutes INT NOT NULL DEFAULT 5,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    close_time DATETIME NULL,
    INDEX idx_class_status (class_id, status)
);

CREATE TABLE IF NOT EXISTS course_attendance_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id BIGINT NOT NULL,
    account VARCHAR(64) NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'present',
    check_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_session_account (session_id, account),
    INDEX idx_session (session_id)
);
