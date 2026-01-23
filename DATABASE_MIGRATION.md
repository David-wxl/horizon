# 📊 数据库重构指南

> 从知识树平台迁移到 Bento 数字策展平台

---

## 🎯 重构概述

本次重构将数据库从"知识树学习平台"完全转型为"Bento 数字策展平台"。

### 核心变化

| 变化类型 | 旧设计 | 新设计 |
|---------|--------|--------|
| **核心表** | t_knowledge_node（知识树节点） | t_bento_card（Bento 卡片） |
| **业务模型** | 树形递归结构 | 平铺 Grid 布局 |
| **内容类型** | Markdown 文本 | 多类型（图片/代码/文本/书影音/链接/合集） |
| **新增功能** | - | 用户配置、点赞、评论、关注 |

---

## 📋 迁移步骤

### 步骤 1：备份旧数据（可选）

如果你想保留旧的知识树数据：

```bash
# 导出旧数据
mysqldump -u root -p horizon > horizon_backup_old.sql

# 或仅导出知识树表
mysqldump -u root -p horizon t_knowledge_node > knowledge_node_backup.sql
```

### 步骤 2：删除旧数据库（重新开始）

```bash
mysql -u root -p
```

```sql
-- 删除旧数据库
DROP DATABASE IF EXISTS horizon;

-- 重新创建数据库
CREATE DATABASE horizon CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 步骤 3：执行新的初始化脚本

```bash
# 进入 backend 目录
cd D:\graduation-project\horizon\backend

# 执行新的初始化脚本
mysql -u root -p horizon < src/main/resources/sql/init.sql
```

**看到以下输出表示成功：**

```
✅ 数据库初始化完成！
📊 已创建 6 张表
👤 测试用户：demo_user (密码：123456)
🎨 已创建 4 张示例卡片
```

### 步骤 4：验证表结构

```sql
USE horizon;

-- 查看所有表
SHOW TABLES;

-- 应该看到以下 6 张表：
-- t_user
-- t_user_profile
-- t_bento_card
-- t_like
-- t_comment
-- t_follow
```

### 步骤 5：查看测试数据

```sql
-- 查看测试用户
SELECT * FROM t_user WHERE username = 'demo_user';

-- 查看用户配置
SELECT * FROM t_user_profile;

-- 查看示例卡片
SELECT id, title, card_type, category FROM t_bento_card;
```

---

## 🗄️ 新数据库表结构

### 1. t_user (用户表) - ✅ 保持不变

```
字段：id, username, password, email, nickname, avatar, status
```

### 2. t_user_profile (用户个人主页配置表) - 🆕 新增

```
字段：
- 基本信息：page_title, bio, custom_url
- 主题配置：theme_color, bg_style
- 统计信息：total_cards, total_views, total_likes
- 社交信息：github, twitter, website
```

### 3. t_bento_card (Bento 卡片表) - 🆕 核心表

```
字段：
- 基本信息：title, description, card_type
- 内容存储：content (JSON)
- 布局信息：grid_x, grid_y, grid_width, grid_height
- 样式配置：bg_color, border_style, card_theme
- 分类标签：tags, category
- 公开性：is_public, status
- 统计信息：like_count, view_count, comment_count
- 排序：sort_order, is_pinned
```

**支持的卡片类型：**
- `image`: 图片卡片（OOTD、摄影）
- `code`: 代码卡片（代码片段、技术笔记）
- `text`: 文本卡片（思考、日记）
- `media`: 书影音卡片（书籍、电影、音乐）
- `link`: 链接卡片（收藏的网站）
- `collection`: 合集卡片（多内容组合）

### 4. t_like (点赞表) - 🆕 新增

```
字段：user_id, card_id, card_owner_id
```

### 5. t_comment (评论表) - 🆕 新增

```
字段：card_id, user_id, content, parent_id, reply_to_user_id
支持二级评论（回复）
```

### 6. t_follow (关注表) - 🆕 新增

```
字段：follower_id, following_id
```

---

## 🔧 后端代码更新

### ✅ 已完成

1. **Entity 实体类**
   - ✅ 新增：`BentoCard.java`, `UserProfile.java`, `Like.java`, `Comment.java`, `Follow.java`
   - ❌ 删除：`KnowledgeNode.java`

2. **Mapper 接口**
   - ✅ 新增：`BentoCardMapper.java`, `UserProfileMapper.java`, `LikeMapper.java`, `CommentMapper.java`, `FollowMapper.java`
   - ❌ 删除：`KnowledgeNodeMapper.java`

3. **Service 业务逻辑**
   - ✅ 新增：`BentoCardService.java`, `UserProfileService.java`, `LikeService.java`, `CommentService.java`, `FollowService.java`
   - ❌ 删除：`KnowledgeNodeService.java`
   - ✅ 更新：`UserService.java` (注册时自动创建 UserProfile)

4. **Controller 接口**
   - ✅ 新增：`BentoCardController.java`, `UserProfileController.java`, `LikeController.java`, `CommentController.java`, `FollowController.java`
   - ❌ 删除：`KnowledgeController.java`
   - ✅ 保留：`UserController.java`

---

## 🧪 测试新接口

### 1. 测试用户注册（自动创建 UserProfile）

```bash
curl -X POST http://localhost:8080/api/user/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test_user",
    "password": "123456",
    "email": "test@horizon.com",
    "nickname": "测试用户"
  }'
