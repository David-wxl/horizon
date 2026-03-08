<script setup lang="ts">
import { ref, watch, onMounted, onUnmounted, computed } from 'vue'
import { post, get } from '../api/request'
import LoginCharacter from '../components/LoginCharacter.vue'

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

const focusedField = ref<'none' | 'username' | 'password' | 'other'>('none')
const charStatus = ref<'idle' | 'success' | 'error'>('idle')

const activeTextLength = computed(() => {
  if (focusedField.value === 'username') return formData.value.username.length
  if (focusedField.value === 'other') return (formData.value.email.length || formData.value.nickname.length)
  return 0
})

const showPassword = ref(false)

let focusCheckTimer: ReturnType<typeof setInterval>

function checkFocus() {
  const el = document.activeElement as HTMLElement | null
  if (!el || el.tagName !== 'INPUT') {
    if (focusedField.value !== 'none') focusedField.value = 'none'
    return
  }
  const id = el.id
  if (id === 'login-username' && focusedField.value !== 'username') {
    focusedField.value = 'username'
    charStatus.value = 'idle'
  } else if (id === 'login-password' && focusedField.value !== 'password') {
    focusedField.value = 'password'
    charStatus.value = 'idle'
  } else if ((id === 'login-email' || id === 'login-nickname') && focusedField.value !== 'other') {
    focusedField.value = 'other'
    charStatus.value = 'idle'
  }
}

function toggleMode() {
  isLogin.value = !isLogin.value
  errorMessage.value = ''
  successMessage.value = ''
  usernameStatus.value = 'idle'
  charStatus.value = 'idle'
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

async function handleLogin() {
  errorMessage.value = ''
  charStatus.value = 'idle'
  if (!formData.value.username || !formData.value.password) {
    errorMessage.value = '请输入用户名和密码'
    charStatus.value = 'error'
    return
  }
  loading.value = true
  try {
    const result = await post<{ token: string; user: any }>('/user/login', {
      username: formData.value.username,
      password: formData.value.password,
    })
    localStorage.setItem('token', result.token)
    localStorage.setItem('user', JSON.stringify(result.user))
    successMessage.value = '登录成功！'
    charStatus.value = 'success'
    setTimeout(() => { globalThis.location.href = '/square' }, 1200)
  } catch (error: any) {
    errorMessage.value = error.message || '登录失败'
    charStatus.value = 'error'
  } finally {
    loading.value = false
  }
}

async function handleRegister() {
  errorMessage.value = ''
  charStatus.value = 'idle'
  if (!formData.value.username || !formData.value.password) {
    errorMessage.value = '请输入用户名和密码'
    charStatus.value = 'error'
    return
  }
  if (formData.value.password.length < 6) {
    errorMessage.value = '密码长度至少6位'
    charStatus.value = 'error'
    return
  }
  if (usernameStatus.value === 'taken') {
    errorMessage.value = '用户名已被占用，请换一个'
    charStatus.value = 'error'
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
    localStorage.setItem('token', result.token)
    localStorage.setItem('user', JSON.stringify(result.user))
    successMessage.value = '注册成功！正在跳转...'
    charStatus.value = 'success'
    setTimeout(() => { globalThis.location.href = '/square' }, 1200)
  } catch (error: any) {
    errorMessage.value = error.message || '注册失败'
    charStatus.value = 'error'
  } finally {
    loading.value = false
  }
}

function handleSubmit() {
  if (isLogin.value) handleLogin()
  else handleRegister()
}

onMounted(() => {
  focusCheckTimer = setInterval(checkFocus, 100)
})

onUnmounted(() => {
  clearInterval(focusCheckTimer)
})
</script>

<template>
  <div class="login-page">
    <!-- ═══════ 左侧：深色面板 + 角色 ═══════ -->
    <div class="left-panel">
      <div class="left-brand">
        <span class="brand-icon">◈</span>
        <span class="brand-name">HORIZON</span>
      </div>

      <div class="left-characters">
        <LoginCharacter
          :focus-field="focusedField"
          :text-length="activeTextLength"
          :status="charStatus"
        />
      </div>

      <div class="left-footer">
        <span>地平线 · 视觉优先的个人技术堡垒</span>
      </div>
    </div>

    <!-- ═══════ 右侧：登录表单 ═══════ -->
    <div class="right-panel">
      <div class="form-container">
        <h1 class="form-title">{{ isLogin ? '欢迎回来！' : '创建账户' }}</h1>
        <p class="form-subtitle">{{ isLogin ? '请输入你的登录信息' : '填写以下信息注册' }}</p>

        <form @submit.prevent="handleSubmit" class="login-form">
          <!-- 用户名 -->
          <div class="field-group">
            <label for="login-username">用户名</label>
            <div class="input-wrap">
              <input
                v-model="formData.username"
                type="text"
                id="login-username"
                autocomplete="username"
                :placeholder="isLogin ? '请输入用户名' : '请输入用户名（注册后不可更改）'"
                :disabled="loading"
                :class="{
                  'input-error': !isLogin && usernameStatus === 'taken',
                  'input-success': !isLogin && usernameStatus === 'available',
                }"
              />
              <span v-if="!isLogin && usernameStatus === 'checking'" class="input-hint hint-neutral">检查中...</span>
              <span v-else-if="!isLogin && usernameStatus === 'taken'" class="input-hint hint-error">已被占用</span>
              <span v-else-if="!isLogin && usernameStatus === 'available'" class="input-hint hint-success">可以使用</span>
            </div>
          </div>

          <!-- 密码 -->
          <div class="field-group">
            <label for="login-password">密码</label>
            <div class="input-wrap">
              <input
                v-model="formData.password"
                :type="showPassword ? 'text' : 'password'"
                id="login-password"
                autocomplete="current-password"
                placeholder="请输入密码"
                :disabled="loading"
              />
              <button type="button" class="toggle-pw" @click="showPassword = !showPassword" tabindex="-1">
                <svg v-if="showPassword" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94"/><path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19"/><line x1="1" y1="1" x2="23" y2="23"/></svg>
                <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
              </button>
            </div>
          </div>

          <!-- 注册额外字段 -->
          <template v-if="!isLogin">
            <div class="field-row">
              <div class="field-group">
                <label for="login-email">邮箱（可选）</label>
                <input v-model="formData.email" type="email" id="login-email" placeholder="email@example.com" :disabled="loading" />
              </div>
              <div class="field-group">
                <label for="login-nickname">昵称（可选）</label>
                <input v-model="formData.nickname" type="text" id="login-nickname" placeholder="你的昵称" :disabled="loading" />
              </div>
            </div>
          </template>

          <!-- 消息 -->
          <div v-if="errorMessage" class="msg msg-error">{{ errorMessage }}</div>
          <div v-if="successMessage" class="msg msg-success">{{ successMessage }}</div>

          <!-- 提交 -->
          <button type="submit" class="submit-btn" :disabled="loading">
            {{ loading ? '处理中...' : isLogin ? '登 录' : '注 册' }}
          </button>

          <!-- 切换 -->
          <p class="switch-mode">
            {{ isLogin ? '还没有账号？' : '已有账号？' }}
            <button type="button" @click="toggleMode" :disabled="loading">
              {{ isLogin ? '立即注册' : '立即登录' }}
            </button>
          </p>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  display: flex;
  min-height: 100vh;
  background: #fafafa;
}

