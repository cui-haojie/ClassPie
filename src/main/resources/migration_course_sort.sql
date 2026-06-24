-- 课程拖拽排序（按用户 account_course.sort_order）
USE t_class;

ALTER TABLE account_course ADD COLUMN sort_order INT NOT NULL DEFAULT 0;

UPDATE account_course SET sort_order = class_id WHERE sort_order = 0;
