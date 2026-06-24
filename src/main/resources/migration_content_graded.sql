-- 作业提交批改状态
USE t_class;

ALTER TABLE content ADD COLUMN is_graded TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已批阅';

UPDATE content SET is_graded = 1 WHERE score IS NOT NULL AND score > 0;