/* ─── 左侧面板 ─── */
.left-panel {
  flex: 0 0 50%;
  max-width: 50%;
  background: #161616;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
}

.left-brand {
  padding: 32px 40px;
  display: flex;
  align-items: center;
  gap: 10px;
  z-index: 2;
}
.brand-icon {
  font-size: 22px;
  color: #888;
}
.brand-name {
  font-size: 16px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 0.06em;
}

.left-characters {
  flex: 1;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  padding: 0 24px;
  z-index: 1;
}

.left-footer {
  padding: 20px 40px;
  z-index: 2;
}
.left-footer span {
  font-size: 12px;
  color: #555;
  letter-spacing: 0.03em;
}

/* ─── 右侧面板 ─── */
.right-panel {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background: #fff;
}

.form-container {
  width: 100%;
  max-width: 400px;
}

.form-title {
  font-size: 28px;
  font-weight: 700;
  color: #111;
  margin-bottom: 6px;
}
.form-subtitle {
  font-size: 14px;
  color: #888;
  margin-bottom: 32px;
}

/* ─── 表单 ─── */
.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.field-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
  flex: 1;
}
.field-group label {
  font-size: 13px;
  font-weight: 500;
  color: #333;
}

.field-row {
  display: flex;
  gap: 12px;
}

.input-wrap {
  position: relative;
}

.field-group input,
.login-form > .field-group input {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid #e0e0e0;
  border-radius: 10px;
  font-size: 14px;
  color: #111;
  background: #fafafa;
  outline: none;
  transition: border-color 0.2s, box-shadow 0.2s;
}
.field-group input::placeholder {
  color: #bbb;
}
.field-group input:focus {
  border-color: #7C5CFC;
  box-shadow: 0 0 0 3px rgba(124, 92, 252, 0.1);
  background: #fff;
}
.field-group input:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
.field-group input.input-error {
  border-color: #f87171;
}
.field-group input.input-success {
  border-color: #34d399;
}

.input-hint {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 11px;
  font-weight: 500;
}
.hint-neutral { color: #999; }
.hint-error { color: #f87171; }
.hint-success { color: #34d399; }

.toggle-pw {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: #999;
  cursor: pointer;
  padding: 2px;
  display: flex;
  transition: color 0.2s;
}
.toggle-pw:hover { color: #555; }

/* ─── 消息 ─── */
.msg {
  padding: 10px 14px;
  border-radius: 10px;
  font-size: 13px;
}
.msg-error {
  background: #fef2f2;
  color: #dc2626;
  border: 1px solid #fecaca;
}
.msg-success {
  background: #f0fdf4;
  color: #16a34a;
  border: 1px solid #bbf7d0;
}

/* ─── 提交按钮 ─── */
.submit-btn {
  width: 100%;
  padding: 12px;
  border: none;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  background: #161616;
  color: #fff;
  transition: all 0.2s;
  letter-spacing: 0.08em;
}
.submit-btn:hover {
  background: #2d2d2d;
}
.submit-btn:active {
  transform: scale(0.98);
}
.submit-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* ─── 切换模式 ─── */
.switch-mode {
  text-align: center;
  font-size: 13px;
  color: #888;
}
.switch-mode button {
  background: none;
  border: none;
  color: #7C5CFC;
  font-weight: 600;
  cursor: pointer;
  font-size: 13px;
}
.switch-mode button:hover {
  text-decoration: underline;
}

/* ─── 响应式 ─── */
@media (max-width: 768px) {
  .login-page {
    flex-direction: column;
  }
  .left-panel {
    flex: 0 0 280px;
    max-width: 100%;
  }
  .right-panel {
    padding: 24px;
  }
}
</style>
