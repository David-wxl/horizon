# 地平线 | 视界

> SaaS 级个人数字策展平台 - 基于 Warm Bento Grid 的所见即所得展示与社区广场

---

## 🎯 项目介绍

「地平线 | 视界」是一个 SaaS 级个人数字策展平台，旨在打破传统博客的列表形式，让用户通过高度自由的 Bento Grid 像拼图一样拼装生活切片，构建一流的个人数字展览馆。

### 核心特性

- **🎨 极致暖色 Bento Grid**
  - 玻璃拟态卡片、超大圆角与柔和阴影
  - 高可读性与留白节奏

- **✍️ 沉浸式所见即所得编辑**
  - 登录后直接进入个人主页
  - “编辑/展示”一键切换，上帝模式自由排布

- **🧩 内容自动构建器**
  - 上传图片/输入代码/添加书影音 → 自动生成卡片
  - 支持代码高亮与媒体展示

- **🌍 社区化广场（The Square）**
  - 瀑布流/网格展示用户展览馆
  - 点赞、查看卡片详情、访问个人主页

- **🏢 多租户 SaaS 架构**
  - 统一审美语言，多租户数据隔离
  - 支持未来的企业与团队扩展

---

## 🛠️ 技术栈

### 前端 (`/frontend`)
- **框架**: Vue 3 (Composition API, `<script setup>`)
- **构建工具**: Vite 4.x
- **语言**: TypeScript
- **样式**: Tailwind CSS (原子化优先)
- **动画**: CSS Transition / Vue Transition（必要时）
- **图表**: ECharts 5.0（用于统计/仪表盘）

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
- **背景渐变**: Warm Oatmeal `linear-gradient(135deg, #f5f5f0 0%, #efece1 50%, #f7f1e8 100%)`
- **主色调**: Warm Yellow / Orange / Soft Grey
- **文字**: High contrast, readable

### UI 原则
1. **Warm Bento Grid** - 模块化卡片 + 清晰层级
2. **柔和玻璃拟态** - 高透明度与柔和阴影
3. **流畅动画** - 0.6-0.8s 过渡时长
4. **大胆留白** - 充足的内边距和间距

详细设计规范请查看 [.cursorrules](./cursorrules) 文件。

---

## 📡 核心 API
> 当前接口以用户与基础模块为主，后续将扩展为 Bento 策展内容与社区广场能力。

### 用户相关
- `POST /api/user/register` - 用户注册
- `POST /api/user/login` - 用户登录
- `GET /api/user/test` - 测试接口

### 知识树相关（当前阶段保留的基础模块）
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
- 作为当前阶段基础模块保留，后续将扩展为 Bento 内容模型

---

## 🎯 开发路线图

- [x] 后端基础架构搭建
- [x] 用户注册登录系统
- [x] 基础模块（知识树）接口与数据结构
- [x] 前端登录页面（Warm Bento Grid）
- [ ] WYSIWYG 编辑与 Bento 布局
- [ ] 内容自动构建器（图片/代码/书影音）
- [ ] 社区化广场（The Square）
- [ ] 多租户与管理后台
- [ ] 前后端联调测试
- [ ] 视觉细节与动效优化

---

## 📄 License

MIT License

---

## 👥 贡献者

本项目是毕业设计作品。

---

**Built with ❤️ for the future of learning**
