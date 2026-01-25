<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { request } from '../api/request'
import type { BentoCard } from '../api/card'

const route = useRoute()
const router = useRouter()

// 用户信息
const user = ref<any>(null)
const loading = ref(true)

// 卡片列表
const cards = ref<BentoCard[]>([])

// 加载用户信息和卡片
async function loadUserProfile() {
  loading.value = true
  try {
    const userId = Number(route.params.userId)
    
    // 获取用户信息
    const userInfo = await request<any>(`/user/${userId}`, { method: 'GET' })
    user.value = userInfo
    
    // 获取用户的公开卡片
    const response = await request<BentoCard[]>(`/card/user/${userId}/public`, { method: 'GET' })
    cards.value = response || []
  } catch (error: any) {
    console.error('加载用户主页失败:', error)
  } finally {
    loading.value = false
  }
}

// 返回
function goBack() {
  router.back()
}

onMounted(() => {
  loadUserProfile()
})
</script>

<template>
  <div class="min-h-screen relative overflow-hidden text-slate-800">
    <!-- 顶部导航栏 -->
    <nav class="sticky top-0 z-50 backdrop-blur-xl bg-white/60 border-b border-white/60">
      <div class="max-w-7xl mx-auto px-8 py-6">
        <div class="flex items-center justify-between">
          <button
            @click="goBack"
            class="flex items-center gap-2 text-slate-700 hover:text-slate-900 transition-colors"
          >
            ← 返回
          </button>
          
          <h1 class="text-xl font-semibold text-slate-900 tracking-tight">用户主页</h1>
          
          <button
            @click="router.push('/square')"
            class="px-6 py-3 rounded-2xl bg-white/80 text-slate-700 border border-white/60 hover:bg-white transition-all duration-300"
          >
            社区广场
          </button>
        </div>
      </div>
    </nav>

    <!-- 主内容 -->
    <main class="max-w-7xl mx-auto px-8 py-12">
      <!-- 加载中 -->
      <div v-if="loading" class="text-center py-20">
        <div class="text-stone-500">加载中...</div>
      </div>

      <!-- 用户信息 -->
      <div v-else-if="user" class="space-y-8">
        <!-- 用户资料卡片 -->
        <div class="glass-card p-12">
          <div class="flex items-start gap-8">
            <!-- 头像 -->
            <div 
              v-if="user.avatar"
              class="w-32 h-32 rounded-full overflow-hidden ring-4 ring-white/60 flex-shrink-0"
            >
              <img :src="user.avatar" :alt="user.nickname || user.username" class="w-full h-full object-cover" />
            </div>
            <div 
              v-else
              class="w-32 h-32 rounded-full bg-gradient-to-br from-amber-300 to-orange-200 flex items-center justify-center text-slate-900 font-bold text-5xl ring-4 ring-white/60 flex-shrink-0"
            >
              {{ (user.nickname || user.username || '?').charAt(0).toUpperCase() }}
            </div>

            <!-- 用户信息 -->
            <div class="flex-1">
              <h2 class="text-3xl font-bold text-slate-900 mb-2">
                {{ user.nickname || user.username }}
              </h2>
              <p class="text-stone-500 mb-4">@{{ user.username }}</p>
              
              <p v-if="user.bio" class="text-stone-700 mb-6 leading-relaxed">
                {{ user.bio }}
              </p>

              <div class="flex items-center gap-6 text-sm text-stone-600">
                <span v-if="user.location" class="flex items-center gap-2">
                  📍 {{ user.location }}
                </span>
                <span v-if="user.gender" class="flex items-center gap-2">
                  {{ user.gender === 'male' ? '👨' : user.gender === 'female' ? '👩' : '🧑' }}
                  {{ user.gender === 'male' ? '男' : user.gender === 'female' ? '女' : '其他' }}
                </span>
                <span class="flex items-center gap-2">
                  📄 {{ cards.length }} 张公开卡片
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- 用户的公开卡片 -->
        <div>
          <h3 class="text-2xl font-semibold text-slate-900 mb-6">TA 的作品</h3>
          
          <!-- 空状态 -->
          <div v-if="cards.length === 0" class="text-center py-20">
            <div class="glass-card p-16 max-w-2xl mx-auto">
              <p class="text-stone-600">该用户还没有公开的作品</p>
            </div>
          </div>

          <!-- 卡片网格 -->
          <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            <div
              v-for="card in cards"
              :key="card.id"
              class="glass-card p-6 cursor-pointer hover:shadow-lg transition-all duration-300"
              @click="router.push(`/card/${card.id}`)"
            >
              <h4 class="text-lg font-semibold text-slate-900 mb-2">{{ card.title }}</h4>
              <p v-if="card.description" class="text-sm text-stone-600 mb-4 line-clamp-2">
                {{ card.description }}
              </p>
              <div class="flex items-center justify-between text-xs text-stone-500">
                <span>{{ card.cardType }}</span>
                <span v-if="card.likeCount">❤️ {{ card.likeCount }}</span>
              </div>
            </div>
          </div>
        </div>
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
