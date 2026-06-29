-- 课程成绩权重配置（四项之和应为 100）
CREATE TABLE IF NOT EXISTS course_grade_weight (
    course_id BIGINT NOT NULL PRIMARY KEY,
    homework_weight INT NOT NULL DEFAULT 35,
    test_weight INT NOT NULL DEFAULT 35,
    attendance_weight INT NOT NULL DEFAULT 20,
    interaction_weight INT NOT NULL DEFAULT 10,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
