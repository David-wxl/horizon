# 📝 HORIZON - 下次开发指南

> 快速恢复开发环境和继续工作

---

## 🎯 项目当前状态

### ✅ 已完成功能
1. ✅ 项目结构：frontend/ + backend/ monorepo 结构
2. ✅ 后端架构：Spring Boot 3.1.5 + MyBatis-Plus + JWT
3. ✅ 数据库：MySQL (horizon 数据库，2个表)
4. ✅ 前后端连接：测试页面已打通
5. ✅ 代码托管：已推送到 GitHub

### ⏳ 待开发功能
1. ⏳ 登录注册页面（Login.vue 已创建，待集成）
2. ⏳ 知识树可视化展示
3. ⏳ AI 简历诊断功能
4. ⏳ 3D 粒子效果优化

---

## 🚀 下次启动服务

### 步骤1：启动后端（终端1）
```bash
cd D:\graduation-project\horizon\backend
mvn spring-boot:run
```

⚠️ **首次启动前记得修改：**
- `backend/src/main/resources/application.yml`
- 将 `password: your_password_here` 改为您的实际密码：`Wxl576566`

### 步骤2：启动前端（终端2）
```bash
cd D:\graduation-project\horizon\frontend
npm run dev
```

### 步骤3：访问应用
- 测试页面：http://localhost:5173
- 后端 API：http://localhost:8080/api/user/test

---

## 💬 提示词模板（直接复制使用）

### 📋 通用开发提示词

```
你好！我正在开发 HORIZON 毕业设计项目。

【项目背景】
- 项目位置：D:\graduation-project\horizon
- 技术栈：Vue 3 + Spring Boot + MySQL
- 已完成：前后端基础架构、数据库设计、连接测试

【当前状态】
前后端服务已打通，现在要继续开发新功能。

【开发要求】
1. 请先阅读 @.cursorrules 了解项目规范
2. 严格遵循 Glassmorphism 设计风格
3. 代码要有注释，清理未使用的导入
4. 开发完成后帮我提交到 Git

【本次目标】
实现 [具体功能名称]

请开始吧！
```

---

### 🎨 功能1：登录注册页面

```
@.cursorrules

继续开发 HORIZON 项目，实现完整的用户登录注册系统。

【需求说明】
1. 创建登录页面（/login）
2. 创建注册页面（或登录注册一体页）
3. 集成后端 API：
   - POST /api/user/login
   - POST /api/user/register
4. 实现表单验证
5. Token 存储到 localStorage
6. 登录成功后跳转到主页

【设计要求】
- 使用 Glassmorphism 毛玻璃风格
- 渐变按钮（purple -> pink -> cyan）
- 大圆角（rounded-3xl）
- 输入框要有 focus 发光效果
- 包含 3D 粒子背景

【技术要求】
- Vue 3 Composition API
- TypeScript 类型定义
- 使用已有的 API 封装（src/api/request.ts）

请开始实现！
```

---

### 🌳 功能2：知识树展示

```
@.cursorrules

继续开发 HORIZON 项目，实现知识树可视化展示功能。

【需求说明】
1. 创建知识树页面组件
2. 展示双轨系统：
   - 左侧：主蓝图（全局只读）
   - 右侧：第二大脑（用户可编辑）
3. 调用后端 API：
   - GET /api/knowledge/master/tree（获取主蓝图）
   - GET /api/knowledge/user/tree?userId=xxx（获取用户树）
4. 树形结构展示（可折叠/展开）
5. 节点点击查看内容

【设计要求】
- 树形左右布局，左右各占 50%
- 节点卡片使用 Glassmorphism 风格
- 不同层级用缩进表示
- 暂时不考虑 3D 效果，用简单的 DOM 树即可
- 支持展开/折叠动画

【技术要求】
- 创建 composables/useKnowledgeTree.ts 处理业务逻辑
- 递归组件实现树形结构
- TypeScript 类型定义

请开始实现！
```

---

### 🤖 功能3：AI 简历诊断

```
@.cursorrules

继续开发 HORIZON 项目，集成 Google Gemini API 实现 AI 简历诊断功能。

【需求说明】
1. 后端集成 Gemini API
2. 创建简历诊断接口：POST /api/resume/analyze
3. 前端创建简历诊断页面
4. 用户输入简历（Markdown 或纯文本）
5. 调用 Gemini API 分析
6. 返回雷达图评分 + 优化建议

【后端要求】
- 使用 OkHttp 调用 Gemini API
- 解析 API 响应，提取关键信息
- 返回结构化数据（技能评分、建议等）

【前端要求】
- 大文本输入框（支持 Markdown）
- 使用 ECharts 绘制雷达图
- 展示 AI 分析结果
- Glassmorphism 卡片设计

【API 配置】
- 在 application.yml 中配置 Gemini API Key
- 支持代理配置

请开始实现！
```

