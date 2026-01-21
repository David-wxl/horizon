# HORIZON | 地平线

> 视觉优先的个人技术堡垒

## 🎯 项目介绍

HORIZON 是一个结合「双轨知识树」与 AI 简历诊断的沉浸式 3D 交互平台。

### 核心特性

- **双轨知识树系统**
  - 左侧：「总纲」（只读，标准路径）
  - 右侧：「Second Brain」（用户可编辑，无限层级）
- **AI 简历工坊** - 基于 Google Gemini API 的简历分析与优化
- **3D 沉浸体验** - 使用 Tres.js 实现粒子流动效果
- **Glassmorphism 设计** - 毛玻璃风格的极客美学

## 🛠️ 技术栈

### 前端
- **框架**: Vue 3 (Composition API) + Vite + TypeScript
- **样式**: Tailwind CSS (原子化优先)
- **3D/视觉**: Tres.js (Three.js Vue 封装) + Framer Motion
- **图表**: ECharts 5.0

### 后端
- **框架**: Spring Boot 3.x + Java 17+
- **数据库**: MySQL 8.0 + MyBatis-Plus
- **AI**: Google Gemini API

## 🚀 快速开始

### 安装依赖

```bash
npm install
```

### 启动开发服务器

```bash
npm run dev
```

### 构建生产版本

```bash
npm run build
```

### 预览生产构建

```bash
npm run preview
```

## 🎨 设计规范

### 色彩系统
- **背景**: `#1a1a1a` (深灰，固定深色模式)
- **主文字**: `#ffffff`
- **次要文字**: `#e0e0e0`
- **强调色**: `#3b82f6` (品牌蓝)

### UI 原则
1. 全局使用 Glassmorphism（毛玻璃）效果
2. 门户首页必须包含 Tres.js 3D 粒子流
3. 过渡动画使用 Framer Motion
4. Element Plus 仅用于后台管理面板

## 📁 项目结构

```
horizon/
├── src/
│   ├── assets/          # 静态资源
│   ├── components/      # 组件
│   ├── composables/     # 组合式函数
│   ├── views/           # 页面视图
│   ├── App.vue          # 根组件
│   ├── main.ts          # 入口文件
│   └── style.css        # 全局样式
├── public/              # 公共资源
├── index.html           # HTML 模板
├── vite.config.ts       # Vite 配置
├── tailwind.config.js   # Tailwind 配置
└── tsconfig.json        # TypeScript 配置
```

## 📝 开发规范

- 使用 Vue 3 Composition API (`<script setup>`)
- 复杂逻辑提取到 `composables/` 目录
- 优先使用 Tailwind 原子类
- 为复杂业务逻辑添加注释（特别是树形递归算法）

## 📄 License

MIT

---

**Built with ❤️ for the future**
