# 🚀 地平线 | 视界 启动指南

> 快速启动前后端服务并验证连接

---

## 📋 前置准备

确保以下环境已安装：
- ✅ MySQL 8.0+ (已启动)
- ✅ JDK 17+
- ✅ Maven 3.6+
- ✅ Node.js 18+

---

## 步骤 1: 初始化数据库

```bash
# 登录 MySQL
mysql -u root -p

# 执行以下 SQL（或直接执行脚本）
source D:/graduation-project/horizon/backend/src/main/resources/sql/init.sql

# 或者手动复制执行 backend/src/main/resources/sql/init.sql 的内容
```

---

## 步骤 2: 配置后端

编辑 `backend/src/main/resources/application.yml`

**必须修改的配置：**
```yaml
spring:
  datasource:
    password: your_password_here  # 改为您的 MySQL 密码
```

**可选配置：**
```yaml
jwt:
  secret: YOUR_SECRET_KEY  # 生产环境建议修改

gemini:
  api-key: your_key  # 暂时不需要
```

---

## 步骤 3: 启动后端服务

**打开第一个终端：**

```bash
# 进入后端目录
cd D:\graduation-project\horizon\backend

# 启动后端（方式1：Maven）
mvn spring-boot:run

# 或者方式2：先打包再运行
mvn clean package
java -jar target/horizon-backend-1.0.0.jar
```

**看到以下输出表示启动成功：**
```
╔═══════════════════════════════════════╗
║   HORIZON Backend Started Successfully   ║
║   Port: 8080                          ║
║   Context: /api                       ║
╚═══════════════════════════════════════╝
```

**测试后端：**
打开浏览器访问：http://localhost:8080/api/user/test
应该看到：`{"code":200,"message":"success","data":"HORIZON Backend is running!"}`

---

## 步骤 4: 启动前端服务

**打开第二个终端：**

```bash
# 进入前端目录
cd D:\graduation-project\horizon\frontend

# 安装依赖（首次运行）
npm install

# 启动前端开发服务器
npm run dev
```

**看到以下输出表示启动成功：**
```
  VITE v4.x.x  ready in xxx ms

  ➜  Local:   http://localhost:5173/
  ➜  Network: use --host to expose
```

---

## 步骤 5: 验证前后端连接

1. **打开浏览器：** http://localhost:5173

2. **查看页面：**
   - 应该看到 "HORIZON" 标题与 Warm Bento Grid 页面
   - "前端服务" 应该显示 ✅ **运行中**（绿灯）
   - "后端服务" 应该显示 ✅ **已连接**（绿灯）
   - 底部显示后端响应消息：`HORIZON Backend is running!`

3. **如果后端显示红灯：**
   - 检查后端是否已启动（步骤3）
   - 检查端口 8080 是否被占用
   - 查看浏览器控制台的错误信息
   - 点击"重新测试连接"按钮

---

## ✅ 成功标志

看到以下界面表示前后端已打通：

```
┌─────────────────────────────────────┐
│  HORIZON                            │
│  地平线 · 视觉优先的个人技术堡垒   │
│                                     │
│  ┌─ 前后端连接测试 ─────────────┐ │
│  │                                 │ │
│  │  前端服务    🟢 运行中         │ │
│  │  后端服务    🟢 已连接         │ │
│  │                                 │ │
│  │  ✅ HORIZON Backend is running! │ │
│  │                                 │ │
│  └─────────────────────────────────┘ │
└─────────────────────────────────────┘
```

---

## 🎯 下一步

前后端打通后，可以：
1. **提交到 Git** - 保存当前进度
2. **添加功能** - 开发 Bento 编辑器、社区广场等
3. **优化体验** - 完善 Warm Bento Grid UI 与动效

---

## 🔧 常见问题

### Q1: 后端启动失败 "Could not create connection to database"
**A:** 检查 MySQL 是否启动，密码是否正确

### Q2: 前端无法连接后端 "fetch failed"
**A:** 检查后端是否在 8080 端口运行，CORS 配置是否正确

### Q3: 端口被占用
**A:** 修改端口号：
- 后端：`application.yml` 中的 `server.port`
- 前端：`vite.config.ts` 中的 `server.port`

---

**准备好了吗？开始启动吧！** 🚀
