<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { request } from '../api/request'
import type { BentoCard } from '../api/card'
import { followUser, unfollowUser, checkFollowing, getFollowStats } from '../api/follow'
import NavBar from '../components/NavBar.vue'
import { animateIn } from '../composables/useAnimate'

const route = useRoute()
const router = useRouter()

// 用户信息
const user = ref<any>(null)
const loading = ref(true)

// 卡片列表
const cards = ref<BentoCard[]>([])

// 关注相关
const isFollowing = ref(false)
const followerCount = ref(0)
const followingCount = ref(0)
const followLoading = ref(false)
const currentUserId = computed(() => {
  const s = localStorage.getItem('user')
  return s ? JSON.parse(s).id : null
})
const isOwnProfile = computed(() => currentUserId.value === Number(route.params.userId))

async function loadFollowData(targetId: number) {
  try {
    const stats = await getFollowStats(targetId)
    followerCount.value = stats.followerCount
    followingCount.value = stats.followingCount
    if (currentUserId.value && !isOwnProfile.value) {
      isFollowing.value = await checkFollowing(currentUserId.value, targetId)
    }
  } catch {}
}

async function toggleFollow() {
  if (!currentUserId.value) { router.push('/login'); return }
  const targetId = Number(route.params.userId)
  followLoading.value = true
  try {
    if (isFollowing.value) {
      await unfollowUser(currentUserId.value, targetId)
      isFollowing.value = false
      followerCount.value = Math.max(0, followerCount.value - 1)
    } else {
      await followUser(currentUserId.value, targetId)
      isFollowing.value = true
      followerCount.value += 1
    }
  } catch {
    await loadFollowData(targetId)
  } finally {
    followLoading.value = false
  }
}

// 加载用户信息和卡片
async function loadUserProfile() {
  loading.value = true
  try {
    const userId = Number(route.params.userId)
    
    const userInfo = await request<any>(`/user/${userId}`, { method: 'GET' })
    user.value = userInfo
    
    const response = await request<BentoCard[]>(`/card/user/${userId}/public`, { method: 'GET' })
    cards.value = response || []

    await loadFollowData(userId)
  } catch (error: any) {
    alert('加载用户主页失败: ' + error.message)
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
  const main = document.querySelector('.userhome-main') as HTMLElement | null
  if (main) animateIn(main, { delay: 50, duration: 550, from: { opacity: 0, y: 28, scale: 1 } })
})
</script>

<template>
  <div class="min-h-screen relative text-slate-800">
    <NavBar variant="back" title="用户主页">
      <button @click="router.push('/square')" class="btn-secondary">🌍 社区广场</button>
    </NavBar>

    <!-- 主内容 -->
    <main class="userhome-main max-w-7xl mx-auto px-8 py-12">
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

              <!-- 关注统计 -->
              <div class="flex items-center gap-6 mb-4">
                <span class="text-sm"><strong class="text-lg text-slate-900">{{ followerCount }}</strong> <span class="text-stone-500">粉丝</span></span>
                <span class="text-sm"><strong class="text-lg text-slate-900">{{ followingCount }}</strong> <span class="text-stone-500">关注</span></span>
                <span class="text-sm"><strong class="text-lg text-slate-900">{{ cards.length }}</strong> <span class="text-stone-500">作品</span></span>
              </div>

              <div class="flex items-center gap-6 text-sm text-stone-600">
                <span v-if="user.location" class="flex items-center gap-2">
                  📍 {{ user.location }}
                </span>
                <span v-if="user.gender" class="flex items-center gap-2">
                  {{ user.gender === 'male' ? '👨' : user.gender === 'female' ? '👩' : '🧑' }}
                  {{ user.gender === 'male' ? '男' : user.gender === 'female' ? '女' : '其他' }}
                </span>
              </div>

              <!-- 关注按钮 -->
              <div v-if="!isOwnProfile && currentUserId" class="mt-4">
                <button
                  @click="toggleFollow"
                  :disabled="followLoading"
                  class="px-8 py-3 rounded-2xl font-semibold transition-all duration-300 hover:scale-105 shadow-md"
                  :class="isFollowing
                    ? 'bg-white/80 text-slate-700 border border-white/60 hover:bg-rose-50 hover:text-rose-600 hover:border-rose-200'
                    : 'bg-gradient-to-r from-amber-300 via-orange-200 to-stone-200 text-slate-900'"
                >
                  {{ followLoading ? '...' : isFollowing ? '已关注' : '+ 关注' }}
                </button>
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
