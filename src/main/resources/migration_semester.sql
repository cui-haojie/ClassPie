-- 学期字段迁移（单独执行一次即可）
USE t_class;

ALTER TABLE courses ADD COLUMN semester VARCHAR(64) NULL COMMENT '学年学期';

UPDATE courses SET semester = '2024-2025 第一学期' WHERE semester IS NULL OR semester = '';
