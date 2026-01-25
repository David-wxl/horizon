-- 修复头像字段长度问题
-- Base64图片会很长，需要使用LONGTEXT
USE horizon;

-- 修改avatar字段类型
ALTER TABLE t_user 
MODIFY COLUMN avatar LONGTEXT COMMENT '头像URL或Base64';

-- 验证
DESCRIBE t_user;

-- 查看结果
SELECT 
    COLUMN_NAME, 
    COLUMN_TYPE, 
    CHARACTER_MAXIMUM_LENGTH 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'horizon' 
AND TABLE_NAME = 't_user' 
AND COLUMN_NAME = 'avatar';