---

### 🎨 功能4：优化 3D 粒子效果

```
@.cursorrules

继续开发 HORIZON 项目，优化首页的 3D 粒子交互效果。

【需求说明】
1. 使用 Tres.js（Three.js for Vue）
2. 实现彩虹色粒子（红→橙→黄→绿→青→蓝→紫）
3. 鼠标交互：悬停时粒子排斥/吸引
4. 500-1000 个粒子
5. 流畅的缓动动画

【技术要求】
- 使用 InstancedMesh 优化性能
- 鼠标位置跟踪
- 粒子速度：ease-out-cubic
- 响应式适配

【设计要求】
- 粒子大小：小而密集
- 运动轨迹：自然、流畅
- 背景与前景内容要融合

请优化 ParticleBackground.vue 组件！
```

---

### 🔧 功能5：修复问题/Debug

```
HORIZON 项目遇到问题，请帮我排查：

【问题描述】
[详细描述问题现象]

【错误信息】
[粘贴完整错误日志]

【已尝试的解决方案】
[列出已经尝试过的方法]

【项目信息】
- 位置：D:\graduation-project\horizon
- 后端：Spring Boot 3.1.5
- 前端：Vue 3 + Vite
- 数据库：MySQL 8.0

请帮我分析并解决！
```

---

## 📂 重要文件位置

| 文件 | 路径 | 说明 |
|------|------|------|
| 项目规范 | `.cursorrules` | AI 开发规范（必读） |
| 项目总览 | `README.md` | 项目介绍和技术栈 |
| 启动指南 | `STARTUP.md` | 详细启动步骤 |
| 后端配置 | `backend/src/main/resources/application.yml` | 数据库密码等配置 |
| 数据库脚本 | `backend/src/main/resources/sql/init.sql` | 数据库初始化 |

---

## 🎯 推荐开发顺序

### 📍 阶段1：完善用户系统（明天开始）
**目标**：实现登录注册功能  
**提示词**：复制上面"功能1：登录注册页面"的提示词  
**预计时间**：1-2 小时

---

### 📍 阶段2：知识树核心功能
**目标**：展示双轨知识树（主蓝图 + 第二大脑）  
**提示词**：复制上面"功能2：知识树展示"的提示词  
**预计时间**：2-3 小时

---

### 📍 阶段3：AI 简历诊断
**目标**：集成 Gemini API 实现智能简历分析  
**提示词**：复制上面"功能3：AI 简历诊断"的提示词  
**预计时间**：2-3 小时

---

### 📍 阶段4：视觉优化
**目标**：优化 3D 粒子交互效果  
**提示词**：复制上面"功能4：优化 3D 粒子效果"的提示词  
**预计时间**：1-2 小时

---

### 💡 使用技巧

1. **每次开发前**：先启动后端和前端服务
2. **复制提示词**：直接复制对应功能的完整提示词
3. **附加文件引用**：使用 `@.cursorrules` 让 AI 读取规范
4. **描述要清晰**：说明具体需求和预期效果
5. **开发完成后**：让 AI 帮你提交到 Git

---

## 🔧 常用命令速查

### Git 操作
```bash
# 查看状态
git status

# 提交代码
git add .
git commit -m "feat: your message here"
git push

# 拉取最新代码
git pull
```

### 后端操作
```bash
cd backend
mvn spring-boot:run          # 启动
mvn clean package            # 打包
```

### 前端操作
```bash
cd frontend
npm install                  # 安装依赖
npm run dev                  # 开发模式
npm run build                # 生产构建
```

---

## 📊 项目技术栈提醒

### 前端
- Vue 3 (Composition API)
- TypeScript
- Tailwind CSS
- Tres.js (3D)

### 后端
- Spring Boot 3.1.5
- MyBatis-Plus 3.5.7
- MySQL 8.0
- JWT 认证

---

## 🎨 设计规范快速参考

- **主题**：深色 Glassmorphism
- **主色调**：Purple (#a855f7) + Cyan (#06b6d4)
- **圆角**：rounded-2xl 或 rounded-3xl
- **间距**：p-12, p-16, gap-8
- **动画**：0.6-0.8s duration

详见 `.cursorrules` 文件。

---

## 💡 提示

1. **每次开发前**：先启动后端和前端服务
2. **提交代码前**：确保测试通过，删除敏感信息
3. **遇到问题**：查看 `STARTUP.md` 常见问题部分

---

**明天见！继续加油！** 🚀

*Last updated: 2026-01-21*
