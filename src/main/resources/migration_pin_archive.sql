-- 数据库迁移：调整 is_pinned 和 is_archived 字段位置
-- 执行前请备份数据库！
-- 说明：
--   - is_pinned 从 courses 移到 account_course（置顶是用户个人偏好）
--   - is_archived 从 account_course 移到 courses（归档是课程全局状态）

USE t_class;

-- 步骤 1：在 account_course 表中添加 is_pinned 列
ALTER TABLE account_course ADD COLUMN is_pinned TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否置顶（1-置顶，0-非置顶）';

-- 步骤 2：将 courses.is_pinned 的数据迁移到 account_course.is_pinned
-- 注意：courses 中的 is_pinned 是针对老师的，所以只迁移老师账户的置顶状态
UPDATE account_course ac
INNER JOIN courses c ON ac.class_id = c.id AND ac.account = c.teacher_account
SET ac.is_pinned = c.is_pinned;

-- 步骤 3：从 courses 表中删除 is_pinned 列
ALTER TABLE courses DROP COLUMN is_pinned;

-- 步骤 4：在 courses 表中添加 is_archived 列
ALTER TABLE courses ADD COLUMN is_archived TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否归档（1-归档，0-未归档）';

-- 步骤 5：将 account_course.is_archived 的数据迁移到 courses.is_archived
-- 注意：归档是全局的，如果任何一个用户归档了课程，课程就应该被标记为归档
UPDATE courses c
SET c.is_archived = 1
WHERE EXISTS (
    SELECT 1 FROM account_course ac
    WHERE ac.class_id = c.id AND ac.is_archived = 1
);

-- 步骤 6：从 account_course 表中删除 is_archived 列
ALTER TABLE account_course DROP COLUMN is_archived;

-- 验证迁移结果
SELECT '迁移后 courses 表结构：' AS info;
DESCRIBE courses;

SELECT '迁移后 account_course 表结构：' AS info;
DESCRIBE account_course;

SELECT 'courses 表中的归档状态：' AS info;
SELECT id, class_name, is_archived FROM courses;

SELECT 'account_course 表中的置顶状态：' AS info;
SELECT account, class_id, is_pinned FROM account_course WHERE is_pinned = 1;