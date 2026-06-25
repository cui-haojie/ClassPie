-- 仿真学生数据：为每门课程插入 12 名学生（账号密码均为 ClassPi123）
-- 正确导入方式（勿用 PowerShell 管道，会乱码）：
--   mysql -u root -p --default-character-set=utf8mb4 t_class < seed_mock_students.sql
-- 或在 mysql 客户端内： source /path/to/seed_mock_students.sql
USE t_class;

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 清理此前导入失败的乱码数据
DELETE FROM account_course WHERE account LIKE 'sim%';
DELETE FROM student_class WHERE account LIKE 'sim%';
DELETE FROM accounts WHERE account LIKE 'sim%';

-- ========== 课程 123 springboot ==========
INSERT INTO accounts (account, name, status, password, mechanism, email_or_phone, status_number) VALUES
('sim123_01@stu.cqut.edu.cn', '张明', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010101'),
('sim123_02@stu.cqut.edu.cn', '李思琪', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010102'),
('sim123_03@stu.cqut.edu.cn', '王浩然', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010103'),
('sim123_04@stu.cqut.edu.cn', '刘雨萱', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010104'),
('sim123_05@stu.cqut.edu.cn', '陈宇航', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010105'),
('sim123_06@stu.cqut.edu.cn', '杨欣怡', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010106'),
('sim123_07@stu.cqut.edu.cn', '赵子涵', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010107'),
('sim123_08@stu.cqut.edu.cn', '黄俊杰', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010108'),
('sim123_09@stu.cqut.edu.cn', '周晓彤', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010109'),
('sim123_10@stu.cqut.edu.cn', '吴志远', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010110'),
('sim123_11@stu.cqut.edu.cn', '徐梦洁', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010111'),
('sim123_12@stu.cqut.edu.cn', '孙嘉豪', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010112');

INSERT INTO account_course (account, class_id, is_archived, sort_order)
SELECT account, 123, 0, 0 FROM accounts WHERE account LIKE 'sim123_%';

-- ========== 课程 234 vue3 ==========
INSERT INTO accounts (account, name, status, password, mechanism, email_or_phone, status_number) VALUES
('sim234_01@stu.cqut.edu.cn', '马文博', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010201'),
('sim234_02@stu.cqut.edu.cn', '朱诗涵', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010202'),
('sim234_03@stu.cqut.edu.cn', '胡天宇', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010203'),
('sim234_04@stu.cqut.edu.cn', '郭佳怡', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010204'),
('sim234_05@stu.cqut.edu.cn', '何俊熙', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010205'),
('sim234_06@stu.cqut.edu.cn', '高雨桐', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010206'),
('sim234_07@stu.cqut.edu.cn', '林梓轩', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010207'),
('sim234_08@stu.cqut.edu.cn', '罗心怡', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010208'),
('sim234_09@stu.cqut.edu.cn', '梁子墨', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010209'),
('sim234_10@stu.cqut.edu.cn', '宋语嫣', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010210'),
('sim234_11@stu.cqut.edu.cn', '郑浩宇', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010211'),
('sim234_12@stu.cqut.edu.cn', '谢若曦', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010212');

INSERT INTO account_course (account, class_id, is_archived, sort_order)
SELECT account, 234, 0, 0 FROM accounts WHERE account LIKE 'sim234_%';

-- ========== 课程 345 html ==========
INSERT INTO accounts (account, name, status, password, mechanism, email_or_phone, status_number) VALUES
('sim345_01@stu.cqut.edu.cn', '唐一鸣', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010301'),
('sim345_02@stu.cqut.edu.cn', '韩雪儿', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010302'),
('sim345_03@stu.cqut.edu.cn', '冯子骞', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010303'),
('sim345_04@stu.cqut.edu.cn', '邓雅婷', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010304'),
('sim345_05@stu.cqut.edu.cn', '曹瑞泽', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010305'),
('sim345_06@stu.cqut.edu.cn', '彭思远', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010306'),
('sim345_07@stu.cqut.edu.cn', '曾可欣', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010307'),
('sim345_08@stu.cqut.edu.cn', '萧俊豪', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010308'),
('sim345_09@stu.cqut.edu.cn', '田雨欣', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010309'),
('sim345_10@stu.cqut.edu.cn', '董文轩', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010310'),
('sim345_11@stu.cqut.edu.cn', '潘诗雅', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010311'),
('sim345_12@stu.cqut.edu.cn', '袁梓豪', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010312');

INSERT INTO account_course (account, class_id, is_archived, sort_order)
SELECT account, 345, 0, 0 FROM accounts WHERE account LIKE 'sim345_%';

-- ========== 课程 346 css ==========
INSERT INTO accounts (account, name, status, password, mechanism, email_or_phone, status_number) VALUES
('sim346_01@stu.cqut.edu.cn', '蔡明轩', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010401'),
('sim346_02@stu.cqut.edu.cn', '蒋欣妍', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010402'),
('sim346_03@stu.cqut.edu.cn', '沈子豪', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010403'),
('sim346_04@stu.cqut.edu.cn', '卢雨菲', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010404'),
('sim346_05@stu.cqut.edu.cn', '汪天佑', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010405'),
('sim346_06@stu.cqut.edu.cn', '范思琪', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010406'),
('sim346_07@stu.cqut.edu.cn', '金浩然', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010407'),
('sim346_08@stu.cqut.edu.cn', '陆晓涵', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010408'),
('sim346_09@stu.cqut.edu.cn', '崔博文', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010409'),
('sim346_10@stu.cqut.edu.cn', '姚佳慧', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010410'),
('sim346_11@stu.cqut.edu.cn', '谭俊宇', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010411'),
('sim346_12@stu.cqut.edu.cn', '邹雨晴', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010412');

INSERT INTO account_course (account, class_id, is_archived, sort_order)
SELECT account, 346, 0, 0 FROM accounts WHERE account LIKE 'sim346_%';

-- ========== 课程 347 java ==========
INSERT INTO accounts (account, name, status, password, mechanism, email_or_phone, status_number) VALUES
('sim347_01@stu.cqut.edu.cn', '石磊', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010501'),
('sim347_02@stu.cqut.edu.cn', '姜雨萌', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010502'),
('sim347_03@stu.cqut.edu.cn', '戴子轩', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010503'),
('sim347_04@stu.cqut.edu.cn', '夏语桐', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010504'),
('sim347_05@stu.cqut.edu.cn', '钟宇航', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010505'),
('sim347_06@stu.cqut.edu.cn', '汪思涵', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010506'),
('sim347_07@stu.cqut.edu.cn', '任嘉乐', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010507'),
('sim347_08@stu.cqut.edu.cn', '白若溪', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010508'),
('sim347_09@stu.cqut.edu.cn', '邱俊凯', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010509'),
('sim347_10@stu.cqut.edu.cn', '秦雨萱', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010510'),
('sim347_11@stu.cqut.edu.cn', '江子涵', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010511'),
('sim347_12@stu.cqut.edu.cn', '阎思远', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010512');

INSERT INTO account_course (account, class_id, is_archived, sort_order)
SELECT account, 347, 0, 0 FROM accounts WHERE account LIKE 'sim347_%';

-- ========== 课程 348 计算机组成（关联软工2024 / 计科2024） ==========
INSERT INTO accounts (account, name, status, password, mechanism, email_or_phone, status_number) VALUES
('sim348_01@stu.cqut.edu.cn', '方文博', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010601'),
('sim348_02@stu.cqut.edu.cn', '石佳怡', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010602'),
('sim348_03@stu.cqut.edu.cn', '熊子豪', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010603'),
('sim348_04@stu.cqut.edu.cn', '孟雨欣', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010604'),
('sim348_05@stu.cqut.edu.cn', '秦浩宇', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010605'),
('sim348_06@stu.cqut.edu.cn', '顾思琪', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010606'),
('sim348_07@stu.cqut.edu.cn', '侯俊熙', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010607'),
('sim348_08@stu.cqut.edu.cn', '邵语嫣', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010608'),
('sim348_09@stu.cqut.edu.cn', '龙梓轩', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010609'),
('sim348_10@stu.cqut.edu.cn', '万心怡', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010610'),
('sim348_11@stu.cqut.edu.cn', '段文轩', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010611'),
('sim348_12@stu.cqut.edu.cn', '雷诗涵', '学生', 'ClassPi123', '重庆理工大学', 'yes', '2024010612');

INSERT INTO account_course (account, class_id, is_archived, sort_order)
SELECT account, 348, 0, 0 FROM accounts WHERE account LIKE 'sim348_%';

-- 前 6 人归属软工2024，后 6 人归属计科2024
INSERT INTO student_class (account, school_class_id)
SELECT account, 1 FROM accounts WHERE account IN (
    'sim348_01@stu.cqut.edu.cn','sim348_02@stu.cqut.edu.cn','sim348_03@stu.cqut.edu.cn',
    'sim348_04@stu.cqut.edu.cn','sim348_05@stu.cqut.edu.cn','sim348_06@stu.cqut.edu.cn'
);
INSERT INTO student_class (account, school_class_id)
SELECT account, 2 FROM accounts WHERE account IN (
    'sim348_07@stu.cqut.edu.cn','sim348_08@stu.cqut.edu.cn','sim348_09@stu.cqut.edu.cn',
    'sim348_10@stu.cqut.edu.cn','sim348_11@stu.cqut.edu.cn','sim348_12@stu.cqut.edu.cn'
);
