<script setup lang="ts">
import { ref, watch } from 'vue'
import { post, get } from '../api/request'

const isLogin = ref(true)
const formData = ref({
  username: '',
  password: '',
  email: '',
  nickname: '',
})
const loading = ref(false)
const errorMessage = ref('')
const successMessage = ref('')
const usernameStatus = ref<'idle' | 'checking' | 'taken' | 'available'>('idle')
let usernameTimer: ReturnType<typeof setTimeout> | null = null

function toggleMode() {
  isLogin.value = !isLogin.value
  errorMessage.value = ''
  successMessage.value = ''
  usernameStatus.value = 'idle'
}

watch(() => formData.value.username, (val) => {
  if (isLogin.value) return
  if (usernameTimer) clearTimeout(usernameTimer)
  if (!val || val.length < 2) {
    usernameStatus.value = 'idle'
    return
  }
  usernameStatus.value = 'checking'
  usernameTimer = setTimeout(async () => {
    try {
      const exists = await get<boolean>(`/user/checkUsername?username=${encodeURIComponent(val)}`)
      usernameStatus.value = exists ? 'taken' : 'available'
    } catch {
      usernameStatus.value = 'idle'
    }
  }, 500)
})

// 登录
async function handleLogin() {
  errorMessage.value = ''
  
  if (!formData.value.username || !formData.value.password) {
    errorMessage.value = '请输入用户名和密码'
    return
  }

  loading.value = true
  
  try {
    const result = await post<{ token: string; user: any }>('/user/login', {
      username: formData.value.username,
      password: formData.value.password,
    })
    
    // 保存 token
    localStorage.setItem('token', result.token)
    localStorage.setItem('user', JSON.stringify(result.user))
    
    successMessage.value = '登录成功！'
    
    // 跳转到广场
    setTimeout(() => {
      globalThis.location.href = '/square'
    }, 1000)
  } catch (error: any) {
    errorMessage.value = error.message || '登录失败'
  } finally {
    loading.value = false
  }
}

// 注册
async function handleRegister() {
  errorMessage.value = ''
  
  if (!formData.value.username || !formData.value.password) {
    errorMessage.value = '请输入用户名和密码'
    return
  }

  if (formData.value.password.length < 6) {
    errorMessage.value = '密码长度至少6位'
    return
  }

  if (usernameStatus.value === 'taken') {
    errorMessage.value = '用户名已被占用，请换一个'
    return
  }

  loading.value = true
  
  try {
    const result = await post<{ token: string; user: any }>('/user/register', {
      username: formData.value.username,
      password: formData.value.password,
      email: formData.value.email,
      nickname: formData.value.nickname,
    })
    
    // 保存 token
    localStorage.setItem('token', result.token)
    localStorage.setItem('user', JSON.stringify(result.user))
    
    successMessage.value = '注册成功！正在跳转...'
    
    // 跳转到广场
    setTimeout(() => {
      globalThis.location.href = '/square'
    }, 1000)
  } catch (error: any) {
    errorMessage.value = error.message || '注册失败'
  } finally {
    loading.value = false
  }
}

// 提交表单
function handleSubmit() {
  if (isLogin.value) {
    handleLogin()
  } else {
    handleRegister()
  }
}
</script>

