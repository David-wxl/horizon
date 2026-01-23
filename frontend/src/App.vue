<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { testConnection } from './api/user'

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
  <div class="min-h-screen relative overflow-hidden text-slate-800">
    <!-- 主内容 -->
    <div class="relative z-10 flex items-center justify-center min-h-screen p-8">
      <div class="w-full max-w-5xl">
        <!-- 标题 -->
        <div class="text-center mb-12 fade-up">
          <p class="text-sm uppercase tracking-[0.3em] text-stone-500 mb-4">
            warm bento grid
          </p>
          <h1 class="text-6xl font-semibold text-slate-900 mb-4 tracking-tight">
            HORIZON
          </h1>
          <p class="text-xl text-stone-600">地平线 · 视觉优先的个人技术堡垒</p>
        </div>

        <!-- Bento Grid -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6 fade-up fade-up-delay-1">
          <div class="md:col-span-2 glass-card p-10">
            <div class="flex items-center justify-between mb-10">
              <h2 class="text-2xl font-semibold text-slate-900">前后端连接测试</h2>
              <span class="text-sm text-stone-500">实时检测</span>
            </div>

            <div class="grid grid-cols-1 sm:grid-cols-2 gap-6">
              <!-- 前端状态 -->
              <div class="p-6 bg-white/70 border border-white/60 rounded-3xl shadow-sm">
                <div class="text-sm text-stone-500 mb-4">前端服务</div>
                <div class="flex items-center justify-between">
                  <span class="text-4xl font-light text-slate-900">OK</span>
                  <div class="flex items-center gap-2">
                    <div class="w-2.5 h-2.5 bg-emerald-500 rounded-full animate-pulse"></div>
                    <span class="text-sm text-stone-600">运行中</span>
                  </div>
                </div>
              </div>

              <!-- 后端状态 -->
              <div class="p-6 bg-white/70 border border-white/60 rounded-3xl shadow-sm">
                <div class="text-sm text-stone-500 mb-4">后端服务</div>
                <div class="flex items-center justify-between">
                  <span class="text-4xl font-light text-slate-900">
                    {{ backendStatus === 'connected' ? 'OK' : backendStatus === 'checking' ? '...' : 'ERR' }}
                  </span>
                  <div class="flex items-center gap-2">
                    <div
                      v-if="backendStatus === 'checking'"
                      class="w-2.5 h-2.5 bg-amber-500 rounded-full animate-pulse"
                    ></div>
                    <div
                      v-else-if="backendStatus === 'connected'"
                      class="w-2.5 h-2.5 bg-emerald-500 rounded-full animate-pulse"
                    ></div>
                    <div v-else class="w-2.5 h-2.5 bg-rose-500 rounded-full animate-pulse"></div>

                    <span
                      class="text-sm font-medium"
                      :class="{
                        'text-amber-600': backendStatus === 'checking',
                        'text-emerald-600': backendStatus === 'connected',
                        'text-rose-600': backendStatus === 'error',
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
              </div>
            </div>

            <!-- 后端响应消息 -->
            <div
              v-if="backendStatus === 'connected'"
              class="mt-6 p-5 bg-emerald-100/60 border border-emerald-200/60 rounded-3xl text-emerald-700 text-sm"
            >
              <span class="font-mono">{{ backendMessage }}</span>
            </div>

            <!-- 错误消息 -->
            <div
              v-if="backendStatus === 'error'"
              class="mt-6 p-5 bg-rose-100/60 border border-rose-200/60 rounded-3xl text-rose-700 text-sm"
            >
              <p class="mb-2">❌ {{ errorMessage }}</p>
              <p class="text-stone-500">
                请确保后端服务已启动：
                <code class="text-amber-700">cd backend && mvn spring-boot:run</code>
              </p>
            </div>
          </div>

          <div class="glass-card p-8 flex flex-col justify-between">
            <div>
              <h3 class="text-lg font-semibold text-slate-900 mb-4">API 配置</h3>
              <div class="space-y-3 text-sm text-stone-600">
                <div class="flex items-center justify-between">
                  <span>后端地址</span>
                  <span class="font-mono text-slate-700">http://localhost:8080/api</span>
                </div>
                <div class="flex items-center justify-between">
                  <span>测试端点</span>
                  <span class="font-mono text-slate-700">/user/test</span>
                </div>
              </div>
            </div>

            <button
              @click="checkBackend"
              class="mt-10 w-full px-6 py-4 rounded-3xl bg-gradient-to-r from-amber-300 via-orange-200 to-stone-200 text-slate-900 font-semibold hover:scale-105 transition-all duration-300 shadow-md hover:shadow-lg"
            >
              重新测试连接
            </button>
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
