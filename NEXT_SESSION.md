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

## 💬 如何告诉 AI 继续开发

### 方式1：明确告诉当前状态（推荐）
```
你好，我是 HORIZON 项目的开发者。
项目已完成前后端基础架构和连接测试。
现在我想继续开发 [具体功能]。

项目位置：D:\graduation-project\horizon
参考文件：.cursorrules, README.md
```

### 方式2：简短说明
```
继续开发 HORIZON 项目，实现登录注册页面
```

### 方式3：附带文件引用
```
@.cursorrules
继续开发 HORIZON，请先阅读项目规范
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

## 🎯 下次开发建议

### 功能开发优先级（按顺序）：
1. **登录注册页面** - 完善用户系统
2. **知识树展示** - 核心功能，先简单版本
3. **AI 简历诊断** - 集成 Gemini API
4. **3D 效果优化** - 视觉增强

### 开发时的沟通方式：
```
我想实现 [功能名称]，请：
1. 先查看 .cursorrules 中的相关规范
2. 然后帮我实现，注意 [具体要求]
```

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
