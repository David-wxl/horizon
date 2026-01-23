# 地平线 | 视界 Frontend | 前端应用

> 本目录是「地平线 | 视界」的前端部分，基于 Vue 3 + Vite + TypeScript

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
│   │   └── (可选) 视觉组件
│   ├── composables/     # 组合式函数（待添加）
│   ├── views/           # 页面视图
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
- **背景渐变**: `linear-gradient(135deg, #f5f5f0 0%, #efece1 50%, #f7f1e8 100%)`
- **主色调**: Warm Yellow / Orange / Soft Grey
- **卡片背景**: `bg-white/60` ~ `bg-white/80` + `backdrop-blur-2xl`

### Tailwind 类命名
- **间距**: 使用 `p-12`, `p-16`, `gap-8` 等大间距
- **圆角**: `rounded-3xl`
- **阴影**: 柔和扩散阴影（避免强烈霓虹）
- **hover**: `hover:scale-105` + 阴影加深

### 组件规范
- 使用 Vue 3 Composition API (`<script setup>`)
- TypeScript 类型定义
- 复杂逻辑提取到 `composables/`
 - 页面强调 WYSIWYG 与 Bento Grid 排布

---

## 🛠️ 技术栈

- **Vue 3**: Composition API, `<script setup>`
- **Vite**: 快速构建工具
- **TypeScript**: 类型安全
- **Tailwind CSS**: 原子化 CSS
- **动画**: CSS Transition / Vue Transition（必要时）

---

## 📝 开发注意事项

1. **API 基础路径**: `http://localhost:8080/api`
2. **Token 存储**: 使用 `localStorage` 或 `sessionStorage`
3. **请求头**: `Authorization: Bearer {token}`
4. **Element Plus**: 仅用于后台管理，门户页面使用纯 Tailwind

---

## 🎯 待开发功能

- [x] 用户登录/注册页面（Warm Bento Grid）
- [ ] Bento 布局编辑器（WYSIWYG）
- [ ] 内容自动构建器（图像/代码/书影音）
- [ ] 社区广场（The Square）
- [ ] 多租户与管理后台
- [ ] 视觉细节优化
- [ ] 响应式布局

---

**返回项目主目录查看完整文档：[../README.md](../README.md)**
