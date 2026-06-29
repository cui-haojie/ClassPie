-- 测试成绩：选择题自动批改，简答题教师手动评分
ALTER TABLE course_test_submission
    ADD COLUMN auto_score INT NOT NULL DEFAULT 0 COMMENT '选择题得分',
    ADD COLUMN manual_score INT NULL COMMENT '简答题得分',
    ADD COLUMN total_score INT NOT NULL DEFAULT 0 COMMENT '总得分',
    ADD COLUMN max_score INT NOT NULL DEFAULT 0 COMMENT '满分',
    ADD COLUMN is_fully_graded TINYINT NOT NULL DEFAULT 0 COMMENT '是否已全部批完';

ALTER TABLE course_test_answer
    ADD COLUMN score INT NULL COMMENT '该题得分',
    ADD COLUMN is_correct TINYINT NULL COMMENT '选择题是否正确',
    ADD COLUMN is_auto_graded TINYINT NOT NULL DEFAULT 0 COMMENT '是否自动批改';
