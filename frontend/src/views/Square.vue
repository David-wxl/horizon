<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { getSquareCards, type BentoCard } from '../api/card'
import { get } from '../api/request'
import BentoCardComponent from '../components/BentoCard.vue'
import NotificationBell from '../components/NotificationBell.vue'
import NavBar from '../components/NavBar.vue'
import { useRouter } from 'vue-router'
import { animateIn, observeCards } from '../composables/useAnimate'

const router = useRouter()

const isLoggedIn = ref(false)
const isAdmin = ref(false)

const cards = ref<BentoCard[]>([])
const allCards = ref<BentoCard[]>([])
const loading = ref(true)

const searchKeyword = ref('')
const sortBy = ref<'latest' | 'hot'>('latest')
const activeCategory = ref<string>('all')
const categories = ref<string[]>([])

interface SearchUser {
  id: number
  username: string
  nickname: string
  avatar: string
  bio: string
}
const matchedUsers = ref<SearchUser[]>([])

// refs for animation targets
const heroRef = ref<HTMLElement | null>(null)
const searchRef = ref<HTMLElement | null>(null)
const gridRef = ref<HTMLElement | null>(null)

function checkLoginStatus() {
  const token = localStorage.getItem('token')
  isLoggedIn.value = !!token
  const userStr = localStorage.getItem('user')
  if (userStr) {
    const user = JSON.parse(userStr)
    isAdmin.value = user.role === 'ADMIN'
  }
}

async function loadSquareCards() {
  loading.value = true
  try {
    const response = await getSquareCards(1, 100)
    allCards.value = response.records || []
    extractCategories()
    filterAndSortCards()
  } catch {
    alert('加载广场卡片失败')
  } finally {
    loading.value = false
    // 卡片加载完后触发入场动画
    requestAnimationFrame(() => animateCards())
  }
}

function extractCategories() {
  const cats = new Set<string>()
  allCards.value.forEach(c => { if (c.category) cats.add(c.category) })
  categories.value = Array.from(cats)
}

function setCategory(cat: string) {
  activeCategory.value = cat
  filterAndSortCards()
}

function filterAndSortCards() {
  let filtered = [...allCards.value]
  if (activeCategory.value !== 'all') {
    filtered = filtered.filter(card => card.category === activeCategory.value)
  }
  if (searchKeyword.value.trim()) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(card => {
      if (card.title?.toLowerCase().includes(keyword)) return true
      if (card.description?.toLowerCase().includes(keyword)) return true
      if (card.content) {
        const isBase64Data = card.content.startsWith('data:image') ||
          card.content.startsWith('data:') ||
          card.content.includes('base64')
        if (!isBase64Data && card.content.toLowerCase().includes(keyword)) return true
      }
      if (card.tags?.toLowerCase().includes(keyword)) return true
      if (card.category?.toLowerCase().includes(keyword)) return true
      return false
    })
  }
  if (sortBy.value === 'hot') {
    filtered.sort((a, b) => (b.likeCount || 0) - (a.likeCount || 0))
  } else {
    filtered.sort((a, b) => new Date(b.createTime || '').getTime() - new Date(a.createTime || '').getTime())
  }
  cards.value = filtered
}

async function handleSearch() {
  filterAndSortCards()
  if (searchKeyword.value.trim()) {
    try {
      matchedUsers.value = await get<SearchUser[]>(`/user/search?keyword=${encodeURIComponent(searchKeyword.value.trim())}`)
    } catch {
      matchedUsers.value = []
    }
  } else {
    matchedUsers.value = []
  }
}

function handleSort(type: 'latest' | 'hot') {
  sortBy.value = type
  filterAndSortCards()
}

function goHome() { router.push('/my') }
function goLogin() { router.push('/login') }
function handleLogout() {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  isLoggedIn.value = false
  router.push('/square')
}

function handleStorageChange(e: StorageEvent) {
  if (e.key === 'token' || e.key === 'user') {
    checkLoginStatus()
    loadSquareCards()
  }
}

let cardObserver: IntersectionObserver | undefined

function runEntryAnimation() {
  if (heroRef.value) {
    animateIn(Array.from(heroRef.value.children), { delay: 50, stagger: 120, duration: 650, from: { opacity: 0, y: 28, scale: 1 } })
  }
  if (searchRef.value) {
    animateIn(searchRef.value, { delay: 300, duration: 550, from: { opacity: 0, y: 20, scale: 0.97 } })
  }
}

function animateCards() {
  if (!gridRef.value) return
  cardObserver?.disconnect()
  cardObserver = observeCards(gridRef.value, '.bento-card-item') || undefined
}

onMounted(() => {
  checkLoginStatus()
  loadSquareCards()
  window.addEventListener('storage', handleStorageChange)
  runEntryAnimation()
})

onUnmounted(() => {
  window.removeEventListener('storage', handleStorageChange)
  cardObserver?.disconnect()
})
</script>

