-- 头像与作业附件
USE t_class;

ALTER TABLE accounts ADD COLUMN avatar_url VARCHAR(512) NULL COMMENT '头像路径';

ALTER TABLE content ADD COLUMN attachment_url VARCHAR(512) NULL COMMENT '附件路径';
ALTER TABLE content ADD COLUMN attachment_name VARCHAR(255) NULL COMMENT '附件原名';
