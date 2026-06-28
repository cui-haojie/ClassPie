-- 测试草稿：draft=未发布，published=已发布
ALTER TABLE course_activity
    ADD COLUMN publish_status VARCHAR(16) NOT NULL DEFAULT 'published' COMMENT 'draft/published';

UPDATE course_activity SET publish_status = 'published' WHERE type = 'test' OR publish_status IS NULL;
