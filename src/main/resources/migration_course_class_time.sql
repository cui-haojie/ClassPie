-- 上课时间支持填写多个时段（如：周一 1-2 节、周三 3-4 节）
USE t_class;
ALTER TABLE courses MODIFY COLUMN class_time VARCHAR(512) NOT NULL COMMENT '上课时间，可多段';
