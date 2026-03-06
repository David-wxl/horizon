<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getUserFavorites } from '../api/favorite'
import type { BentoCard } from '../api/card'
import BentoCardComponent from '../components/BentoCard.vue'

const router = useRouter()
const cards = ref<BentoCard[]>([])
const loading = ref(true)
const userId = ref<number>(0)

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
  }
}

onMounted(() => {
  loadUserInfo()
  loadFavorites()
})
</script>

<template>
  <div class="min-h-screen relative overflow-hidden text-slate-800">
    <nav class="sticky top-0 z-50 backdrop-blur-xl bg-white/60 border-b border-white/60">
      <div class="max-w-7xl mx-auto px-8 py-6">
        <div class="flex items-center justify-between">
          <button
            @click="router.back()"
            class="flex items-center gap-2 text-slate-700 hover:text-slate-900 transition-colors"
          >← 返回</button>
          <h1 class="text-xl font-semibold text-slate-900 tracking-tight">我的收藏</h1>
          <div class="w-20"></div>
        </div>
      </div>
    </nav>

    <main class="max-w-7xl mx-auto px-8 py-12">
      <div v-if="loading" class="text-center py-20">
        <div class="text-stone-500">加载中...</div>
      </div>

      <div v-else-if="cards.length === 0" class="text-center py-20">
        <div class="glass-card p-16 max-w-2xl mx-auto">
          <h2 class="text-2xl font-semibold text-slate-900 mb-4">暂无收藏</h2>
          <p class="text-stone-600 mb-8">去广场发现喜欢的内容并收藏吧！</p>
          <button
            @click="router.push('/square')"
            class="px-8 py-4 rounded-3xl bg-gradient-to-r from-amber-300 via-orange-200 to-stone-200 text-slate-900 font-semibold hover:scale-105 transition-all duration-300 shadow-md hover:shadow-lg"
          >前往广场</button>
        </div>
      </div>

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
* { font-family: 'Inter', sans-serif; }
</style>
