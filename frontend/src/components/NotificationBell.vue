<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { 
  getNotifications, 
  getUnreadCount, 
  markNotificationAsRead, 
  markAllNotificationsAsRead,
  type Notification 
} from '../api/notification'

const router = useRouter()
const user = ref<any>(null)
const showPanel = ref(false)
const notifications = ref<Notification[]>([])
const unreadCount = ref(0)
const loading = ref(false)

// 加载用户信息
function loadUserInfo() {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    user.value = JSON.parse(userStr)
  }
}

// 显示所有通知
const showAllNotifications = ref(false)

// 加载通知
async function loadNotifications(limit?: number) {
  if (!user.value) return
  
  loading.value = true
  try {
    const [notifs, countData] = await Promise.all([
      getNotifications(user.value.id, limit || (showAllNotifications.value ? undefined : 10)),
      getUnreadCount(user.value.id)
    ])
    notifications.value = notifs
    unreadCount.value = countData.count
  } catch (error: any) {
    console.error('加载通知失败:', error)
  } finally {
    loading.value = false
  }
}

// 显示全部通知
function handleShowAllNotifications() {
  showAllNotifications.value = true
  loadNotifications()
}

// 切换面板显示
function togglePanel() {
  showPanel.value = !showPanel.value
  if (showPanel.value) {
    loadNotifications()
  }
}

// 点击通知
async function handleNotificationClick(notification: Notification) {
  // 标记为已读
  if (notification.isRead === 0) {
    try {
      await markNotificationAsRead(notification.id, user.value.id)
      notification.isRead = 1
      unreadCount.value = Math.max(0, unreadCount.value - 1)
    } catch (error: any) {
      console.error('标记已读失败:', error)
    }
  }
  
  // 如果有关联卡片，跳转到卡片详情
  if (notification.cardId) {
    showPanel.value = false
    router.push(`/card/${notification.cardId}`)
  }
}

// 标记全部已读
async function handleMarkAllRead() {
  if (!user.value || notifications.value.length === 0) return
  
  try {
    await markAllNotificationsAsRead(user.value.id)
    notifications.value.forEach(n => n.isRead = 1)
    unreadCount.value = 0
  } catch (error: any) {
    console.error('标记全部已读失败:', error)
  }
}

// 通知图标
const notificationIcon = computed(() => {
  return (type: string) => {
    switch (type) {
      case 'audit_pass': return '✅'
      case 'audit_reject': return '❌'
      case 'comment': return '💬'
      case 'like': return '❤️'
      case 'system': return '🔔'
      default: return '📢'
    }
  }
})

// 相对时间
const relativeTime = computed(() => {
  return (timeStr: string) => {
    const now = new Date()
    const time = new Date(timeStr)
    const diff = now.getTime() - time.getTime()
    const minutes = Math.floor(diff / 60000)
    const hours = Math.floor(diff / 3600000)
    const days = Math.floor(diff / 86400000)
    
    if (minutes < 1) return '刚刚'
    if (minutes < 60) return `${minutes}分钟前`
    if (hours < 24) return `${hours}小时前`
    if (days < 7) return `${days}天前`
    return time.toLocaleDateString()
  }
})

// 点击外部关闭面板
function handleClickOutside(event: MouseEvent) {
  const target = event.target as HTMLElement
  if (!target.closest('.notification-bell-container')) {
    showPanel.value = false
  }
}

// 监听 storage 变化（多标签页同步）
function handleStorageChange(e: StorageEvent) {
  if (e.key === 'user' || e.key === 'token') {
    loadUserInfo()
    if (user.value) {
      loadNotifications()
    } else {
      // 用户已登出
      notifications.value = []
      unreadCount.value = 0
      showPanel.value = false
    }
  }
}

onMounted(() => {
  loadUserInfo()
  if (user.value) {
    loadNotifications()
    // 每30秒刷新一次
    const timer = setInterval(loadNotifications, 30000)
    onUnmounted(() => clearInterval(timer))
  }
  document.addEventListener('click', handleClickOutside)
  window.addEventListener('storage', handleStorageChange)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
  window.removeEventListener('storage', handleStorageChange)
})
</script>