```

**预期响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1Ni...",
    "user": {
      "id": 2,
      "username": "test_user",
      "nickname": "测试用户",
      "email": "test@horizon.com"
    }
  }
}
```

### 2. 测试获取用户配置

```bash
curl http://localhost:8080/api/profile/2
```

**预期响应：**
```json
{
  "code": 200,
  "data": {
    "id": 2,
    "userId": 2,
    "pageTitle": "我的展览馆",
    "bio": null,
    "customUrl": null,
    "themeColor": "#f6d365",
    "bgStyle": "warm",
    "totalCards": 0,
    "totalViews": 0,
    "totalLikes": 0
  }
}
```

### 3. 测试创建 Bento 卡片

```bash
curl -X POST http://localhost:8080/api/card/create \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 2,
    "title": "我的第一张卡片",
    "description": "这是一张测试卡片",
    "cardType": "text",
    "content": "{\"markdown\": \"Hello Bento Grid!\"}",
    "gridX": 0,
    "gridY": 0,
    "gridWidth": 1,
    "gridHeight": 1,
    "category": "text"
  }'
```

### 4. 测试获取用户卡片

```bash
curl http://localhost:8080/api/card/user/2
```

### 5. 测试获取广场卡片

```bash
curl http://localhost:8080/api/card/square?pageNum=1&pageSize=10
```

---

## 🚀 启动项目

### 1. 启动后端

```bash
cd D:\graduation-project\horizon\backend
mvn clean compile
mvn spring-boot:run
```

**看到以下输出表示启动成功：**
```
╔═══════════════════════════════════════╗
║   HORIZON Backend Started Successfully   ║
║   Port: 8080                          ║
║   Context: /api                       ║
╚═══════════════════════════════════════╝
```

### 2. 启动前端

```bash
cd D:\graduation-project\horizon\frontend
npm run dev
```

### 3. 测试连接

访问：http://localhost:5173

应该能看到 Warm Bento Grid 页面，并且后端连接测试应该显示 ✅ **已连接**。

---

## ⚠️ 常见问题

### Q1: 编译错误 "找不到 KnowledgeNode"

**原因**：旧代码引用了已删除的类。

**解决**：确保已删除所有对 `KnowledgeNode`、`KnowledgeNodeService`、`KnowledgeNodeMapper`、`KnowledgeController` 的引用。

### Q2: 启动时报错 "Invalid bound statement"

**原因**：Mapper 接口与数据库表不匹配。

**解决**：
1. 确保数据库已执行新的 `init.sql`
2. 确保 MyBatis-Plus 配置正确
3. 重新编译项目：`mvn clean compile`

### Q3: JSON 字段无法解析

**原因**：`BentoCard` 的 `content` 字段需要 JSON 类型处理器。

**解决**：确保 `BentoCard.java` 中已添加：
```java
@TableField(typeHandler = JacksonTypeHandler.class)
private String content;
```

---

## 📝 下一步

数据库重构完成后，接下来可以：

1. ✅ 前端开发 Bento 编辑器（WYSIWYG）
2. ✅ 前端开发内容自动构建器
3. ✅ 前端开发社区广场（The Square）
4. ✅ 前端开发用户个人主页

---

**重构完成时间：2026-01-23**

**版本：v2.0.0 - Bento Edition**
