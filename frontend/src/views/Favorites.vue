<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getUserFavorites } from '../api/favorite'
import type { BentoCard } from '../api/card'
import BentoCardComponent from '../components/BentoCard.vue'
import NavBar from '../components/NavBar.vue'
import { animateIn, observeCards } from '../composables/useAnimate'

const router = useRouter()
const cards = ref<BentoCard[]>([])
const loading = ref(true)
const userId = ref<number>(0)
const gridRef = ref<HTMLElement | null>(null)

function loadUserInfo() {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    userId.value = JSON.parse(userStr).id
  }
}

async function loadFavorites() {
  loading.value = true
  try {
    cards.value = await getUserFavorites(userId.value) || []
  } catch (error: any) {
    alert('加载收藏失败: ' + error.message)
  } finally {
    loading.value = false
    requestAnimationFrame(() => animateCards())
  }
}

function animateCards() {
  if (!gridRef.value) return
  observeCards(gridRef.value, ':scope > *')
}

onMounted(() => {
  loadUserInfo()
  loadFavorites()
  const header = document.querySelector('.fav-header') as HTMLElement | null
  if (header) animateIn(header, { delay: 50, duration: 500, from: { opacity: 0, y: 20, scale: 1 } })
})
</script>

<template>
  <div class="min-h-screen relative text-slate-800">
    <NavBar variant="back" title="我的收藏" />

    <main class="max-w-7xl mx-auto px-8 py-12 fav-header">
      <div v-if="loading" class="text-center py-24">
        <div class="inline-flex flex-col items-center gap-4">
          <div class="w-8 h-8 border-2 border-amber-300 border-t-transparent rounded-full animate-spin"></div>
          <p class="text-stone-400 text-sm">正在加载收藏...</p>
        </div>
      </div>

      <div v-else-if="cards.length === 0" class="text-center py-20 fade-up">
        <div class="glass-card p-16 max-w-2xl mx-auto">
          <h2 class="text-2xl font-semibold text-slate-900 mb-4">暂无收藏</h2>
          <p class="text-stone-500 mb-8">去广场发现喜欢的内容并收藏吧！</p>
          <button @click="router.push('/square')" class="btn-primary px-8 py-3">前往广场</button>
        </div>
      </div>

      <div v-else ref="gridRef" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
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
