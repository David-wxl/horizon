<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { testConnection } from './api/user'
import ParticleBackground from './components/ParticleBackground.vue'

const backendStatus = ref<'checking' | 'connected' | 'error'>('checking')
const backendMessage = ref('')
const errorMessage = ref('')

// 测试后端连接
async function checkBackend() {
  backendStatus.value = 'checking'
  try {
    const message = await testConnection()
    backendMessage.value = message
    backendStatus.value = 'connected'
  } catch (error: any) {
    backendStatus.value = 'error'
    errorMessage.value = error.message || '无法连接到后端服务'
  }
}

onMounted(() => {
  checkBackend()
})
</script>

<template>
  <div class="min-h-screen relative overflow-hidden">
    <!-- 3D 粒子背景 -->
    <ParticleBackground />

    <!-- 主内容 -->
    <div class="relative z-10 flex items-center justify-center min-h-screen p-8">
      <div class="w-full max-w-2xl">
        <!-- 标题 -->
        <div class="text-center mb-16">
          <h1 class="text-6xl font-bold text-white mb-4 tracking-tight">
            HORIZON
          </h1>
          <p class="text-xl text-slate-300">地平线 · 视觉优先的个人技术堡垒</p>
        </div>

        <!-- 连接状态卡片 -->
        <div
          class="bg-white/5 backdrop-blur-2xl border border-white/10 rounded-3xl p-12 shadow-2xl shadow-purple-500/20 hover:bg-white/10 hover:border-white/20 transition-all duration-700"
        >
          <h2 class="text-3xl font-bold text-white mb-8">前后端连接测试</h2>

          <div class="space-y-6">
            <!-- 前端状态 -->
            <div class="flex items-center justify-between p-6 bg-white/5 rounded-2xl">
              <span class="text-lg text-slate-300">前端服务</span>
              <div class="flex items-center gap-3">
                <div class="w-3 h-3 bg-green-500 rounded-full animate-pulse"></div>
                <span class="text-white font-semibold">运行中</span>
              </div>
            </div>

            <!-- 后端状态 -->
            <div class="flex items-center justify-between p-6 bg-white/5 rounded-2xl">
              <span class="text-lg text-slate-300">后端服务</span>
              <div class="flex items-center gap-3">
                <!-- 检查中 -->
                <div
                  v-if="backendStatus === 'checking'"
                  class="w-3 h-3 bg-yellow-500 rounded-full animate-pulse"
                ></div>
                <!-- 已连接 -->
                <div
                  v-else-if="backendStatus === 'connected'"
                  class="w-3 h-3 bg-green-500 rounded-full animate-pulse"
                ></div>
                <!-- 错误 -->
                <div
                  v-else
                  class="w-3 h-3 bg-red-500 rounded-full animate-pulse"
                ></div>

                <span
                  class="font-semibold"
                  :class="{
                    'text-yellow-400': backendStatus === 'checking',
                    'text-green-400': backendStatus === 'connected',
                    'text-red-400': backendStatus === 'error',
                  }"
                >
                  {{
                    backendStatus === 'checking'
                      ? '检测中...'
                      : backendStatus === 'connected'
                      ? '已连接'
                      : '连接失败'
                  }}
                </span>
              </div>
            </div>

            <!-- 后端响应消息 -->
            <div
              v-if="backendStatus === 'connected'"
              class="p-6 bg-green-500/10 border border-green-500/20 rounded-2xl"
            >
              <p class="text-green-400 font-mono">{{ backendMessage }}</p>
            </div>

            <!-- 错误消息 -->
            <div
              v-if="backendStatus === 'error'"
              class="p-6 bg-red-500/10 border border-red-500/20 rounded-2xl"
            >
              <p class="text-red-400 mb-2">❌ {{ errorMessage }}</p>
              <p class="text-sm text-slate-400">
                请确保后端服务已启动：
                <code class="text-purple-400">cd backend && mvn spring-boot:run</code>
              </p>
            </div>

            <!-- 重新测试按钮 -->
            <button
              @click="checkBackend"
              class="w-full px-8 py-4 bg-gradient-to-r from-purple-500 via-pink-500 to-cyan-500 text-white font-bold text-lg rounded-2xl hover:scale-105 hover:-translate-y-1 transition-all duration-300 shadow-2xl shadow-purple-500/30"
            >
              重新测试连接
            </button>
          </div>

          <!-- API 信息 -->
          <div class="mt-8 pt-8 border-t border-white/10">
            <h3 class="text-lg font-semibold text-white mb-4">📡 API 配置</h3>
            <div class="space-y-2 text-sm font-mono">
              <div class="flex justify-between text-slate-400">
                <span>后端地址:</span>
                <span class="text-purple-400">http://localhost:8080/api</span>
              </div>
              <div class="flex justify-between text-slate-400">
                <span>测试端点:</span>
                <span class="text-cyan-400">/user/test</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800;900&display=swap');

* {
  font-family: 'Inter', sans-serif;
}
</style>
