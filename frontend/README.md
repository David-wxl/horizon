# HORIZON Frontend | 前端应用

> 本目录是 HORIZON 项目的前端部分，基于 Vue 3 + Vite + TypeScript

---

## 🚀 快速开始

### 1. 安装依赖

```bash
npm install
```

### 2. 启动开发服务器

```bash
npm run dev
```

访问：`http://localhost:5173`

### 3. 构建生产版本

```bash
npm run build
```

### 4. 预览生产构建

```bash
npm run preview
```

---

## 📁 目录结构

```
frontend/
├── src/
│   ├── components/      # Vue 组件
│   │   └── ParticleBackground.vue  # 3D 粒子背景
│   ├── composables/     # 组合式函数（待添加）
│   ├── views/           # 页面视图（待添加）
│   ├── App.vue          # 根组件
│   ├── main.ts          # 入口文件
│   └── style.css        # 全局样式
├── public/              # 静态资源
├── index.html           # HTML 模板
├── vite.config.ts       # Vite 配置
├── tailwind.config.js   # Tailwind 配置
└── tsconfig.json        # TypeScript 配置
```

---

## 🎨 设计规范

### 色彩系统
- **背景渐变**: `linear-gradient(135deg, #1e1b4b 0%, #1e3a8a 50%, #0f172a 100%)`
- **主色调**: Purple `#a855f7` / Cyan `#06b6d4`
- **卡片背景**: `rgba(255, 255, 255, 0.05)` + `backdrop-blur-2xl`

### Tailwind 类命名
- **间距**: 使用 `p-12`, `p-16`, `gap-8` 等大间距
- **圆角**: `rounded-2xl` 或 `rounded-3xl`
- **阴影**: `shadow-2xl shadow-purple-500/20`
- **hover**: `hover:scale-105 hover:-translate-y-2`

### 组件规范
- 使用 Vue 3 Composition API (`<script setup>`)
- TypeScript 类型定义
- 复杂逻辑提取到 `composables/`

---

## 🛠️ 技术栈

- **Vue 3**: Composition API, `<script setup>`
- **Vite**: 快速构建工具
- **TypeScript**: 类型安全
- **Tailwind CSS**: 原子化 CSS
- **Tres.js**: Three.js for Vue (3D 粒子效果)

---

## 📝 开发注意事项

1. **API 基础路径**: `http://localhost:8080/api`
2. **Token 存储**: 使用 `localStorage` 或 `sessionStorage`
3. **请求头**: `Authorization: Bearer {token}`
4. **Element Plus**: 仅用于后台管理，门户页面使用纯 Tailwind

---

## 🎯 待开发功能

- [ ] 用户登录/注册页面
- [ ] 双轨知识树可视化
- [ ] AI 简历诊断界面
- [ ] 3D 粒子交互优化
- [ ] 响应式布局

---

**返回项目主目录查看完整文档：[../README.md](../README.md)**