<template>
  <div class="min-h-screen relative overflow-hidden text-slate-800">
    <!-- 主内容 -->
    <div class="relative z-10 flex items-center justify-center min-h-screen p-8">
      <div class="w-full max-w-4xl">
        <!-- Logo -->
        <div class="text-center mb-10 fade-up">
          <p class="text-sm uppercase tracking-[0.3em] text-stone-500 mb-4">
            warm bento grid
          </p>
          <h1 class="text-6xl font-semibold text-slate-900 mb-4 tracking-tight">
            HORIZON
          </h1>
          <p class="text-xl text-stone-600">地平线 · 视觉优先的个人技术堡垒</p>
        </div>

        <!-- Bento Grid -->
        <div class="max-w-xl mx-auto fade-up fade-up-delay-1">
          <div class="glass-card p-10">
            <div class="flex items-center justify-between mb-8">
              <h2 class="text-2xl font-semibold text-slate-900">
                {{ isLogin ? '登录' : '注册' }}
              </h2>
              <span class="text-sm text-stone-500">
                {{ isLogin ? '欢迎回来' : '创建你的账户' }}
              </span>
            </div>

            <form @submit.prevent="handleSubmit" class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <!-- 用户名 -->
              <div class="md:col-span-2">
                <label class="block text-stone-600 mb-2" for="login-username">用户名</label>
                <div class="relative">
                  <input
                    v-model="formData.username"
                    type="text"
                    id="login-username"
                    class="w-full px-6 py-4 bg-white/80 border rounded-3xl text-slate-900 placeholder-stone-400 focus:outline-none focus:ring-2 transition-all"
                    :class="!isLogin && usernameStatus === 'taken'
                      ? 'border-rose-300 focus:border-rose-400 focus:ring-rose-200/60'
                      : !isLogin && usernameStatus === 'available'
                        ? 'border-emerald-300 focus:border-emerald-400 focus:ring-emerald-200/60'
                        : 'border-white/60 focus:border-amber-300 focus:ring-amber-200/60'"
                    placeholder="请输入用户名（注册后不可更改）"
                    :disabled="loading"
                  />
                  <span v-if="!isLogin && usernameStatus === 'checking'" class="absolute right-4 top-1/2 -translate-y-1/2 text-stone-400 text-sm">检查中...</span>
                  <span v-else-if="!isLogin && usernameStatus === 'taken'" class="absolute right-4 top-1/2 -translate-y-1/2 text-rose-500 text-sm">❌ 已被占用</span>
                  <span v-else-if="!isLogin && usernameStatus === 'available'" class="absolute right-4 top-1/2 -translate-y-1/2 text-emerald-500 text-sm">✓ 可以使用</span>
                </div>
              </div>

              <!-- 密码 -->
              <div class="md:col-span-2">
                <label class="block text-stone-600 mb-2" for="login-password">密码</label>
                <input
                  v-model="formData.password"
                  type="password"
                  id="login-password"
                  class="w-full px-6 py-4 bg-white/80 border border-white/60 rounded-3xl text-slate-900 placeholder-stone-400 focus:outline-none focus:border-amber-300 focus:ring-2 focus:ring-amber-200/60 transition-all"
                  placeholder="请输入密码"
                  :disabled="loading"
                />
              </div>

              <!-- 注册时的额外字段 -->
              <template v-if="!isLogin">
                <!-- 邮箱 -->
                <div class="md:col-span-1">
                  <label class="block text-stone-600 mb-2" for="login-email">邮箱（可选）</label>
                  <input
                    v-model="formData.email"
                    type="email"
                    id="login-email"
                    class="w-full px-6 py-4 bg-white/80 border border-white/60 rounded-3xl text-slate-900 placeholder-stone-400 focus:outline-none focus:border-amber-300 focus:ring-2 focus:ring-amber-200/60 transition-all"
                    placeholder="请输入邮箱"
                    :disabled="loading"
                  />
                </div>

                <!-- 昵称 -->
                <div class="md:col-span-1">
                  <label class="block text-stone-600 mb-2" for="login-nickname">昵称（可选）</label>
                  <input
                    v-model="formData.nickname"
                    type="text"
                    id="login-nickname"
                    class="w-full px-6 py-4 bg-white/80 border border-white/60 rounded-3xl text-slate-900 placeholder-stone-400 focus:outline-none focus:border-amber-300 focus:ring-2 focus:ring-amber-200/60 transition-all"
                    placeholder="请输入昵称"
                    :disabled="loading"
                  />
                </div>
              </template>

              <!-- 错误消息 -->
              <div
                v-if="errorMessage"
                class="md:col-span-2 p-4 bg-rose-100/70 border border-rose-200/60 rounded-3xl text-rose-700 text-sm"
              >
                {{ errorMessage }}
              </div>

              <!-- 成功消息 -->
              <div
                v-if="successMessage"
                class="md:col-span-2 p-4 bg-emerald-100/70 border border-emerald-200/60 rounded-3xl text-emerald-700 text-sm"
              >
                {{ successMessage }}
              </div>

              <!-- 提交按钮 -->
              <button
                type="submit"
                :disabled="loading"
                class="md:col-span-2 w-full px-8 py-4 rounded-3xl bg-gradient-to-r from-amber-300 via-orange-200 to-stone-200 text-slate-900 font-semibold hover:scale-105 transition-all duration-300 shadow-md hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none"
              >
                {{ loading ? '处理中...' : isLogin ? '登录' : '注册' }}
              </button>

              <!-- 切换登录/注册 -->
              <div class="md:col-span-2 text-center">
                <button
                  type="button"
                  @click="toggleMode"
                  class="text-stone-500 hover:text-slate-900 transition-colors"
                  :disabled="loading"
                >
                  {{ isLogin ? '还没有账号？立即注册' : '已有账号？立即登录' }}
                </button>
              </div>
            </form>
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
