<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { post } from '../api/request'
import { changePassword } from '../api/user'

const router = useRouter()

// 用户信息
const user = ref<any>({
  id: 0,
  username: '',
  nickname: '',
  email: '',
  avatar: '',
  bio: '',
  gender: '',
  birthday: '',
  location: ''
})

const loading = ref(false)
const successMessage = ref('')
const errorMessage = ref('')

// 修改密码
const showPasswordForm = ref(false)
const passwordForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })
const passwordLoading = ref(false)
const passwordMessage = ref('')
const passwordError = ref('')

async function handleChangePassword() {
  passwordMessage.value = ''
  passwordError.value = ''

  if (!passwordForm.value.oldPassword || !passwordForm.value.newPassword) {
    passwordError.value = '请填写完整'
    return
  }
  if (passwordForm.value.newPassword.length < 6) {
    passwordError.value = '新密码长度不能少于6位'
    return
  }
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    passwordError.value = '两次输入的新密码不一致'
    return
  }

  passwordLoading.value = true
  try {
    await changePassword(user.value.id, passwordForm.value.oldPassword, passwordForm.value.newPassword)
    passwordMessage.value = '密码修改成功！'
    passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
    setTimeout(() => { passwordMessage.value = ''; showPasswordForm.value = false }, 2000)
  } catch (error: any) {
    passwordError.value = error.message || '修改失败'
  } finally {
    passwordLoading.value = false
  }
}

// 头像预览
const avatarPreview = ref('')

// 加载用户信息
function loadUserInfo() {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    const userData = JSON.parse(userStr)
    user.value = {
      ...userData,
      bio: userData.bio || '',
      gender: userData.gender || '',
      birthday: userData.birthday || '',
      location: userData.location || ''
    }
    avatarPreview.value = user.value.avatar || ''
  }
}

// 处理头像上传
function handleAvatarUpload(event: Event) {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  
  if (file) {
    if (file.size > 2 * 1024 * 1024) {
      alert('头像大小不能超过2MB')
      return
    }
    
    const reader = new FileReader()
    reader.onload = (e) => {
      avatarPreview.value = e.target?.result as string
      user.value.avatar = e.target?.result as string
    }
    reader.readAsDataURL(file)
  }
}

// 保存个人资料
async function handleSave() {
  loading.value = true
  errorMessage.value = ''
  successMessage.value = ''
  
  try {
    const result = await post<any>('/user/updateProfile', {
      id: user.value.id,
      nickname: user.value.nickname,
      email: user.value.email,
      avatar: user.value.avatar,
      bio: user.value.bio,
      gender: user.value.gender,
      birthday: user.value.birthday,
      location: user.value.location
    })
    
    // 更新本地存储
    localStorage.setItem('user', JSON.stringify(result))
    user.value = result
    
    successMessage.value = '保存成功！'
    
    setTimeout(() => {
      successMessage.value = ''
    }, 3000)
  } catch (error: any) {
    errorMessage.value = error.message || '保存失败'
  } finally {
    loading.value = false
  }
}

// 返回
function goBack() {
  router.back()
}

onMounted(() => {
  loadUserInfo()
})
</script>

