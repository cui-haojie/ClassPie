-- 话题回复图片 + 作业发布附件
USE t_class;

ALTER TABLE course_activity_reply
    ADD COLUMN attachment_url VARCHAR(512) NULL COMMENT '回复附件路径',
    ADD COLUMN attachment_name VARCHAR(255) NULL COMMENT '回复附件原名';

ALTER TABLE homework
    ADD COLUMN attachment_url VARCHAR(512) NULL COMMENT '作业附件路径',
    ADD COLUMN attachment_name VARCHAR(255) NULL COMMENT '作业附件原名';
