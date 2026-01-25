# 地平线 | 视界 (HORIZON)

> SaaS 级个人数字策展平台 - 基于 Warm Bento Grid 的所见即所得展示与社区广场

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Vue](https://img.shields.io/badge/Vue-3.4-green.svg)](https://vuejs.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1-green.svg)](https://spring.io/projects/spring-boot)

---

## 🎯 项目介绍

「地平线 | 视界」是一个 SaaS 级个人数字策展平台，旨在打破传统博客的列表形式，让用户通过高度自由的 Bento Grid 像拼图一样拼装生活切片，构建一流的个人数字展览馆。

### ✨ 核心特性

- **🎨 极致暖色 Bento Grid**
  - 玻璃拟态卡片、超大圆角与柔和阴影
  - 高可读性与留白节奏

- **✍️ 沉浸式所见即所得编辑**
  - 编辑/展示模式一键切换
  - 支持批量选择与删除

- **🧩 多类型内容卡片**
  - 文本卡片：支持Markdown渲染
  - 图片卡片：Base64或URL，自动预览
  - 代码卡片：语法高亮显示
  - 链接卡片：一键访问外部链接
  - 媒体卡片：书影音展示

- **🌍 社区化广场（The Square）**
  - 🔍 智能搜索：支持标题、内容、标签模糊查询
  - 🔥 热门排序：按最新/最热切换
  - 👀 访客模式：无需登录即可浏览
  - ❤️ 社交互动：点赞、评论、查看详情

- **👤 个人资料系统**
  - 完整的个人信息编辑（头像、昵称、简介等）
  - 用户主页展示公开作品
  - 评论显示用户头像和昵称
  - 点击头像跳转用户主页

- **🔐 智能权限管理**
  - JWT Token 身份认证
  - 访客模式支持浏览公开内容
  - 登录后解锁完整功能

---

## 🛠️ 技术栈

### 前端 (`/frontend`)
- **框架**: Vue 3 (Composition API, `<script setup>`)
- **构建工具**: Vite 4.x
- **语言**: TypeScript
- **样式**: Tailwind CSS (原子化优先)
- **代码高亮**: highlight.js
- **Markdown**: marked
- **动画**: CSS Transition / Vue Transition

### 后端 (`/backend`)
- **框架**: Spring Boot 3.1.5
- **语言**: Java 17+
- **数据库**: MySQL 8.0
- **ORM**: MyBatis-Plus 3.5.7
- **认证**: JWT (jjwt 0.12.3)

---

## 📁 项目结构

```
horizon/
├── README.md                   # 项目总说明
├── .cursorrules                # AI 开发规范
├── .gitignore                  # Git 忽略配置
├── NEW_FEATURES_GUIDE.md       # 新功能指南
├── BUGS_FIXED.md               # Bug修复记录
├── QUICK_DEPLOY.md             # 快速部署指南
│
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── components/         # Vue 组件
│   │   │   └── BentoCard.vue   # 卡片组件
│   │   ├── views/              # 页面视图
│   │   │   ├── Login.vue       # 登录注册页
│   │   │   ├── Square.vue      # 社区广场
│   │   │   ├── Home.vue        # 个人主页
│   │   │   ├── CardDetail.vue  # 卡片详情
│   │   │   ├── Profile.vue     # 个人资料
│   │   │   └── UserHome.vue    # 用户主页
│   │   ├── api/                # API 接口
│   │   ├── router/             # 路由配置
│   │   ├── App.vue             # 根组件
│   │   └── main.ts             # 入口文件
│   ├── package.json
│   ├── vite.config.ts
│   └── tailwind.config.js
│
└── backend/                    # 后端项目
    ├── src/main/java/com/horizon/
    │   ├── config/             # 配置类
    │   ├── controller/         # 控制器
    │   │   ├── UserController.java
    │   │   ├── BentoCardController.java
    │   │   ├── CommentController.java
    │   │   └── LikeController.java
    │   ├── service/            # 业务逻辑
    │   ├── mapper/             # 数据访问
    │   ├── entity/             # 实体类
    │   ├── vo/                 # 视图对象
    │   ├── dto/                # 数据传输对象
    │   ├── common/             # 公共类
    │   └── util/               # 工具类
    ├── src/main/resources/
    │   ├── application.yml     # 应用配置
    │   └── sql/init.sql        # 数据库初始化
    ├── pom.xml
    ├── START_BACKEND.bat       # 启动脚本
    ├── ADD_USER_PROFILE_FIELDS.sql
    └── FIX_AVATAR_LENGTH.sql
```

---

## 🚀 快速开始

### 环境要求

- **Node.js**: 18+ 
- **JDK**: 17+
- **Maven**: 3.6+
- **MySQL**: 8.0+

### 1. 克隆项目

```bash
git clone <your-repo-url>
cd horizon
```

### 2. 配置数据库

```sql
-- 创建数据库
CREATE DATABASE horizon CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 导入数据库结构
USE horizon;
SOURCE backend/src/main/resources/sql/init.sql;

-- 执行必要的数据库更新
-- 1. 添加用户资料字段
SOURCE backend/ADD_USER_PROFILE_FIELDS.sql;

-- 2. 修复头像字段长度
SOURCE backend/FIX_AVATAR_LENGTH.sql;
```

### 3. 配置后端

编辑 `backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/horizon
    username: root
    password: 你的数据库密码  # 修改这里
```

### 4. 启动后端

```bash
cd backend

# 方式1：使用Maven
mvn spring-boot:run

# 方式2：使用批处理脚本（Windows）
.\START_BACKEND.bat
```

后端服务将在 `http://localhost:8080/api` 启动

### 5. 启动前端

```bash
# 新开终端，进入前端目录
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端服务将在 `http://localhost:5173` 启动

### 6. 访问应用

浏览器打开 `http://localhost:5173`，即可开始使用！

**首次访问**：
1. 点击"立即注册"创建账号
2. 登录后自动跳转到社区广场
3. 点击"我的主页"开始创建你的第一张卡片

---

## 🎨 设计规范

### 色彩系统
- **背景渐变**: Warm Oatmeal `linear-gradient(135deg, #f5f5f0 0%, #efece1 50%, #f7f1e8 100%)`
- **主色调**: Warm Yellow / Orange / Soft Grey
- **文字**: High contrast, readable

### UI 原则
1. **Warm Bento Grid** - 模块化卡片 + 清晰层级
2. **柔和玻璃拟态** - 高透明度与柔和阴影
3. **流畅动画** - 0.6-0.8s 过渡时长
4. **大胆留白** - 充足的内边距和间距

详细设计规范请查看 [.cursorrules](./.cursorrules) 文件。

---

## 📡 核心 API

### 用户相关
- `POST /api/user/register` - 用户注册
- `POST /api/user/login` - 用户登录
- `GET /api/user/{userId}` - 获取用户信息
- `POST /api/user/updateProfile` - 更新个人资料

### 卡片相关
- `POST /api/card/create` - 创建卡片
- `PUT /api/card/update` - 更新卡片
- `DELETE /api/card/delete/{cardId}` - 删除卡片
- `GET /api/card/detail/{cardId}` - 获取卡片详情
- `GET /api/card/user/{userId}` - 获取用户卡片
- `GET /api/card/user/{userId}/public` - 获取用户公开卡片
- `GET /api/card/square` - 获取广场卡片（分页）

### 社交互动
- `POST /api/like/card` - 点赞卡片
- `DELETE /api/like/card` - 取消点赞
- `GET /api/like/check` - 检查点赞状态
- `POST /api/comment/create` - 发表评论
- `GET /api/comment/card/{cardId}` - 获取卡片评论
- `DELETE /api/comment/delete/{commentId}` - 删除评论

---

## 📝 开发规范

### 前端
- 使用 Vue 3 Composition API (`<script setup>`)
- 优先使用 Tailwind 原子类
- TypeScript 类型定义完善

### 后端
- Controller → Service → Mapper 三层架构
- 使用 DTO 和 VO 进行数据传输
- 为复杂业务逻辑添加注释

### Git 提交
- 使用中文提交信息：`feat: 添加xxx功能` / `fix: 修复xxx问题`
- 频繁提交，每个功能点一个提交
- 绝不提交敏感数据（密码、API Key）

---

## 📊 数据库设计

### 用户表 (t_user)
- 用户认证：用户名、密码（MD5加密）、JWT Token
- 个人资料：昵称、邮箱、头像（支持Base64）
- 扩展信息：个人简介、性别、生日、所在地
- 状态管理：账户状态、逻辑删除

### 卡片表 (t_bento_card)
- 基本信息：标题、描述、类型、内容（LONGTEXT）
- 布局信息：网格位置、尺寸
- 分类标签：分类、标签
- 统计数据：点赞数、评论数、查看数
- 权限控制：公开状态、所属用户

### 评论表 (t_comment)
- 评论内容、发布时间
- 关联关系：卡片ID、用户ID、卡片所有者ID
- 层级结构：支持父评论、回复
- 统计数据：点赞数

### 点赞表 (t_like)
- 关联关系：卡片ID、用户ID、卡片所有者ID
- 点赞时间、状态

---

## 🎯 开发路线图

### ✅ 已完成
- [x] 后端基础架构搭建（Spring Boot + MyBatis-Plus）
- [x] 用户注册登录系统（JWT认证）
- [x] 前端登录页面（Warm Bento Grid风格）
- [x] Bento卡片系统（6种卡片类型）
- [x] 个人主页与编辑模式
- [x] 社区广场（瀑布流展示）
- [x] 搜索功能（模糊查询）
- [x] 排序功能（最新/最热）
- [x] 访客模式（无需登录浏览）
- [x] 点赞评论系统
- [x] 个人资料页（完整信息编辑）
- [x] 用户主页（查看他人作品）
- [x] 批量删除功能
- [x] 代码高亮（highlight.js）
- [x] Markdown渲染（marked）

### 🚧 进行中
- [ ] 响应式适配优化
- [ ] 性能优化与缓存
- [ ] 图片压缩与CDN

### 📅 计划中
- [ ] AI内容生成助手（Gemini API）
- [ ] 数据统计仪表盘
- [ ] 导出个人作品集
- [ ] 多语言支持

---

## 📖 使用指南

### 创建卡片
1. 进入"我的主页"
2. 点击"+ 添加卡片"
3. 选择卡片类型（文本/图片/代码/链接等）
4. 填写标题、描述、内容
5. 勾选"公开到社区广场"分享给其他人
6. 点击"创建"完成

### 批量管理
1. 点击"☑️ 批量删除"进入选择模式
2. 点击卡片上的复选框选择
3. 点击"全选"快速选择所有卡片
4. 点击"🗑️ 删除"批量删除

### 搜索与发现
1. 在社区广场搜索框输入关键词
2. 点击"搜索"按钮查找相关卡片
3. 切换"最新"/"最热"排序方式
4. 点击卡片查看详情、点赞、评论

### 个人资料
1. 点击"👤 个人资料"
2. 上传头像（支持图片<2MB）
3. 编辑昵称、邮箱、性别、生日等信息
4. 点击"保存"更新资料

---

## 🐛 问题反馈

如果遇到问题，请查看：
- `BUGS_FIXED.md` - 已修复的问题列表
- `NEW_FEATURES_GUIDE.md` - 新功能使用指南
- `QUICK_DEPLOY.md` - 快速部署指南

---

## 📄 License

MIT License

---

## 👥 贡献者

本项目是毕业设计作品，持续更新中。

### 技术栈总结
- **前端**: Vue 3 + TypeScript + Vite + Tailwind CSS
- **后端**: Spring Boot 3 + MyBatis-Plus + MySQL 8
- **认证**: JWT Token
- **特色**: Warm Bento Grid 设计风格

---

## 🙏 致谢

感谢所有开源项目和工具的支持！

---

**Built with ❤️ and ☕**

*最后更新：2026-01-25*