<template>
  <div class="min-h-screen relative overflow-hidden text-slate-800">
    <!-- 顶部导航栏 -->
    <nav class="sticky top-0 z-50 backdrop-blur-xl bg-white/60 border-b border-white/60">
      <div class="max-w-5xl mx-auto px-8 py-6">
        <div class="flex items-center justify-between">
          <button
            @click="goBack"
            class="flex items-center gap-2 text-slate-700 hover:text-slate-900 transition-colors"
          >
            ← 返回
          </button>
          
          <h1 class="text-xl font-semibold text-slate-900 tracking-tight">个人资料</h1>
          
          <div class="w-20"></div>
        </div>
      </div>
    </nav>

    <!-- 主内容 -->
    <main class="max-w-3xl mx-auto px-8 py-12">
      <div class="glass-card p-12">
        <form @submit.prevent="handleSave" class="space-y-8">
          <!-- 头像 -->
          <div class="text-center">
            <div class="mb-4">
              <div 
                v-if="avatarPreview"
                class="w-32 h-32 mx-auto rounded-full overflow-hidden ring-4 ring-white/60"
              >
                <img :src="avatarPreview" alt="头像" class="w-full h-full object-cover" />
              </div>
              <div 
                v-else
                class="w-32 h-32 mx-auto rounded-full bg-gradient-to-br from-amber-300 to-orange-200 flex items-center justify-center text-slate-900 font-bold text-4xl ring-4 ring-white/60"
              >
                {{ (user.nickname || user.username || '?').charAt(0).toUpperCase() }}
              </div>
            </div>
            
            <label class="inline-block px-6 py-3 rounded-2xl bg-white/80 text-slate-700 border border-white/60 hover:bg-white transition-all cursor-pointer">
              <input 
                type="file" 
                accept="image/*" 
                @change="handleAvatarUpload"
                class="hidden"
              />
              更换头像
            </label>
          </div>

          <!-- 基本信息 -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <!-- 用户名 -->
            <div class="md:col-span-2">
              <label class="block text-stone-600 mb-2">用户名</label>
              <input
                v-model="user.username"
                type="text"
                disabled
                class="w-full px-6 py-4 bg-stone-100/50 border border-white/60 rounded-3xl text-stone-500 cursor-not-allowed"
              />
              <p class="text-xs text-stone-500 mt-1">用户名不可修改</p>
            </div>

            <!-- 昵称 -->
            <div class="md:col-span-2">
              <label for="profile-nickname" class="block text-stone-600 mb-2">昵称</label>
              <input
                id="profile-nickname"
                v-model="user.nickname"
                type="text"
                placeholder="请输入昵称"
                class="w-full px-6 py-4 bg-white/80 border border-white/60 rounded-3xl text-slate-900 placeholder-stone-400 focus:outline-none focus:border-amber-300 focus:ring-2 focus:ring-amber-200/60 transition-all"
              />
            </div>

            <!-- 邮箱 -->
            <div class="md:col-span-2">
              <label for="profile-email" class="block text-stone-600 mb-2">邮箱</label>
              <input
                id="profile-email"
                v-model="user.email"
                type="email"
                placeholder="请输入邮箱"
                class="w-full px-6 py-4 bg-white/80 border border-white/60 rounded-3xl text-slate-900 placeholder-stone-400 focus:outline-none focus:border-amber-300 focus:ring-2 focus:ring-amber-200/60 transition-all"
              />
            </div>

            <!-- 性别 -->
            <div>
              <label for="profile-gender" class="block text-stone-600 mb-2">性别</label>
              <select
                id="profile-gender"
                v-model="user.gender"
                class="w-full px-6 py-4 bg-white/80 border border-white/60 rounded-3xl text-slate-900 focus:outline-none focus:border-amber-300 focus:ring-2 focus:ring-amber-200/60 transition-all"
              >
                <option value="">不设置</option>
                <option value="male">男</option>
                <option value="female">女</option>
                <option value="other">其他</option>
              </select>
            </div>

            <!-- 生日 -->
            <div>
              <label for="profile-birthday" class="block text-stone-600 mb-2">生日</label>
              <input
                id="profile-birthday"
                v-model="user.birthday"
                type="date"
                class="w-full px-6 py-4 bg-white/80 border border-white/60 rounded-3xl text-slate-900 focus:outline-none focus:border-amber-300 focus:ring-2 focus:ring-amber-200/60 transition-all"
              />
            </div>

            <!-- 所在地 -->
            <div class="md:col-span-2">
              <label for="profile-location" class="block text-stone-600 mb-2">所在地</label>
              <input
                id="profile-location"
                v-model="user.location"
                type="text"
                placeholder="例如：北京"
                class="w-full px-6 py-4 bg-white/80 border border-white/60 rounded-3xl text-slate-900 placeholder-stone-400 focus:outline-none focus:border-amber-300 focus:ring-2 focus:ring-amber-200/60 transition-all"
              />
            </div>

            <!-- 个人简介 -->
            <div class="md:col-span-2">
              <label for="profile-bio" class="block text-stone-600 mb-2">个人简介</label>
              <textarea
                id="profile-bio"
                v-model="user.bio"
                rows="4"
                placeholder="介绍一下自己..."
                class="w-full px-6 py-4 bg-white/80 border border-white/60 rounded-3xl text-slate-900 placeholder-stone-400 focus:outline-none focus:border-amber-300 focus:ring-2 focus:ring-amber-200/60 transition-all resize-none"
              ></textarea>
            </div>
          </div>

          <!-- 消息提示 -->
          <div v-if="errorMessage" class="p-4 bg-rose-100/70 border border-rose-200/60 rounded-3xl text-rose-700 text-sm">
            {{ errorMessage }}
          </div>

          <div v-if="successMessage" class="p-4 bg-emerald-100/70 border border-emerald-200/60 rounded-3xl text-emerald-700 text-sm">
            {{ successMessage }}
          </div>

          <!-- 提交按钮 -->
          <div class="flex gap-4">
            <button
              type="submit"
              :disabled="loading"
              class="flex-1 px-8 py-4 rounded-3xl bg-gradient-to-r from-amber-300 via-orange-200 to-stone-200 text-slate-900 font-semibold hover:scale-105 transition-all duration-300 shadow-md hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {{ loading ? '保存中...' : '保存' }}
            </button>
          </div>
        </form>
      </div>

      <!-- 修改密码区域 -->
      <div class="glass-card p-12 mt-8">
        <div class="flex items-center justify-between mb-6">
          <h2 class="text-xl font-semibold text-slate-900">账户安全</h2>
          <button
            @click="showPasswordForm = !showPasswordForm"
            class="px-6 py-3 rounded-2xl bg-white/80 text-slate-700 border border-white/60 hover:bg-white transition-all duration-300"
          >
            {{ showPasswordForm ? '取消' : '修改密码' }}
          </button>
        </div>

        <form v-if="showPasswordForm" @submit.prevent="handleChangePassword" class="space-y-6">
          <div>
            <label for="old-password" class="block text-stone-600 mb-2">当前密码</label>
            <input
              id="old-password"
              v-model="passwordForm.oldPassword"
              type="password"
              required
              placeholder="请输入当前密码"
              class="w-full px-6 py-4 bg-white/80 border border-white/60 rounded-3xl text-slate-900 placeholder-stone-400 focus:outline-none focus:border-amber-300 focus:ring-2 focus:ring-amber-200/60 transition-all"
            />
          </div>
          <div>
            <label for="new-password" class="block text-stone-600 mb-2">新密码</label>
            <input
              id="new-password"
              v-model="passwordForm.newPassword"
              type="password"
              required
              placeholder="请输入新密码（至少6位）"
              class="w-full px-6 py-4 bg-white/80 border border-white/60 rounded-3xl text-slate-900 placeholder-stone-400 focus:outline-none focus:border-amber-300 focus:ring-2 focus:ring-amber-200/60 transition-all"
            />
          </div>
          <div>
            <label for="confirm-password" class="block text-stone-600 mb-2">确认新密码</label>
            <input
              id="confirm-password"
              v-model="passwordForm.confirmPassword"
              type="password"
              required
              placeholder="请再次输入新密码"
              class="w-full px-6 py-4 bg-white/80 border border-white/60 rounded-3xl text-slate-900 placeholder-stone-400 focus:outline-none focus:border-amber-300 focus:ring-2 focus:ring-amber-200/60 transition-all"
            />
          </div>

          <div v-if="passwordError" class="p-4 bg-rose-100/70 border border-rose-200/60 rounded-3xl text-rose-700 text-sm">
            {{ passwordError }}
          </div>
          <div v-if="passwordMessage" class="p-4 bg-emerald-100/70 border border-emerald-200/60 rounded-3xl text-emerald-700 text-sm">
            {{ passwordMessage }}
          </div>

          <button
            type="submit"
            :disabled="passwordLoading"
            class="w-full px-8 py-4 rounded-3xl bg-gradient-to-r from-slate-800 to-slate-700 text-white font-semibold hover:scale-105 transition-all duration-300 shadow-md hover:shadow-lg disabled:opacity-50"
          >
            {{ passwordLoading ? '修改中...' : '确认修改密码' }}
          </button>
        </form>
      </div>
    </main>
  </div>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800;900&display=swap');

* {
  font-family: 'Inter', sans-serif;
}
</style>
