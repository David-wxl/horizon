-- ============================================
-- HORIZON | 视界 数据库初始化脚本
-- SaaS 级个人数字策展平台
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS horizon CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE horizon;

-- ============================================
-- 1. 用户表
-- ============================================
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
    INDEX idx_email (email),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================
-- 2. 用户个人主页配置表
-- ============================================
CREATE TABLE IF NOT EXISTS t_user_profile (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '配置ID',
    user_id BIGINT NOT NULL UNIQUE COMMENT '用户ID',
    page_title VARCHAR(100) DEFAULT '我的展览馆' COMMENT '个人主页标题',
    bio TEXT COMMENT '个人简介',
    custom_url VARCHAR(50) UNIQUE COMMENT '自定义URL（用户名缩写）',
    
    -- 主题配置
    theme_color VARCHAR(20) DEFAULT '#f6d365' COMMENT '主题色',
    bg_style VARCHAR(20) DEFAULT 'warm' COMMENT '背景风格：warm/cool/dark',
    
    -- 统计信息
    total_cards INT DEFAULT 0 COMMENT '总卡片数',
    total_views INT DEFAULT 0 COMMENT '总浏览量',
    total_likes INT DEFAULT 0 COMMENT '总点赞数',
    
    -- 社交信息
    github VARCHAR(100) COMMENT 'GitHub',
    twitter VARCHAR(100) COMMENT 'Twitter',
    website VARCHAR(200) COMMENT '个人网站',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_user_id (user_id),
    INDEX idx_custom_url (custom_url)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户个人主页配置表';

-- ============================================
-- 3. Bento 卡片表（核心表）
-- ============================================
CREATE TABLE IF NOT EXISTS t_bento_card (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '卡片ID',
    user_id BIGINT NOT NULL COMMENT '所属用户ID',
    
    -- 基本信息
    title VARCHAR(200) COMMENT '卡片标题',
    description TEXT COMMENT '卡片描述',
    card_type VARCHAR(20) NOT NULL COMMENT '卡片类型：image/code/text/media/link/collection',
    
    -- 内容存储（JSON格式，根据card_type存储不同结构）
    content JSON COMMENT '卡片内容（JSON格式）',
    -- image: {urls: [], captions: [], style: "grid/slider"}
    -- code: {language: "javascript", code: "...", theme: "dark"}
    -- text: {markdown: "...", style: "prose/minimal"}
    -- media: {type: "video/music/book", title: "", cover: "", metadata: {}}
    -- link: {url: "", title: "", favicon: "", preview: ""}
    -- collection: {items: [{type, content}]}
    
    -- 布局信息（Grid 坐标系统）
    grid_x INT DEFAULT 0 COMMENT 'Grid X 坐标',
    grid_y INT DEFAULT 0 COMMENT 'Grid Y 坐标',
    grid_width INT DEFAULT 1 COMMENT '卡片宽度（1-4）',
    grid_height INT DEFAULT 1 COMMENT '卡片高度（1-4）',
    
    -- 样式配置
    bg_color VARCHAR(20) COMMENT '背景色（可选）',
    border_style VARCHAR(20) DEFAULT 'default' COMMENT '边框样式',
    card_theme VARCHAR(20) DEFAULT 'auto' COMMENT '卡片主题：auto/light/dark',
    
    -- 标签与分类
    tags VARCHAR(500) COMMENT '标签（逗号分隔）',
    category VARCHAR(50) COMMENT '分类：ootd/edc/code/reading/travel',
    
    -- 公开性与状态
    is_public INT DEFAULT 1 COMMENT '是否公开到广场：0-私密，1-公开',
    status INT DEFAULT 0 COMMENT '状态：0-正常，1-归档，2-草稿',
    
    -- 统计信息
    like_count INT DEFAULT 0 COMMENT '点赞数',
    view_count INT DEFAULT 0 COMMENT '查看数',
    comment_count INT DEFAULT 0 COMMENT '评论数',
    
    -- 排序与权重
    sort_order INT DEFAULT 0 COMMENT '排序权重（用户自定义顺序）',
    is_pinned INT DEFAULT 0 COMMENT '是否置顶：0-否，1-是',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    
    INDEX idx_user_id (user_id),
    INDEX idx_card_type (card_type),
    INDEX idx_category (category),
    INDEX idx_is_public (is_public),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time),
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Bento卡片表';

-- ============================================
-- 4. 点赞表
-- ============================================
CREATE TABLE IF NOT EXISTS t_like (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '点赞ID',
    user_id BIGINT NOT NULL COMMENT '点赞用户ID',
    card_id BIGINT NOT NULL COMMENT '被点赞卡片ID',
    card_owner_id BIGINT NOT NULL COMMENT '卡片所有者ID（冗余字段，方便查询）',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    
    UNIQUE KEY uk_user_card (user_id, card_id),
    INDEX idx_card_id (card_id),
    INDEX idx_user_id (user_id),
    INDEX idx_card_owner_id (card_owner_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞表';

-- ============================================
-- 5. 评论表
-- ============================================
CREATE TABLE IF NOT EXISTS t_comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评论ID',
    card_id BIGINT NOT NULL COMMENT '被评论卡片ID',
    user_id BIGINT NOT NULL COMMENT '评论用户ID',
    card_owner_id BIGINT NOT NULL COMMENT '卡片所有者ID',
    
    content TEXT NOT NULL COMMENT '评论内容',
    parent_id BIGINT DEFAULT 0 COMMENT '父评论ID（0表示一级评论）',
    reply_to_user_id BIGINT COMMENT '回复给谁（二级评论时有值）',
    
    like_count INT DEFAULT 0 COMMENT '点赞数',
    status INT DEFAULT 0 COMMENT '状态：0-正常，1-已删除，2-被举报',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    
    INDEX idx_card_id (card_id),
    INDEX idx_user_id (user_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- ============================================
-- 6. 关注表（可选，初期可不启用）
-- ============================================
CREATE TABLE IF NOT EXISTS t_follow (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关注ID',
    follower_id BIGINT NOT NULL COMMENT '关注者ID（我）',
    following_id BIGINT NOT NULL COMMENT '被关注者ID（Ta）',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    
    UNIQUE KEY uk_follower_following (follower_id, following_id),
    INDEX idx_follower_id (follower_id),
    INDEX idx_following_id (following_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='关注表';

-- ============================================
-- 插入测试数据
-- ============================================

-- 插入测试用户
INSERT INTO t_user (username, password, email, nickname, avatar) VALUES
('demo_user', MD5('123456'), 'demo@horizon.com', 'Demo 策展人', 'https://api.dicebear.com/7.x/avataaars/svg?seed=demo');

-- 获取刚插入的用户ID
SET @demo_user_id = LAST_INSERT_ID();

-- 插入用户配置
INSERT INTO t_user_profile (user_id, page_title, bio, custom_url) VALUES
(@demo_user_id, '我的数字展览馆', '这是一个示例账号，展示 Bento Grid 布局的魅力。', 'demo');

-- 插入示例卡片
INSERT INTO t_bento_card (user_id, title, description, card_type, content, grid_x, grid_y, grid_width, grid_height, category, is_public) VALUES
-- 图片卡片（OOTD）
(@demo_user_id, '今日穿搭', '简约风格，米色系', 'image', 
 JSON_OBJECT('urls', JSON_ARRAY('https://images.unsplash.com/photo-1490481651871-ab68de25d43d'), 'style', 'single'),
 0, 0, 1, 2, 'ootd', 1),

-- 代码卡片
(@demo_user_id, 'Vue 3 Composition API', '我最喜欢的响应式语法', 'code',
 JSON_OBJECT('language', 'javascript', 'code', 'const count = ref(0)\nconst double = computed(() => count.value * 2)', 'theme', 'nord'),
 1, 0, 2, 1, 'code', 1),

-- 文本卡片
(@demo_user_id, '今日思考', '关于设计的一些想法', 'text',
 JSON_OBJECT('markdown', '简约不是少，而是没有多余。\n\n好的设计应该像空气一样，你感觉不到它的存在，但离开它却无法生存。', 'style', 'prose'),
 1, 1, 1, 1, 'text', 1),

-- 书影音卡片
(@demo_user_id, '最近在读', '《设计心理学》', 'media',
 JSON_OBJECT('type', 'book', 'title', '设计心理学', 'author', 'Donald Norman', 'cover', 'https://images.unsplash.com/photo-1512820790803-83ca734da794', 'rating', 5),
 3, 0, 1, 1, 'reading', 1);

-- ============================================
-- 显示初始化结果
-- ============================================
SELECT '✅ 数据库初始化完成！' AS message;
SELECT CONCAT('📊 已创建 ', COUNT(*), ' 张表') AS summary FROM information_schema.tables WHERE table_schema = 'horizon';
SELECT '👤 测试用户：demo_user (密码：123456)' AS test_account;
SELECT CONCAT('🎨 已创建 ', COUNT(*), ' 张示例卡片') AS sample_cards FROM t_bento_card;
