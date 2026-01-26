# 地平线 | 视界 (HORIZON)

> 基于 Bento Grid 的个人数字策展平台

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Vue](https://img.shields.io/badge/Vue-3.4-green.svg)](https://vuejs.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1-green.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)

## 项目简介

「地平线 | 视界」是一个现代化的个人数字策展平台，采用 Bento Grid 布局，让用户像拼图一样自由组织和展示内容。

### 核心特性

- 🎨 **极致暖色 Bento Grid** - 玻璃拟态卡片、超大圆角、柔和阴影
- ✍️ **所见即所得编辑** - 编辑/展示模式一键切换
- 🧩 **多类型内容卡片** - 文本、图片、代码、链接、媒体卡片
- 🌍 **社区广场** - 智能搜索、热门排序、社交互动
- 👤 **用户系统** - 完整的个人资料和主页展示
- 🔐 **管理员后台** - 精美的 Glassmorphism 设计风格
- 📢 **通知系统** - 实时通知推送、审核结果通知

---

## 技术栈

### 前端
- Vue 3 + TypeScript + Vite
- Tailwind CSS（原子化 CSS）
- Vue Router（路由）
- Axios（HTTP 客户端）

### 后端
- Spring Boot 3.1.5
- Java 17
- MySQL 8.0
- MyBatis-Plus 3.5.7
- JWT 认证

---

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

### 2. 数据库配置

创建数据库并导入初始化脚本：

```bash
mysql -u root -p
CREATE DATABASE horizon CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE horizon;
source backend/src/main/resources/sql/init.sql
```

### 3. 后端启动

修改配置文件 `backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/horizon?useUnicode=true&characterEncoding=utf8mb4
    username: root
    password: 你的密码
```

启动后端：

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

后端将运行在 `http://localhost:8080`

### 4. 前端启动

```bash
cd frontend
npm install
npm run dev
```

前端将运行在 `http://localhost:5173`

---

## 使用说明

### 普通用户

1. **注册/登录** - 访问主页，点击注册按钮创建账号
2. **创建卡片** - 进入"我的主页"，点击"+ 添加卡片"
3. **编辑模式** - 点击"编辑模式"可编辑、删除、批量管理卡片
4. **社区广场** - 浏览其他用户的公开内容，点赞和评论

### 管理员

1. **设置管理员权限** - 在数据库中将用户的 `role` 字段设置为 `ADMIN`

```sql
UPDATE t_user SET role = 'ADMIN' WHERE username = '你的用户名';
```

2. **访问管理后台** - 登录后，导航栏会显示"🔐 管理后台"按钮
3. **审核内容** - 查看待审核卡片，点击"通过"或"拒绝"（需填写理由）
4. **用户管理** - 查看用户统计、禁用/启用用户
5. **删除权限** - 管理员可以删除任意卡片和评论

---

## 项目结构

```
horizon/
├── backend/                # 后端 (Spring Boot)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/       # Java 源代码
│   │   │   └── resources/  # 配置文件和 SQL 脚本
│   │   └── test/           # 测试代码
│   └── pom.xml             # Maven 配置
│
├── frontend/               # 前端 (Vue 3)
│   ├── src/
│   │   ├── api/           # API 接口
│   │   ├── components/    # Vue 组件
│   │   ├── views/         # 页面视图
│   │   ├── router/        # 路由配置
│   │   └── App.vue        # 根组件
│   ├── package.json       # npm 依赖
│   └── vite.config.ts     # Vite 配置
│
└── README.md              # 项目说明
```

---

## 核心功能

### 卡片类型

- **文本卡片** - 支持 Markdown 渲染
- **图片卡片** - Base64 或 URL，自动预览
- **代码卡片** - 语法高亮显示
- **链接卡片** - 一键访问外部链接
- **媒体卡片** - 书影音展示

### 审核流程

1. 用户创建卡片后，状态为"待审核"（status=0），不会在广场显示
2. 管理员在后台审核：
   - **通过** - 卡片状态变为"已通过"（status=1），在广场公开展示，用户收到通知
   - **拒绝** - 卡片状态变为"已拒绝"（status=2），用户收到包含理由的通知

### 通知系统

- 审核通过通知："✅ 审核通过"
- 审核拒绝通知："❌ 审核未通过"（包含具体理由）
- 实时红点提示
- 点击通知可跳转到相关卡片

---

## 数据库设计

### 主要表结构

- `t_user` - 用户表
- `t_bento_card` - 卡片表
- `t_comment` - 评论表
- `t_like` - 点赞表
- `t_notification` - 通知表

详细表结构请查看 `backend/src/main/resources/sql/init.sql`

---

## API 文档

### 用户相关
- `POST /api/user/register` - 用户注册
- `POST /api/user/login` - 用户登录
- `GET /api/user/info` - 获取用户信息

### 卡片相关
- `GET /api/card/square` - 获取广场卡片
- `POST /api/card/create` - 创建卡片
- `PUT /api/card/update` - 更新卡片
- `DELETE /api/card/delete/{id}` - 删除卡片

### 管理员相关
- `GET /api/admin/dashboard-stats` - 获取统计数据
- `GET /api/admin/audit-queue` - 获取审核队列
- `POST /api/admin/audit/{id}` - 审核卡片

### 通知相关
- `GET /api/notification/list` - 获取通知列表
- `GET /api/notification/unread-count` - 获取未读数量
- `PUT /api/notification/read/{id}` - 标记已读

---

## 常见问题

### 1. 端口冲突
- 后端默认端口：8080
- 前端默认端口：5173
- 如需修改，请编辑对应的配置文件

### 2. 跨域问题
已配置 CORS，允许前端访问后端 API

### 3. 数据库连接失败
检查 MySQL 是否启动，数据库名称、用户名、密码是否正确

### 4. 审核通过的卡片不显示
确保卡片的 `status=1` 且 `is_public=1`

---

## 开发计划

- [ ] 关注功能
- [ ] 消息系统
- [ ] 标签系统优化
- [ ] 卡片拖拽排序
- [ ] 移动端适配
- [ ] 国际化支持

---

## 许可证

[MIT License](LICENSE)

---

## 联系方式

如有问题或建议，欢迎提交 Issue 或 Pull Request。

项目地址：https://github.com/David-wxl/horizon
