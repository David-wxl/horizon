-- HORIZON 数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS horizon CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE horizon;

-- 用户表
CREATE TABLE IF NOT EXISTS t_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码（MD5加密）',
    email VARCHAR(100) COMMENT '邮箱',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(255) COMMENT '头像URL',
    status INT DEFAULT 0 COMMENT '状态：0-正常，1-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 知识树节点表
CREATE TABLE IF NOT EXISTS t_knowledge_node (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '节点ID',
    title VARCHAR(200) NOT NULL COMMENT '节点标题',
    content TEXT COMMENT '节点内容（Markdown）',
    parent_id BIGINT DEFAULT 0 COMMENT '父节点ID（0表示根节点）',
    node_type INT NOT NULL COMMENT '节点类型：0-主蓝图（全局只读），1-个人第二大脑（用户可编辑）',
    user_id BIGINT COMMENT '所属用户ID（node_type=1时有效）',
    sort_order INT DEFAULT 0 COMMENT '排序权重',
    icon VARCHAR(50) COMMENT '节点图标',
    color VARCHAR(20) COMMENT '节点颜色',
    status INT DEFAULT 0 COMMENT '状态：0-正常，1-已归档',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_parent_id (parent_id),
    INDEX idx_node_type (node_type),
    INDEX idx_user_id (user_id),
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识树节点表';

-- 插入主蓝图示例数据（全局只读）
INSERT INTO t_knowledge_node (title, content, parent_id, node_type, sort_order, icon, color) VALUES
('前端开发', '前端开发知识体系', 0, 0, 1, '🎨', '#3b82f6'),
('后端开发', '后端开发知识体系', 0, 0, 2, '⚙️', '#10b981'),
('数据库', '数据库知识体系', 0, 0, 3, '💾', '#f59e0b');

-- 插入前端子节点
INSERT INTO t_knowledge_node (title, content, parent_id, node_type, sort_order, icon, color) 
SELECT 'HTML/CSS', 'HTML和CSS基础', id, 0, 1, '📝', '#3b82f6' FROM t_knowledge_node WHERE title='前端开发' AND node_type=0;

INSERT INTO t_knowledge_node (title, content, parent_id, node_type, sort_order, icon, color) 
SELECT 'JavaScript', 'JavaScript核心知识', id, 0, 2, '🔧', '#3b82f6' FROM t_knowledge_node WHERE title='前端开发' AND node_type=0;

INSERT INTO t_knowledge_node (title, content, parent_id, node_type, sort_order, icon, color) 
SELECT 'Vue.js', 'Vue框架学习', id, 0, 3, '💚', '#3b82f6' FROM t_knowledge_node WHERE title='前端开发' AND node_type=0;

-- 插入后端子节点
INSERT INTO t_knowledge_node (title, content, parent_id, node_type, sort_order, icon, color) 
SELECT 'Java', 'Java编程语言', id, 0, 1, '☕', '#10b981' FROM t_knowledge_node WHERE title='后端开发' AND node_type=0;

INSERT INTO t_knowledge_node (title, content, parent_id, node_type, sort_order, icon, color) 
SELECT 'Spring Boot', 'Spring Boot框架', id, 0, 2, '🍃', '#10b981' FROM t_knowledge_node WHERE title='后端开发' AND node_type=0;

-- 显示初始化结果
SELECT '数据库初始化完成！' AS message;
SELECT * FROM t_knowledge_node WHERE node_type=0;
