# 地平线 | 视界 Backend | 后端服务

> 本目录是「地平线 | 视界」的后端部分，基于 Spring Boot 3.x。平台定位为 SaaS 级个人数字策展平台（多租户）。

---

## 📋 快速开始

### 1. 数据库初始化

```bash
# 登录 MySQL
mysql -u root -p

# 执行初始化脚本
source src/main/resources/sql/init.sql
```

### 2. 配置文件

修改 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    username: root
    password: your_password_here  # 修改为您的MySQL密码

jwt:
  secret: YOUR_SECRET_KEY  # 修改为安全的随机字符串

gemini:
  api-key: your_gemini_api_key_here  # 配置 Gemini API Key（可选）
```

### 3. 启动服务

```bash
# 方式1：Maven 直接运行
mvn spring-boot:run

# 方式2：打包后运行
mvn clean package
java -jar target/horizon-backend-1.0.0.jar
```

服务地址：`http://localhost:8080/api`

---

## 📡 API 文档

> 说明：当前接口以基础用户与知识树模块为主，后续会扩展为 Bento 策展内容与社区广场相关能力。

### 用户相关

#### 注册
- **URL**: `POST /api/user/register`
- **请求体**:
```json
{
  "username": "testuser",
  "password": "123456",
  "email": "test@example.com",
  "nickname": "测试用户"
}
```

#### 登录
- **URL**: `POST /api/user/login`
- **请求体**:
```json
{
  "username": "testuser",
  "password": "123456"
}
```
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6...",
    "user": {
      "id": 1,
      "username": "testuser",
      "nickname": "测试用户",
      "email": "test@example.com"
    }
  }
}
```

### 知识树相关

#### 获取主蓝图
- **URL**: `GET /api/knowledge/master/tree`

#### 获取用户树
- **URL**: `GET /api/knowledge/user/tree?userId=1`

#### 创建节点
- **URL**: `POST /api/knowledge/node`
- **请求体**:
```json
{
  "title": "Vue 3 基础",
  "content": "Vue 3 核心概念学习",
  "parentId": 1,
  "nodeType": 1,
  "userId": 1,
  "sortOrder": 1
}
```

---

## 📁 目录结构

```
backend/
├── src/main/java/com/horizon/
│   ├── HorizonApplication.java    # 主启动类
│   ├── config/                    # 配置类
│   ├── controller/                # 控制器
│   ├── service/                   # 业务逻辑
│   ├── mapper/                    # 数据访问
│   ├── entity/                    # 实体类
│   ├── dto/                       # 数据传输对象
│   ├── common/                    # 公共响应
│   ├── util/                      # 工具类
│   └── exception/                 # 全局异常处理
├── src/main/resources/
│   ├── application.yml            # 配置文件
│   └── sql/init.sql               # 数据库初始化
└── pom.xml                        # Maven 依赖
```

---

## 🔧 技术说明

### JWT 认证
- Token 有效期：7天
- 算法：HS256
- 请求头：`Authorization: Bearer {token}`

### 数据库设计
- **用户表** (`t_user`): 用户信息、密码 MD5 加密
- **知识树表** (`t_knowledge_node`): 递归结构、双轨系统（当前阶段保留）

### 多租户（Multi-tenant）
- 租户隔离策略将用于用户、内容与配置层面的逻辑隔离
- 当前阶段以用户维度隔离为主，后续补充租户域模型

### MyBatis-Plus
- 逻辑删除：`deleted` 字段
- 自动填充：`createTime`, `updateTime`
- 分页插件：已配置

---

## 📝 开发注意事项

1. **密码安全**: 使用 MD5 加密（生产环境建议用 BCrypt）
2. **Token 管理**: 前端需在请求头携带 Token
3. **跨域配置**: 已开启 CORS，允许所有域名（开发环境）
4. **异常处理**: 全局异常处理器已配置

---

**返回项目主目录查看完整文档：[../README.md](../README.md)**
