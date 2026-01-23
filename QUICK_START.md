# 🚀 快速启动指南

> 数据库初始化 + 项目启动

---

## 📊 步骤 1：初始化数据库

### 方法 1：使用 MySQL Workbench（推荐）

1. **打开 MySQL Workbench**
2. **连接到本地数据库**（用户名：root，密码：Wxl576566）
3. **执行以下 SQL**：

```sql
-- 删除旧数据库
DROP DATABASE IF EXISTS horizon;

-- 执行初始化脚本（点击 File -> Run SQL Script）
-- 选择文件：D:\graduation-project\horizon\backend\src\main\resources\sql\init.sql
```

4. **验证结果**：

```sql
USE horizon;
SHOW TABLES;

-- 应该看到 6 张表：
-- t_user, t_user_profile, t_bento_card, t_like, t_comment, t_follow
```

---

### 方法 2：使用命令行

打开 **MySQL 命令行客户端**（MySQL 8.0 Command Line Client）：

```sql
-- 1. 删除旧数据库
DROP DATABASE IF EXISTS horizon;

-- 2. 导入新数据库
SOURCE D:/graduation-project/horizon/backend/src/main/resources/sql/init.sql;

-- 3. 验证
USE horizon;
SHOW TABLES;
SELECT * FROM t_user;
SELECT * FROM t_bento_card;
```

---

## 🔥 步骤 2：启动后端

打开终端（PowerShell 或 CMD）：

```bash
cd D:\graduation-project\horizon\backend
mvn spring-boot:run
```

**成功标志：**
```
╔═══════════════════════════════════════╗
║   HORIZON Backend Started Successfully   ║
║   Port: 8080                          ║
║   Context: /api                       ║
╚═══════════════════════════════════════╝
```

---

## 🎨 步骤 3：启动前端

**新开一个终端**：

```bash
cd D:\graduation-project\horizon\frontend
npm run dev
```

**成功标志：**
```
  ➜  Local:   http://localhost:5173/
```

---

## ✅ 步骤 4：验证连接

打开浏览器访问：**http://localhost:5173**

应该看到：
- ✅ 前端服务：运行中（绿灯）
- ✅ 后端服务：已连接（绿灯）
- ✅ 显示消息：`HORIZON Backend is running!`

---

## 🧪 测试新 API

### 1. 测试用户注册

```bash
curl -X POST http://localhost:8080/api/user/register ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"test_user\",\"password\":\"123456\",\"email\":\"test@horizon.com\",\"nickname\":\"测试用户\"}"
```

### 2. 测试登录

```bash
curl -X POST http://localhost:8080/api/user/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"demo_user\",\"password\":\"123456\"}"
```

### 3. 测试获取用户配置

```bash
curl http://localhost:8080/api/profile/1
```

### 4. 测试创建卡片

```bash
curl -X POST http://localhost:8080/api/card/create ^
  -H "Content-Type: application/json" ^
  -d "{\"userId\":1,\"title\":\"测试卡片\",\"cardType\":\"text\",\"content\":\"{\\\"markdown\\\":\\\"Hello Bento!\\\"}\",\"gridX\":0,\"gridY\":0,\"gridWidth\":1,\"gridHeight\":1}"
```

### 5. 测试获取广场卡片

```bash
curl http://localhost:8080/api/card/square?pageNum=1^&pageSize=10
```

---

## 📝 测试账号

数据库初始化后会自动创建测试账号：

- **用户名**：`demo_user`
- **密码**：`123456`
- **测试卡片**：4 张（图片、代码、文本、书影音）

---

## ⚠️ 常见问题

### Q1: 数据库连接失败

**解决**：
1. 确认 MySQL 服务已启动
2. 检查密码是否为：`Wxl576566`
3. 修改 `backend/src/main/resources/application.yml` 中的密码配置

### Q2: 后端启动报错 "Invalid bound statement"

**解决**：
```bash
cd backend
mvn clean compile
mvn spring-boot:run
```

### Q3: 端口被占用

**解决**：
- 后端 8080 端口：修改 `application.yml` 中的 `server.port`
- 前端 5173 端口：修改 `vite.config.ts` 中的 `server.port`

---

## 🎯 下一步开发

数据库初始化完成后，可以开始开发：

1. **Bento 编辑器**（WYSIWYG）
2. **内容自动构建器**
3. **社区广场**（The Square）
4. **用户个人主页**

---

**准备好了吗？开始启动吧！** 🚀
