-- ============================================
-- 添加管理员角色支持
-- ============================================

USE horizon;

-- 1. 添加 role 字段
ALTER TABLE t_user 
ADD COLUMN role VARCHAR(20) DEFAULT 'USER' COMMENT '用户角色：USER/ADMIN' AFTER status;

-- 2. 创建索引
CREATE INDEX idx_role ON t_user(role);

-- 3. 设置第一个用户为管理员（可选）
UPDATE t_user 
SET role = 'ADMIN' 
WHERE id = 1;

-- 4. 验证
SELECT id, username, nickname, role, status FROM t_user;

SELECT '✅ 管理员角色字段添加完成！' AS message;
