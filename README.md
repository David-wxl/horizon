# HORIZON | 地平线

> 视觉优先的个人技术堡垒 - 结合双轨知识树与 AI 简历诊断的沉浸式 3D 交互平台

---

## 🎯 项目介绍

HORIZON 是一个创新的全栈 Web 应用，旨在打造一个视觉化的个人技术学习与成长平台。

### 核心特性

- **🌳 双轨知识树系统**
  - **左侧 - 总纲「Master Blueprint」**: 全局只读，标准学习路径，所有用户共享
  - **右侧 - 第二大脑「Second Brain」**: 用户专属，可自由编辑，无限层级结构
  - 采用递归数据结构，支持无限深度的知识组织

- **🤖 AI 简历工坊**
  - 基于 Google Gemini API 的智能简历分析
  - 实时生成雷达图评分与优化建议
  - 无状态设计，保护用户隐私

- **🎨 沉浸式 3D 体验**
  - Tres.js 驱动的粒子流动效果
  - 鼠标交互式动画
  - Glassmorphism 2.0 毛玻璃美学

- **📚 文章中心**
  - 技术文章、教程、学习资源
  - 富文本编辑器支持
  - 完整的 CRUD 操作

---

## 🛠️ 技术栈

### 前端 (`/frontend`)
- **框架**: Vue 3 (Composition API, `<script setup>`)
- **构建工具**: Vite 4.x
- **语言**: TypeScript
- **样式**: Tailwind CSS (原子化优先)
- **3D/动画**: Tres.js + Framer Motion
- **图表**: ECharts 5.0

### 后端 (`/backend`)
- **框架**: Spring Boot 3.2.1
- **语言**: Java 17+
- **数据库**: MySQL 8.0
- **ORM**: MyBatis-Plus 3.5.5
- **认证**: JWT (jjwt 0.12.3)
- **AI**: Google Gemini API

---

## 📁 项目结构

```
horizon/
├── README.md           # 项目总说明（当前文件）
├── .cursorrules        # AI 开发规范
├── .gitignore          # Git 忽略配置
│
├── frontend/           # 前端项目
│   ├── src/
│   │   ├── components/     # Vue 组件
│   │   ├── composables/    # 组合式函数
│   │   ├── views/          # 页面视图
│   │   ├── App.vue         # 根组件
│   │   └── main.ts         # 入口文件
│   ├── public/             # 静态资源
│   ├── index.html
│   ├── package.json
│   ├── vite.config.ts
│   ├── tailwind.config.js
│   └── tsconfig.json
│
└── backend/            # 后端项目
    ├── src/main/java/com/horizon/
    │   ├── config/             # 配置类
    │   ├── controller/         # 控制器
    │   ├── service/            # 业务逻辑
    │   ├── mapper/             # 数据访问
    │   ├── entity/             # 实体类
    │   ├── dto/                # 数据传输对象
    │   ├── common/             # 公共类
    │   ├── util/               # 工具类
    │   └── exception/          # 异常处理
    ├── src/main/resources/
    │   ├── application.yml     # 应用配置
    │   └── sql/init.sql        # 数据库初始化
    ├── pom.xml
    └── README.md
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

### 2. 启动后端

```bash
# 进入后端目录
cd backend

# 初始化数据库
mysql -u root -p < src/main/resources/sql/init.sql

# 修改配置文件
# 编辑 src/main/resources/application.yml
# 修改 MySQL 密码和其他配置

# 启动后端服务
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080/api` 启动

### 3. 启动前端

```bash
# 新开终端，进入前端目录
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端服务将在 `http://localhost:5173` 启动

---

## 🎨 设计规范

### 色彩系统
- **背景渐变**: Deep Purple → Dark Blue `linear-gradient(135deg, #1e1b4b 0%, #1e3a8a 50%, #0f172a 100%)`
- **主色调**: Purple `#a855f7` / Cyan `#06b6d4`
- **文字**: Primary `#ffffff` / Secondary `#cbd5e1`

### UI 原则
1. **Glassmorphism 2.0** - 所有卡片使用毛玻璃效果
2. **3D 粒子流** - 首页必备的视觉特效
3. **流畅动画** - 0.6-0.8s 过渡时长
4. **大胆留白** - 充足的内边距和间距

详细设计规范请查看 [.cursorrules](./cursorrules) 文件。

---

## 📡 核心 API

### 用户相关
- `POST /api/user/register` - 用户注册
- `POST /api/user/login` - 用户登录
- `GET /api/user/test` - 测试接口

### 知识树相关
- `GET /api/knowledge/master/tree` - 获取主蓝图
- `GET /api/knowledge/user/tree` - 获取用户树
- `POST /api/knowledge/node` - 创建节点
- `PUT /api/knowledge/node/{id}` - 更新节点
- `DELETE /api/knowledge/node/{id}` - 删除节点

---

## 📝 开发规范

### 前端
- 使用 Vue 3 Composition API (`<script setup>`)
- 复杂逻辑提取到 `composables/` 目录
- 优先使用 Tailwind 原子类
- Element Plus 仅用于后台管理面板

### 后端
- Controller → Service → Mapper 三层架构
- 使用 DTO 进行数据传输
- 为复杂业务逻辑添加注释

### Git 提交
- 使用中文提交信息：`feat: 添加xxx功能` / `fix: 修复xxx问题`
- 频繁提交，每个功能点一个提交
- 绝不提交敏感数据（密码、API Key）

---

## 📊 数据库设计

### 用户表 (t_user)
- 支持用户注册、登录
- JWT Token 认证
- 密码 MD5 加密

### 知识树节点表 (t_knowledge_node)
- 递归结构设计 (`parent_id`)
- 双轨系统 (`node_type`: 0=主蓝图, 1=第二大脑)
- 用户隔离 (`user_id`)

---

## 🎯 开发路线图

- [x] 后端基础架构搭建
- [x] 用户注册登录系统
- [x] 知识树数据库设计
- [x] 知识树 CRUD 接口
- [ ] 前端登录页面
- [ ] 知识树可视化展示
- [ ] Gemini API 集成
- [ ] 前后端联调测试
- [ ] 3D 粒子效果优化
- [ ] 响应式设计完善

---

## 📄 License

MIT License

---

## 👥 贡献者

本项目是毕业设计作品。

---

**Built with ❤️ for the future of learning**
