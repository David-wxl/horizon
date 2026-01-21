<script setup lang="ts">
import { ref } from 'vue'
import ParticleBackground from './components/ParticleBackground.vue'

const appTitle = ref('HORIZON')
const features = [
  { icon: '🌳', title: '双轨知识树', desc: '标准路径 × 个人脑图' },
  { icon: '🤖', title: 'AI 简历工坊', desc: 'Gemini 智能分析' },
  { icon: '📝', title: '文章中心', desc: '技术笔记 · 教程' },
]

const techStack = [
  'Vue 3', 'TypeScript', 'Tailwind CSS', 
  'Tres.js', 'Spring Boot', 'MySQL'
]
</script>

<template>
  <div class="min-h-screen flex items-center justify-center p-8 relative overflow-hidden">
    <!-- 彩虹粒子背景 -->
    <ParticleBackground />
    <!-- 装饰性背景光晕 -->
    <div class="absolute top-20 left-20 w-96 h-96 bg-purple-500/20 rounded-full blur-3xl animate-pulse"></div>
    <div class="absolute bottom-20 right-20 w-96 h-96 bg-cyan-500/20 rounded-full blur-3xl animate-pulse" style="animation-delay: 1s;"></div>
    
    <!-- 主容器 -->
    <div class="max-w-6xl w-full relative z-10">
      <!-- 标题区域 -->
      <div class="text-center mb-16 space-y-6">
        <h1 class="text-8xl font-black tracking-tighter gradient-text mb-4 animate-float">
          {{ appTitle }}
        </h1>
        <p class="text-3xl font-light text-horizon-secondary tracking-wide">
          视觉优先的个人技术堡垒
        </p>
        <div class="h-1 w-32 mx-auto bg-gradient-to-r from-purple-500 via-pink-500 to-cyan-500 rounded-full"></div>
      </div>

      <!-- 核心功能卡片 -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-8 mb-16">
        <div 
          v-for="(feature, index) in features" 
          :key="index"
          class="glass-card p-8 group cursor-pointer"
        >
          <div class="text-5xl mb-4 group-hover:scale-110 transition-transform duration-500">
            {{ feature.icon }}
          </div>
          <h3 class="text-2xl font-bold text-white mb-2">
            {{ feature.title }}
          </h3>
          <p class="text-horizon-secondary">
            {{ feature.desc }}
          </p>
        </div>
      </div>

      <!-- CTA 按钮组 -->
      <div class="flex flex-col sm:flex-row justify-center items-center gap-6 mb-16">
        <button class="btn-primary w-full sm:w-auto">
          🚀 开始探索
        </button>
        <button class="btn-secondary w-full sm:w-auto">
          📖 阅读文档
        </button>
      </div>

      <!-- 技术栈标签 -->
      <div class="glass-panel p-8">
        <p class="text-sm uppercase tracking-widest text-horizon-secondary/60 text-center mb-6 font-semibold">
          Powered By
        </p>
        <div class="flex flex-wrap justify-center gap-4">
          <span 
            v-for="tech in techStack" 
            :key="tech"
            class="px-6 py-3 bg-white/5 hover:bg-white/10 border border-white/10 hover:border-white/20 
                   rounded-xl text-sm font-medium text-horizon-secondary hover:text-white
                   transition-all duration-300 cursor-default hover:-translate-y-1"
          >
            {{ tech }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 额外的微调动画 */
@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-10px);
  }
}

.animate-float {
  animation: float 4s ease-in-out infinite;
}
</style>
