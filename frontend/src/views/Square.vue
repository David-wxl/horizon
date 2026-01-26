<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { getSquareCards, type BentoCard } from '../api/card'
import BentoCardComponent from '../components/BentoCard.vue'
import NotificationBell from '../components/NotificationBell.vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 用户登录状态
const isLoggedIn = ref(false)
const isAdmin = ref(false)

// 卡片列表
const cards = ref<BentoCard[]>([])
const allCards = ref<BentoCard[]>([])  // 所有卡片（用于搜索）
const loading = ref(true)

// 搜索和排序
const searchKeyword = ref('')
const sortBy = ref<'latest' | 'hot'>('latest')  // latest: 最新, hot: 最热

// 检查登录状态和角色
function checkLoginStatus() {
  const token = localStorage.getItem('token')
  isLoggedIn.value = !!token
  
  const userStr = localStorage.getItem('user')
  if (userStr) {
    const user = JSON.parse(userStr)
    isAdmin.value = user.role === 'ADMIN'
  }
}

// 加载广场卡片（不分页，加载所有）
async function loadSquareCards() {
  loading.value = true
  try {
    const response = await getSquareCards(1, 100)  // 一次加载100张卡片
    allCards.value = response.records || []
    filterAndSortCards()
  } catch (error: any) {
    console.error('加载广场卡片失败:', error)
  } finally {
    loading.value = false
  }
}

// 过滤和排序卡片
function filterAndSortCards() {
  let filtered = [...allCards.value]
  
  // 搜索过滤
  if (searchKeyword.value.trim()) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(card => {
      // 搜索标题
      if (card.title?.toLowerCase().includes(keyword)) return true
      
      // 搜索描述
      if (card.description?.toLowerCase().includes(keyword)) return true
      
      // 搜索内容（但排除Base64图片数据和其他二进制数据）
      if (card.content) {
        const isBase64Data = card.content.startsWith('data:image') || 
                            card.content.startsWith('data:') ||
                            card.content.includes('base64')
        if (!isBase64Data && card.content.toLowerCase().includes(keyword)) {
          return true
        }
      }
      
      // 搜索标签
      if (card.tags?.toLowerCase().includes(keyword)) return true
      
      // 搜索分类
      if (card.category?.toLowerCase().includes(keyword)) return true
      
      return false
    })
  }
  
  // 排序
  if (sortBy.value === 'hot') {
    filtered.sort((a, b) => (b.likeCount || 0) - (a.likeCount || 0))
  } else {
    filtered.sort((a, b) => new Date(b.createTime || '').getTime() - new Date(a.createTime || '').getTime())
  }
  
  cards.value = filtered
}

// 搜索处理
function handleSearch() {
  filterAndSortCards()
}

// 排序处理
function handleSort(type: 'latest' | 'hot') {
  sortBy.value = type
  filterAndSortCards()
}

// 前往个人主页
function goHome() {
  router.push('/my')
}

// 前往登录页
function goLogin() {
  router.push('/login')
}

// 登出
function handleLogout() {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  isLoggedIn.value = false
  router.push('/square')
}

// 监听 storage 变化（多标签页同步）
function handleStorageChange(e: StorageEvent) {
  if (e.key === 'token' || e.key === 'user') {
    // localStorage 变化时重新检查登录状态
    checkLoginStatus()
    loadSquareCards()
  }
}

onMounted(() => {
  checkLoginStatus()
  loadSquareCards()
  // 监听其他标签页的 localStorage 变化
  window.addEventListener('storage', handleStorageChange)
})

onUnmounted(() => {
  window.removeEventListener('storage', handleStorageChange)
})
</script>