<template>
  <div class="notification-bell-container relative">
    <!-- 铃铛按钮 -->
    <button 
      @click.stop="togglePanel"
      class="relative p-2 rounded-full hover:bg-gray-100 transition-colors"
      :class="{ 'bg-gray-100': showPanel }"
    >
      <svg class="w-6 h-6 text-gray-700" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
          d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
      </svg>
      
      <!-- 红点提示 -->
      <span 
        v-if="unreadCount > 0" 
        class="absolute top-1 right-1 min-w-[18px] h-[18px] bg-red-500 text-white text-xs rounded-full flex items-center justify-center px-1 font-bold"
      >
        {{ unreadCount > 99 ? '99+' : unreadCount }}
      </span>
    </button>

    <!-- 通知面板 -->
    <transition name="fade-slide">
      <div 
        v-if="showPanel"
        class="absolute right-0 top-12 w-96 bg-white rounded-2xl shadow-2xl border border-gray-200 z-50 overflow-hidden"
        @click.stop
      >
        <!-- 头部 -->
        <div class="px-4 py-3 border-b border-gray-100 flex items-center justify-between">
          <h3 class="font-bold text-gray-900">通知</h3>
          <button 
            v-if="unreadCount > 0"
            @click="handleMarkAllRead"
            class="text-xs text-blue-600 hover:text-blue-700 font-medium"
          >
            全部标为已读
          </button>
        </div>

        <!-- 通知列表 -->
        <div class="max-h-96 overflow-y-auto">
          <!-- 加载状态 -->
          <div v-if="loading" class="p-8 text-center text-gray-500">
            <div class="inline-block w-6 h-6 border-2 border-gray-300 border-t-blue-600 rounded-full animate-spin"></div>
            <p class="mt-2 text-sm">加载中...</p>
          </div>

          <!-- 空状态 -->
          <div v-else-if="notifications.length === 0" class="p-8 text-center">
            <div class="text-5xl mb-3">🔔</div>
            <p class="text-gray-500 text-sm">暂无通知</p>
          </div>

          <!-- 通知项 -->
          <div v-else>
            <div 
              v-for="notification in notifications" 
              :key="notification.id"
              @click="handleNotificationClick(notification)"
              class="px-4 py-3 hover:bg-gray-50 cursor-pointer transition-colors border-b border-gray-50 last:border-b-0"
              :class="{ 'bg-blue-50/30': notification.isRead === 0 }"
            >
              <div class="flex items-start gap-3">
                <!-- 图标 -->
                <div class="flex-shrink-0 text-2xl">
                  {{ notificationIcon(notification.type) }}
                </div>

                <!-- 内容 -->
                <div class="flex-1 min-w-0">
                  <div class="flex items-start justify-between gap-2 mb-1">
                    <h4 class="font-semibold text-sm text-gray-900 line-clamp-1">
                      {{ notification.title }}
                    </h4>
                    <span 
                      v-if="notification.isRead === 0"
                      class="flex-shrink-0 w-2 h-2 bg-blue-600 rounded-full mt-1"
                    ></span>
                  </div>
                  <p class="text-sm text-gray-600 line-clamp-2 mb-1">
                    {{ notification.content }}
                  </p>
                  <p class="text-xs text-gray-400">
                    {{ relativeTime(notification.createTime) }}
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 底部 -->
        <div class="px-4 py-3 border-t border-gray-100 text-center">
          <button 
            v-if="!showAllNotifications"
            @click="handleShowAllNotifications"
            class="text-sm text-blue-600 hover:text-blue-700 font-medium"
          >
            查看全部通知
          </button>
          <button 
            v-else
            @click="() => { showAllNotifications = false; loadNotifications() }"
            class="text-sm text-gray-600 hover:text-gray-700 font-medium"
          >
            收起通知
          </button>
        </div>
      </div>
    </transition>
  </div>
</template>

<style scoped>
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s ease;
}

.fade-slide-enter-from,
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

.line-clamp-1 {
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
