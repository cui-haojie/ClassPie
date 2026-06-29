-- BCrypt 哈希长度为 60，需扩展 password 列
ALTER TABLE accounts MODIFY COLUMN password VARCHAR(255) NOT NULL;