<template>
  <div class="min-h-screen relative overflow-hidden text-slate-800">
    <!-- 顶部导航栏 -->
    <nav class="sticky top-0 z-50 backdrop-blur-xl bg-white/60 border-b border-white/60">
      <div class="max-w-7xl mx-auto px-8 py-6">
        <div class="flex items-center justify-between">
          <!-- Logo -->
          <div class="flex items-center gap-4">
            <h1 class="text-2xl font-semibold text-slate-900 tracking-tight">HORIZON</h1>
            <span class="text-sm text-stone-500">社区广场</span>
          </div>

          <!-- 操作按钮 -->
          <div class="flex items-center gap-4">
            <!-- 通知铃铛（仅登录用户可见） -->
            <NotificationBell v-if="isLoggedIn" />
            
            <button
              v-if="isAdmin"
              @click="$router.push('/admin')"
              class="px-6 py-3 rounded-2xl bg-gradient-to-r from-purple-300 via-pink-200 to-rose-200 text-slate-900 font-semibold shadow-md hover:scale-105 transition-all duration-300"
            >
              🔐 管理后台
            </button>
            
            <button
              v-if="isLoggedIn"
              @click="goHome"
              class="px-6 py-3 rounded-2xl bg-white/80 text-slate-700 border border-white/60 hover:bg-white transition-all duration-300"
            >
              我的主页
            </button>

            <button
              v-if="isLoggedIn"
              @click="handleLogout"
              class="px-6 py-3 rounded-2xl bg-white/80 text-slate-700 border border-white/60 hover:bg-white transition-all duration-300"
            >
              登出
            </button>

            <button
              v-if="!isLoggedIn"
              @click="goLogin"
              class="px-6 py-3 rounded-2xl bg-gradient-to-r from-amber-300 via-orange-200 to-stone-200 text-slate-900 font-semibold shadow-md hover:scale-105 transition-all duration-300"
            >
              登录
            </button>
          </div>
        </div>
      </div>
    </nav>

    <!-- 主内容 -->
    <main class="max-w-7xl mx-auto px-8 py-12">
      <!-- 页面标题 -->
      <div class="mb-12 text-center">
        <h2 class="text-4xl font-bold text-slate-900 mb-4">探索视界</h2>
        <p class="text-stone-600">发现来自世界各地的精彩内容</p>
      </div>

      <!-- 搜索和排序 -->
      <div class="mb-8 flex flex-col md:flex-row gap-4 items-center justify-between">
        <!-- 搜索框 -->
        <div class="w-full md:w-96">
          <form @submit.prevent="handleSearch" class="relative flex gap-2">
            <div class="relative flex-1">
              <input
                v-model="searchKeyword"
                @keyup.enter="handleSearch"
                type="text"
                placeholder="搜索卡片标题、内容、标签..."
                class="w-full px-6 py-4 pl-12 bg-white/80 border border-white/60 rounded-3xl text-slate-900 placeholder-stone-400 focus:outline-none focus:border-amber-300 focus:ring-2 focus:ring-amber-200/60 transition-all"
              />
              <span class="absolute left-4 top-1/2 -translate-y-1/2 text-stone-400 text-xl">
                🔍
              </span>
            </div>
            <button
              type="submit"
              class="px-6 py-4 rounded-2xl bg-gradient-to-r from-amber-300 via-orange-200 to-stone-200 text-slate-900 font-semibold hover:scale-105 transition-all duration-300 shadow-md whitespace-nowrap"
            >
              搜索
            </button>
          </form>
        </div>

        <!-- 排序按钮 -->
        <div class="flex items-center gap-3">
          <span class="text-sm text-stone-500">排序:</span>
          <button
            @click="handleSort('latest')"
            class="px-6 py-3 rounded-2xl transition-all duration-300"
            :class="sortBy === 'latest' 
              ? 'bg-gradient-to-r from-amber-300 via-orange-200 to-stone-200 text-slate-900 font-semibold shadow-md' 
              : 'bg-white/80 text-slate-700 border border-white/60 hover:bg-white'"
          >
            🕐 最新
          </button>
          <button
            @click="handleSort('hot')"
            class="px-6 py-3 rounded-2xl transition-all duration-300"
            :class="sortBy === 'hot' 
              ? 'bg-gradient-to-r from-amber-300 via-orange-200 to-stone-200 text-slate-900 font-semibold shadow-md' 
              : 'bg-white/80 text-slate-700 border border-white/60 hover:bg-white'"
          >
            🔥 最热
          </button>
        </div>
      </div>

      <!-- 加载中 -->
      <div v-if="loading" class="text-center py-20">
        <div class="text-stone-500">加载中...</div>
      </div>

      <!-- 空状态 -->
      <div v-else-if="cards.length === 0" class="text-center py-20">
        <div class="glass-card p-16 max-w-2xl mx-auto">
          <h3 class="text-2xl font-semibold text-slate-900 mb-4">暂无公开内容</h3>
          <p class="text-stone-600 mb-8">
            成为第一个在广场分享内容的人吧！
          </p>
          <button
            @click="goHome"
            class="px-8 py-4 rounded-3xl bg-gradient-to-r from-amber-300 via-orange-200 to-stone-200 text-slate-900 font-semibold hover:scale-105 transition-all duration-300 shadow-md hover:shadow-lg"
          >
            前往我的主页
          </button>
        </div>
      </div>

      <!-- 瀑布流网格布局 -->
      <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        <BentoCardComponent
          v-for="card in cards"
          :key="card.id"
          :card="card"
          :edit-mode="false"
        />
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
