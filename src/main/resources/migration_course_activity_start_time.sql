-- 测试起止时间：start_time 为开始，deadline 为结束
USE t_class;

ALTER TABLE course_activity
    ADD COLUMN start_time DATETIME NULL COMMENT '测试开始时间' AFTER attachment_name;
