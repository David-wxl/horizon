# 📝 地平线 | 视界 - 下次开发指南

> 快速恢复开发环境和继续工作

---

## 🎯 项目当前状态

### ✅ 已完成功能
1. ✅ 项目结构：frontend/ + backend/ monorepo 结构
2. ✅ 后端架构：Spring Boot 3.1.5 + MyBatis-Plus + JWT
3. ✅ 数据库：MySQL (horizon 数据库，2个表)
4. ✅ 前后端连接：测试页面已打通
5. ✅ 前端登录/注册页面（Warm Bento Grid 风格）
6. ✅ 代码托管：已推送到 GitHub

### ⏳ 待开发功能
1. ⏳ Bento 布局编辑器（WYSIWYG）
2. ⏳ 内容自动构建器（图像/代码/书影音）
3. ⏳ 社区广场（The Square）
4. ⏳ 多租户与管理后台
5. ⏳ 视觉细节优化

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
你好！我正在开发「地平线 | 视界」毕业设计项目。

【项目背景】
- 项目位置：D:\graduation-project\horizon
- 技术栈：Vue 3 + Spring Boot + MySQL
- 已完成：前后端基础架构、数据库设计、连接测试

【当前状态】
前后端服务已打通，现在要继续开发 Bento 策展核心功能。

【开发要求】
1. 请先阅读 @.cursorrules 了解项目规范
2. 严格遵循 Warm Bento Grid 设计风格
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

继续开发「地平线 | 视界」项目，实现完整的用户登录注册系统。

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
- 使用 Warm Bento Grid 风格
- 暖色渐变按钮（yellow -> orange -> soft grey）
- 大圆角（rounded-3xl）
- 输入框 focus 柔和高亮
- 暂不加入 3D 粒子背景

【技术要求】
- Vue 3 Composition API
- TypeScript 类型定义
- 使用已有的 API 封装（src/api/request.ts）

请开始实现！
```

---

### 🧩 功能2：Bento 布局编辑器（WYSIWYG）

```
@.cursorrules

继续开发「地平线 | 视界」项目，实现 Bento 布局编辑器与所见即所得体验。

【需求说明】
1. 创建用户主页编辑器
2. 拖拽调整布局与尺寸（1x1 / 1x2 / 2x2）
3. 空白处“+”按钮新增卡片
4. 编辑与展示一键切换（上帝模式）
5. 初期可用静态数据模拟

【设计要求】
- Warm Bento Grid 玻璃卡片
- 丰富留白与层级
- 拖拽时有弹性回馈
- 进入页面时错落浮现

【技术要求】
- 封装 composables 处理布局与卡片状态
- TypeScript 类型定义

请开始实现！
```

---

### 🧱 功能3：内容自动构建器

```
@.cursorrules

继续开发「地平线 | 视界」项目，实现内容自动构建器。

【需求说明】
1. 上传图片自动生成 OOTD 卡片
2. 输入代码自动高亮并生成代码卡片
3. 录入书影音信息生成展示卡片

【后端要求】
- 保留基础上传/解析接口（后续扩展）

【前端要求】
- 统一卡片展示规范
- 支持媒体与代码渲染
- Warm Bento Grid 卡片设计

【API 配置】
- 暂不启用 AI 服务，可留占位

请开始实现！
```

---

### 🌍 功能4：社区广场（The Square）

```
@.cursorrules

继续开发「地平线 | 视界」项目，实现社区化广场。

【需求说明】
1. 公共广场瀑布流/网格墙
2. 展示用户卡片合集
3. 进入用户主页
4. 支持点赞与查看详情

【技术要求】
- 使用 Tailwind 细化网格布局
- 响应式适配

【设计要求】
- Warm Bento Grid 统一视觉
- 动效克制

请实现社区广场页面！
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

### 📍 阶段2：Bento 编辑器核心功能
**目标**：所见即所得编辑 + 自由布局  
**提示词**：复制上面"功能2：Bento 布局编辑器"的提示词  
**预计时间**：2-3 小时

---

### 📍 阶段3：内容自动构建器
**目标**：图片/代码/书影音卡片自动生成  
**提示词**：复制上面"功能3：内容自动构建器"的提示词  
**预计时间**：2-3 小时

---

### 📍 阶段4：社区广场
**目标**：广场瀑布流/网格展示  
**提示词**：复制上面"功能4：社区广场"的提示词  
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
- CSS Transition / Vue Transition

### 后端
- Spring Boot 3.1.5
- MyBatis-Plus 3.5.7
- MySQL 8.0
- JWT 认证

---

## 🎨 设计规范快速参考

- **主题**：Warm Bento Grid
- **主色调**：Warm Yellow / Orange / Soft Grey
- **圆角**：rounded-3xl
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
