/*
 Navicat Premium Dump SQL

 Source Server         : root
 Source Server Type    : MySQL
 Source Server Version : 80041 (8.0.41)
 Source Host           : localhost:3306
 Source Schema         : t_class

 Target Server Type    : MySQL
 Target Server Version : 80041 (8.0.41)
 File Encoding         : 65001

 Date: 23/06/2026 15:40:58
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for account_course
-- ----------------------------
DROP TABLE IF EXISTS `account_course`;
CREATE TABLE `account_course`  (
  `account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `class_id` bigint NOT NULL,
  `is_archived` tinyint NOT NULL DEFAULT 0,
  PRIMARY KEY (`account`, `class_id`) USING BTREE,
  INDEX `account_course_ibfk_2`(`class_id` ASC) USING BTREE,
  CONSTRAINT `account_course_ibfk_1` FOREIGN KEY (`account`) REFERENCES `accounts` (`account`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `account_course_ibfk_2` FOREIGN KEY (`class_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of account_course
-- ----------------------------
INSERT INTO `account_course` VALUES ('12423020124@stu.cqut.edu.cn', 123, 0);
INSERT INTO `account_course` VALUES ('12423020124@stu.cqut.edu.cn', 234, 0);
INSERT INTO `account_course` VALUES ('12423020124@stu.cqut.edu.cn', 346, 0);
INSERT INTO `account_course` VALUES ('12423020124@stu.cqut.edu.cn', 347, 0);
INSERT INTO `account_course` VALUES ('2782314722@qq.com', 123, 0);
INSERT INTO `account_course` VALUES ('2782314722@qq.com', 234, 0);
INSERT INTO `account_course` VALUES ('2782314722@qq.com', 345, 0);
INSERT INTO `account_course` VALUES ('2782314722@qq.com', 346, 0);
INSERT INTO `account_course` VALUES ('2782314722@qq.com', 348, 0);

-- ----------------------------
-- Table structure for accounts
-- ----------------------------
DROP TABLE IF EXISTS `accounts`;
CREATE TABLE `accounts`  (
  `account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '账户（电话/邮箱/账号）',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `status` enum('老师','学生') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '身份',
  `password` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `mechanism` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '机构名',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `email_or_phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '除账户外的另一条信息',
  `status_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '工号or学号·',
  PRIMARY KEY (`account`) USING BTREE,
  UNIQUE INDEX `account`(`account` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of accounts
-- ----------------------------
INSERT INTO `accounts` VALUES ('123456', '杨琴', '老师', 'cuihaojie123.', '麻省理工', '2025-07-16 09:36:50', 'yes', '12857');
INSERT INTO `accounts` VALUES ('12423020124@stu.cqut.edu.cn', '陈杰', '学生', 'cuihaojie123.', '清华大学', '2025-07-15 17:27:08', 'yes', '14253657889');
INSERT INTO `accounts` VALUES ('15338907202', '赖星宇', '老师', 'cheuioavcerdahgo', '重庆大学', '2025-07-15 19:16:15', 'yes', '');
INSERT INTO `accounts` VALUES ('278231472', '吴永治', '老师', 'cuihaojie123.', '北京大学', '2025-07-15 19:11:39', 'yes', '');
INSERT INTO `accounts` VALUES ('2782314722@qq.com', '崔豪杰', '老师', 'cuihaojie123.', '重庆大学', '2025-07-15 19:22:29', 'yes', '');
INSERT INTO `accounts` VALUES ('5434五532245524245', '崔豪杰', '老师', 'cuihaojie123.', '重庆大学', '2025-07-15 19:23:33', 'yes', '');

-- ----------------------------
-- Table structure for content
-- ----------------------------
DROP TABLE IF EXISTS `content`;
CREATE TABLE `content`  (
  `content_id` bigint NOT NULL COMMENT '作业id',
  `account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '提交账户',
  `score` tinyint NULL DEFAULT NULL COMMENT '作业分数',
  `details` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '作业内容',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`content_id`, `account`) USING BTREE,
  INDEX `account`(`account` ASC) USING BTREE,
  CONSTRAINT `content_ibfk_1` FOREIGN KEY (`content_id`) REFERENCES `homework` (`homework_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `content_ibfk_2` FOREIGN KEY (`account`) REFERENCES `accounts` (`account`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of content
-- ----------------------------
INSERT INTO `content` VALUES (5, '12423020124@stu.cqut.edu.cn', 80, 'qweDQWEDFWEDEWFEWFWSEFE ', '2025-08-21 16:53:49');
INSERT INTO `content` VALUES (5, '278231472', 90, 'WESFVCRSGV', '2025-08-21 16:54:35');

-- ----------------------------
-- Table structure for course_school_class
-- ----------------------------
DROP TABLE IF EXISTS `course_school_class`;
CREATE TABLE `course_school_class`  (
  `course_id` bigint NOT NULL,
  `school_class_id` int NOT NULL,
  PRIMARY KEY (`course_id`, `school_class_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_school_class
-- ----------------------------
INSERT INTO `course_school_class` VALUES (348, 1);
INSERT INTO `course_school_class` VALUES (348, 2);

-- ----------------------------
-- Table structure for courses
-- ----------------------------
DROP TABLE IF EXISTS `courses`;
CREATE TABLE `courses`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '课程id',
  `teacher_account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '老师账户（用于创建课程）',
  `class_time` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程截止时间',
  `class_name` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程标题',
  `selected_classes` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '选课班级',
  `code` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '加课码',
  `is_pinned` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否置顶（1-置顶，0-非置顶）',
  `school_class_id` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `teacher_account`(`teacher_account` ASC) USING BTREE,
  CONSTRAINT `courses_ibfk_1` FOREIGN KEY (`teacher_account`) REFERENCES `accounts` (`account`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 349 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of courses
-- ----------------------------
INSERT INTO `courses` VALUES (123, '2782314722@qq.com', '24', 'springboot', '01/02/03', 'CMRNRI1D', 1, NULL);
INSERT INTO `courses` VALUES (234, '2782314722@qq.com', '25', 'vue3', '02/03/04', 'FTITAN5L', 1, NULL);
INSERT INTO `courses` VALUES (345, '2782314722@qq.com', '25', 'html', '01/02/03/04', 'TQ0KKT2L', 1, NULL);
INSERT INTO `courses` VALUES (346, '2782314722@qq.com', '25', 'css', '01/02/03', '7L032REF', 1, NULL);
INSERT INTO `courses` VALUES (347, '2782314722@qq.com', '10', 'java', '02/03/04', 'RVUCXCNX', 1, NULL);
INSERT INTO `courses` VALUES (348, '2782314722@qq.com', '25', '计算机组成', '01/02/03', 'MU1GVNFW', 0, 1);

-- ----------------------------
-- Table structure for courses_homework
-- ----------------------------
DROP TABLE IF EXISTS `courses_homework`;
CREATE TABLE `courses_homework`  (
  `class_id` bigint NOT NULL COMMENT '关联课程',
  `homework_id` bigint NOT NULL COMMENT '关联作业',
  PRIMARY KEY (`class_id`, `homework_id`) USING BTREE,
  INDEX `mid_2`(`homework_id` ASC) USING BTREE,
  CONSTRAINT `courses_homework_ibfk_1` FOREIGN KEY (`class_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `courses_homework_ibfk_2` FOREIGN KEY (`homework_id`) REFERENCES `homework` (`homework_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of courses_homework
-- ----------------------------
INSERT INTO `courses_homework` VALUES (346, 5);
INSERT INTO `courses_homework` VALUES (346, 13);
INSERT INTO `courses_homework` VALUES (346, 14);
INSERT INTO `courses_homework` VALUES (346, 15);
INSERT INTO `courses_homework` VALUES (346, 16);
INSERT INTO `courses_homework` VALUES (346, 17);
INSERT INTO `courses_homework` VALUES (346, 18);
INSERT INTO `courses_homework` VALUES (234, 19);

-- ----------------------------
-- Table structure for homework
-- ----------------------------
DROP TABLE IF EXISTS `homework`;
CREATE TABLE `homework`  (
  `submitter` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '提交人',
  `homework_id` bigint NOT NULL AUTO_INCREMENT COMMENT '外键（关联课程）',
  `content_id` bigint NULL DEFAULT NULL COMMENT '被作业内容关联',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '作业名称',
  `deadline` datetime NOT NULL COMMENT '截止时间',
  `type` enum('个人作业','团队作业') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '作业类型',
  `isCorrect` tinyint(1) NULL DEFAULT NULL COMMENT '是否批改',
  `score` int NULL DEFAULT NULL COMMENT '分数',
  `details` varchar(10000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '详情',
  PRIMARY KEY (`homework_id`) USING BTREE,
  INDEX `content_id`(`content_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of homework
-- ----------------------------
INSERT INTO `homework` VALUES (NULL, 5, 0, 'springboot', '2025-08-29 00:00:00', '团队作业', 0, 0, '测试1');
INSERT INTO `homework` VALUES (NULL, 13, 0, '测试', '2025-08-22 00:00:00', '个人作业', 0, 0, '测试');
INSERT INTO `homework` VALUES (NULL, 14, 0, '测试2', '2025-08-24 00:00:00', '个人作业', 0, 0, '测试2');
INSERT INTO `homework` VALUES (NULL, 15, 0, '测试2', '2025-08-24 00:00:00', '个人作业', 0, 0, '测试2');
INSERT INTO `homework` VALUES (NULL, 16, 0, '测试2', '2025-08-24 00:00:00', '个人作业', 0, 0, '测试2');
INSERT INTO `homework` VALUES (NULL, 17, 0, '测试2', '2025-08-22 00:00:00', '团队作业', 0, 0, '上千万的');
INSERT INTO `homework` VALUES (NULL, 18, 0, 'springcloud', '2026-06-24 00:00:00', '个人作业', 0, 0, '课堂派');
INSERT INTO `homework` VALUES (NULL, 19, 0, 'springcloud', '2026-06-24 00:00:00', '个人作业', 0, 0, '');

-- ----------------------------
-- Table structure for notification
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `account` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `class_id` int NULL DEFAULT NULL,
  `homework_id` int NULL DEFAULT NULL,
  `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `message` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `is_read` tinyint NOT NULL DEFAULT 0,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notification
-- ----------------------------
INSERT INTO `notification` VALUES (1, '12423020124@stu.cqut.edu.cn', 234, 19, 'remind', '老师催交作业：springcloud', 0, '2026-06-22 20:35:13');
INSERT INTO `notification` VALUES (2, '2782314722@qq.com', 234, 19, 'remind', '老师催交作业：springcloud', 1, '2026-06-22 20:35:13');
INSERT INTO `notification` VALUES (3, '12423020124@stu.cqut.edu.cn', 234, 19, 'remind', '老师催交作业：springcloud', 0, '2026-06-22 20:43:23');
INSERT INTO `notification` VALUES (4, '2782314722@qq.com', 234, 19, 'remind', '老师催交作业：springcloud', 1, '2026-06-22 20:43:23');

-- ----------------------------
-- Table structure for school_class
-- ----------------------------
DROP TABLE IF EXISTS `school_class`;
CREATE TABLE `school_class`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `mechanism` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `teacher_account` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of school_class
-- ----------------------------
INSERT INTO `school_class` VALUES (1, '软工2024', '重庆理工大学', '2782314722@qq.com');
INSERT INTO `school_class` VALUES (2, '计科2024', '重庆理工大学', '2782314722@qq.com');

-- ----------------------------
-- Table structure for student_class
-- ----------------------------
DROP TABLE IF EXISTS `student_class`;
CREATE TABLE `student_class`  (
  `account` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `school_class_id` int NOT NULL,
  PRIMARY KEY (`account`, `school_class_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student_class
-- ----------------------------

-- ----------------------------
-- Function structure for generate_random_code
-- ----------------------------
DROP FUNCTION IF EXISTS `generate_random_code`;
delimiter ;;
CREATE FUNCTION `generate_random_code`()
 RETURNS varchar(8) CHARSET utf8mb4
  DETERMINISTIC
BEGIN
    DECLARE chars VARCHAR(36) DEFAULT 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
    DECLARE code VARCHAR(8) DEFAULT '';
    DECLARE i INT DEFAULT 0;

    WHILE i < 8 DO
            SET code = CONCAT(code, SUBSTRING(chars, FLOOR(1 + RAND() * 36), 1));
            SET i = i + 1;
        END WHILE;

    RETURN code;
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table courses
-- ----------------------------
DROP TRIGGER IF EXISTS `before_insert_courses`;
delimiter ;;
CREATE TRIGGER `before_insert_courses` BEFORE INSERT ON `courses` FOR EACH ROW BEGIN
    DECLARE new_code VARCHAR(8);
    DECLARE code_exists INT;

    -- 循环直到生成唯一编码
    REPEAT
        SET new_code = generate_random_code();
        SELECT COUNT(*) INTO code_exists FROM courses WHERE code = new_code;
    UNTIL code_exists = 0 END REPEAT;

    SET NEW.code = new_code;
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