<template>
  <div class="min-h-screen relative text-slate-800">
    <!-- 顶部导航栏 -->
    <NavBar variant="logo" title="社区广场">
      <NotificationBell v-if="isLoggedIn" />
      <button v-if="isAdmin" @click="$router.push('/admin')" class="btn-admin">🔐 管理后台</button>
      <button v-if="isLoggedIn" @click="goHome" class="btn-secondary">我的主页</button>
      <button v-if="isLoggedIn" @click="handleLogout" class="btn-secondary">登出</button>
      <button v-if="!isLoggedIn" @click="goLogin" class="btn-primary">登录</button>
    </NavBar>

    <!-- 主内容 -->
    <main class="max-w-7xl mx-auto px-8 py-12">
      <!-- 页面标题 Hero -->
      <div ref="heroRef" class="mb-12 text-center">
        <h2 class="text-5xl font-bold text-slate-900 mb-4 tracking-tight">探索视界</h2>
        <p class="text-stone-500 text-lg">发现来自世界各地的精彩内容</p>
      </div>

      <!-- 搜索 & 分类区域 -->
      <div ref="searchRef">
        <!-- 分类筛选标签 -->
        <div v-if="categories.length > 0" class="mb-5 flex items-center gap-2.5 flex-wrap">
          <span class="text-sm text-stone-400 font-medium">分类</span>
          <button
            @click="setCategory('all')"
            class="px-4 py-1.5 rounded-full text-sm font-medium transition-all duration-200"
            :class="activeCategory === 'all' ? 'btn-primary' : 'btn-secondary'"
          >全部</button>
          <button
            v-for="cat in categories"
            :key="cat"
            @click="setCategory(cat)"
            class="px-4 py-1.5 rounded-full text-sm font-medium transition-all duration-200"
            :class="activeCategory === cat ? 'btn-primary' : 'btn-secondary'"
          >{{ cat }}</button>
        </div>

        <!-- 搜索和排序 -->
        <div class="mb-8 flex flex-col md:flex-row gap-4 items-center justify-between">
          <!-- 搜索框 -->
          <div class="w-full md:w-[480px]">
            <form @submit.prevent="handleSearch" class="relative flex gap-2">
              <div class="relative flex-1">
                <input
                  v-model="searchKeyword"
                  @keyup.enter="handleSearch"
                  type="text"
                  placeholder="探索everything"
                  class="w-full px-6 py-3.5 pl-11 bg-white/80 border border-white/70 rounded-2xl text-slate-900 placeholder-stone-400 focus:outline-none focus:border-amber-300 focus:ring-2 focus:ring-amber-200/50 transition-all text-sm shadow-sm"
                />
                <span class="absolute left-3.5 top-1/2 -translate-y-1/2 text-stone-400 text-base">🔍</span>
              </div>
              <button type="submit" class="btn-primary whitespace-nowrap">搜索</button>
            </form>
          </div>

          <!-- 排序按钮 -->
          <div class="flex items-center gap-2">
            <span class="text-sm text-stone-400 font-medium">排序</span>
            <button
              @click="handleSort('latest')"
              class="px-4 py-2 rounded-xl text-sm font-medium transition-all duration-200"
              :class="sortBy === 'latest' ? 'btn-primary' : 'btn-secondary'"
            >🕐 最新</button>
            <button
              @click="handleSort('hot')"
              class="px-4 py-2 rounded-xl text-sm font-medium transition-all duration-200"
              :class="sortBy === 'hot' ? 'btn-primary' : 'btn-secondary'"
            >🔥 最热</button>
          </div>
        </div>
      </div>

      <!-- 用户搜索结果 -->
      <div v-if="matchedUsers.length > 0" class="mb-8 fade-up">
        <h3 class="text-sm text-stone-400 mb-3 font-medium">找到 {{ matchedUsers.length }} 位相关用户</h3>
        <div class="flex gap-3 flex-wrap">
          <div
            v-for="u in matchedUsers"
            :key="u.id"
            @click="router.push(`/user/${u.id}`)"
            class="flex items-center gap-3 px-4 py-2.5 bg-white/80 border border-white/60 rounded-2xl cursor-pointer hover:shadow-md hover:bg-white hover:-translate-y-0.5 transition-all duration-200"
          >
            <img
              :src="u.avatar || `https://api.dicebear.com/7.x/avataaars/svg?seed=${u.username}`"
              :alt="u.nickname"
              class="w-9 h-9 rounded-full object-cover bg-stone-100"
            />
            <div>
              <div class="font-semibold text-slate-900 text-sm">{{ u.nickname || u.username }}</div>
              <div class="text-xs text-stone-400">@{{ u.username }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 加载中 -->
      <div v-if="loading" class="text-center py-24">
        <div class="inline-flex flex-col items-center gap-4">
          <div class="w-8 h-8 border-2 border-amber-300 border-t-transparent rounded-full animate-spin"></div>
          <p class="text-stone-400 text-sm">正在加载内容...</p>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else-if="cards.length === 0" class="text-center py-20 fade-up">
        <div class="glass-card p-16 max-w-2xl mx-auto">
          <h3 class="text-2xl font-semibold text-slate-900 mb-4">暂无公开内容</h3>
          <p class="text-stone-500 mb-8">成为第一个在广场分享内容的人吧！</p>
          <button @click="goHome" class="btn-primary px-8 py-3">前往我的主页</button>
        </div>
      </div>

      <!-- 卡片网格 -->
      <div v-else ref="gridRef" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        <div v-for="card in cards" :key="card.id" class="bento-card-item">
          <BentoCardComponent :card="card" :edit-mode="false" />
        </div>
      </div>
    </main>
  </div>
</template>
