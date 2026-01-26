# 快速部署指南

## 📋 环境要求

```
Node.js: 18+
JDK: 17+
Maven: 3.6+
MySQL: 8.0+
```

---

## 🚀 5步快速部署

### 第1步：克隆项目

```bash
git clone <your-repo-url>
cd horizon
```

### 第2步：初始化数据库

在MySQL中执行：

```sql
-- 创建数据库
CREATE DATABASE horizon CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 导入表结构
USE horizon;
SOURCE backend/src/main/resources/sql/init.sql;
```

### 第3步：配置后端

编辑 `backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/horizon
    username: root
    password: 你的数据库密码  # 修改这里
```

### 第4步：启动后端

```bash
cd backend

# Windows
.\START_BACKEND.bat

# 或使用Maven
mvn spring-boot:run
```

✅ 后端启动成功：`http://localhost:8080/api`

### 第5步：启动前端

```bash
cd frontend
npm install
npm run dev
```

✅ 前端启动成功：`http://localhost:5173`

---

## 🎉 完成！

打开浏览器访问 `http://localhost:5173`

**首次使用**：
1. 点击"立即注册"创建账号
2. 登录后自动跳转到社区广场
3. 点击"我的主页"开始创建你的第一张卡片

---

## 🔧 常见问题

### 后端启动失败

**问题1：端口被占用**
```bash
# 查找占用8080端口的进程
netstat -ano | findstr :8080

# 结束进程
taskkill /PID <进程ID> /F
```

**问题2：数据库连接失败**
- 检查MySQL服务是否启动
- 检查数据库用户名密码
- 检查数据库是否已创建

### 前端启动失败

**问题：依赖安装失败**
```bash
# 清除缓存重新安装
npm cache clean --force
npm install
```

### 功能异常

**搜索结果不正确**
- 强制刷新浏览器：`Ctrl + Shift + R`
- 清除浏览器缓存

**头像上传失败**
- 确保图片小于2MB
- 确认数据库avatar字段为LONGTEXT类型

---

## 📊 验证部署

访问以下页面确认部署成功：

- ✅ 社区广场：`http://localhost:5173/square`
- ✅ 登录页：`http://localhost:5173/login`
- ✅ API测试：`http://localhost:8080/api/user/test`

---

## 🔄 重启服务

### 重启后端
```bash
cd backend
.\RESTART_BACKEND.bat
```

### 重启前端
```bash
# 停止：Ctrl + C
# 重新启动
npm run dev
```

---

## 📝 生产环境部署

### 前端构建
```bash
cd frontend
npm run build
```

构建产物在 `frontend/dist` 目录，可部署到：
- Nginx
- Apache
- Vercel
- Netlify

### 后端打包
```bash
cd backend
mvn clean package
```

生成的JAR包在 `backend/target` 目录，运行：
```bash
java -jar horizon-*.jar
```

---

## 🌐 反向代理配置（可选）

### Nginx配置示例

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # 前端
    location / {
        root /path/to/frontend/dist;
        try_files $uri $uri/ /index.html;
    }

    # 后端API
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

---

**部署文档版本**：1.0  
*最后更新：2026-01-26*
