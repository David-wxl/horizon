# 管理员功能 - 快速开始

## ⚡ 3分钟部署

### 第1步：数据库更新（1分钟）

打开Navicat，执行：

```sql
USE horizon;

ALTER TABLE t_user 
ADD COLUMN role VARCHAR(20) DEFAULT 'USER' COMMENT '用户角色' AFTER status;

CREATE INDEX idx_role ON t_user(role);

UPDATE t_user SET role = 'ADMIN' WHERE id = 1;

SELECT id, username, role FROM t_user;
```

**预期结果**：看到id=1的用户role为ADMIN

---

### 第2步：重启后端（1分钟）

```bash
cd d:\graduation-project\horizon\backend
.\RESTART_BACKEND.bat
```

等待启动完成。

---

### 第3步：访问管理后台（1分钟）

1. 浏览器强制刷新：`Ctrl + Shift + R`
2. 登出当前账户
3. 用管理员账户重新登录
4. 点击 **🔐 管理后台** 按钮

✅ 成功！你应该看到漂亮的玻璃拟态仪表盘了！

---

## 🎯 快速验证

访问：`http://localhost:5173/admin`

应该看到：
- ✅ 左侧：管理员身份卡
- ✅ 中间：CPU、内存、存储监控
- ✅ 右侧：统计数据和最新动态

---

## 🐛 如果失败

### 看不到"管理后台"按钮

**解决**：
```sql
-- 1. 检查role字段
DESCRIBE t_user;

-- 2. 检查用户角色
SELECT id, username, role FROM t_user WHERE id = 1;

-- 3. 确保为ADMIN
UPDATE t_user SET role = 'ADMIN' WHERE id = 1;
```

然后**重新登录**。

### 访问被拦截

**解决**：
1. 确认已重启后端
2. 确认已刷新前端（Ctrl + Shift + R）
3. 确认已重新登录
4. 按F12查看localStorage中的user对象是否包含role字段

---

## 📖 详细文档

- **[ADMIN_FEATURE_COMPLETE.md](./ADMIN_FEATURE_COMPLETE.md)** - 实现详情
- **[ADMIN_GUIDE.md](./ADMIN_GUIDE.md)** - 使用指南
- **[ADMIN_DEPLOYMENT.md](./ADMIN_DEPLOYMENT.md)** - 部署指南

---

**快速开始版本**：1.0  
*最后更新：2026-01-26*
