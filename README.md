<p align="center">
  <h1 align="center">HORIZON | 视界</h1>
  <p align="center">基于 Bento Grid 的个人数字策展平台</p>
</p>

<p align="center">
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-MIT-blue.svg" alt="License"></a>
  <img src="https://img.shields.io/badge/Vue-3.4-4FC08D?logo=vuedotjs&logoColor=white" alt="Vue">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.1-6DB33F?logo=springboot&logoColor=white" alt="Spring Boot">
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white" alt="MySQL">
  <img src="https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white" alt="Java">
  <img src="https://img.shields.io/badge/Tailwind%20CSS-3.x-06B6D4?logo=tailwindcss&logoColor=white" alt="Tailwind CSS">
</p>

---

## 简介

HORIZON 是一个现代化的个人数字策展平台。用户可以通过 **Bento Grid** 布局自由拼装内容卡片，支持代码、图片、Markdown 文本、链接、书影音等多种类型，打造个性化的数字展览馆。平台同时提供社区广场，让用户发现和互动。

## 功能特性

| 模块 | 功能 |
|------|------|
| **内容卡片** | 支持文本(Markdown)、图片、代码(语法高亮)、链接、书影音 5 种类型 |
| **Bento 布局** | 拖拽排序、拖拽边框调整卡片尺寸、自由 Grid 编排 |
| **社区广场** | 分类筛选、关键词/用户名搜索、最新/最热排序 |
| **社交互动** | 点赞、收藏、评论、关注/取关、用户主页 |
| **通知系统** | 审核结果推送、实时未读提醒、一键已读 |
| **内容审核** | 管理员审核队列、通过/拒绝(含理由)、状态流转 |
| **管理后台** | 数据统计(ECharts)、用户管理、内容管理、待办事项 |
| **用户系统** | 注册/登录(JWT)、个人资料编辑、密码修改、用户名唯一校验 |

## 技术栈

```
Frontend:  Vue 3 (Composition API) + TypeScript + Vite + Tailwind CSS
Backend:   Spring Boot 3.1.5 + Java 17 + MyBatis-Plus 3.5.7
Database:  MySQL 8.0
Auth:      JWT (JSON Web Token)
Charts:    ECharts 5.0
```

## 快速开始

### 环境要求

- Node.js 16+
- Java 17+
- MySQL 8.0+
- Maven 3.6+

### 1. 克隆项目

```bash
git clone https://github.com/David-wxl/horizon.git
cd horizon
```

### 2. 数据库初始化

```sql
CREATE DATABASE horizon CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE horizon;
SOURCE backend/src/main/resources/sql/init.sql;
-- 可选：导入模拟数据
SOURCE backend/src/main/resources/sql/mock_data.sql;
```

### 3. 后端启动

编辑 `backend/src/main/resources/application.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/horizon?useUnicode=true&characterEncoding=utf8mb4
    username: root
    password: your_password
```

```bash
cd backend
mvn spring-boot:run
```

后端运行于 `http://localhost:8080`

### 4. 前端启动

```bash
cd frontend
npm install
npm run dev
```

前端运行于 `http://localhost:5173`

### 5. 管理员设置

```sql
UPDATE t_user SET role = 'ADMIN' WHERE username = 'your_username';
```

## 项目结构

```
horizon/
├── backend/
│   └── src/main/java/com/horizon/
│       ├── config/          # 配置类 (CORS, MyBatis-Plus, WebMvc)
│       ├── controller/      # REST 控制器
│       ├── entity/          # 实体类
│       ├── mapper/          # MyBatis Mapper
│       ├── service/         # 业务逻辑
│       ├── interceptor/     # JWT 拦截器
│       ├── dto/             # 数据传输对象
│       ├── vo/              # 视图对象
│       ├── common/          # 通用响应封装
│       ├── exception/       # 全局异常处理
│       └── util/            # 工具类 (JWT)
│
├── frontend/src/
│   ├── api/                 # Axios 请求封装
│   ├── components/          # 通用组件 (BentoCard, NotificationBell)
│   ├── views/               # 页面 (Home, Square, Login, CardDetail...)
│   ├── router/              # Vue Router 路由配置
│   ├── style.css            # 全局样式 (Glassmorphism)
│   └── main.ts              # 应用入口
│
└── README.md
```

## 数据库设计

| 表名 | 说明 |
|------|------|
| `t_user` | 用户表 (账号、密码、角色、头像) |
| `t_user_profile` | 用户档案 (简介、性别、地区) |
| `t_bento_card` | 内容卡片 (类型、内容、布局、审核状态) |
| `t_comment` | 评论表 (支持嵌套回复) |
| `t_like` | 点赞表 |
| `t_favorite` | 收藏表 |
| `t_follow` | 关注关系表 |
| `t_notification` | 通知表 |

完整建表语句见 [`backend/src/main/resources/sql/init.sql`](backend/src/main/resources/sql/init.sql)

## API 概览

| 模块 | 端点 | 说明 |
|------|------|------|
| 用户 | `POST /api/user/register` | 注册 |
| | `POST /api/user/login` | 登录 |
| | `GET /api/user/{userId}` | 获取用户信息 |
| | `GET /api/user/checkUsername` | 用户名可用性检查 |
| 卡片 | `GET /api/card/square` | 广场卡片列表 |
| | `POST /api/card/create` | 创建卡片 |
| | `PUT /api/card/update` | 更新卡片 |
| | `DELETE /api/card/delete/{id}` | 删除卡片 |
| 互动 | `POST /api/like/card` | 点赞 |
| | `POST /api/favorite` | 收藏 |
| | `POST /api/follow` | 关注 |
| | `POST /api/comment/create` | 评论 |
| 通知 | `GET /api/notification/list` | 通知列表 |
| | `GET /api/notification/unread-count` | 未读数 |
| 管理 | `GET /api/admin/dashboard-stats` | 仪表盘数据 |
| | `POST /api/admin/audit/{cardId}` | 审核卡片 |

## 许可证

[MIT License](LICENSE)
