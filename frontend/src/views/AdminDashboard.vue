<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { 
  getDashboardStats, getAuditQueue, getApprovedCards, getWeeklyActivity, auditCard,
  type DashboardStats, type AuditItem, type WeekActivity
} from '../api/admin'

const router = useRouter()
const user = ref<any>(null)
const loading = ref(true)
const dashboardStats = ref<DashboardStats>({ totalUsers: 0, totalCards: 0, totalComments: 0, pendingAudit: 0 })
const auditQueue = ref<AuditItem[]>([])
const approvedCards = ref<AuditItem[]>([])
const weeklyActivity = ref<WeekActivity[]>([])
const showModal = ref(false)
const modalTitle = ref('')
const modalList = ref<AuditItem[]>([])
const activeStatusFilter = ref<string>('all')
const cpuData = ref<number[]>([30, 35, 40, 38, 45, 42, 50])
const cpuTimer = ref<any>(null)
const onlineTime = ref('02:35')
const onlineProgress = ref(65)
const expandedMenus = ref<Record<string, boolean>>({ system: false, users: true, statistics: false, logs: false })
const currentMonth = ref('2024年9月')
const calendarDays = ref([
  { day: '周一', date: 22 }, { day: '周二', date: 23 }, { day: '周三', date: 24 },
  { day: '周四', date: 25 }, { day: '周五', date: 26 }, { day: '周六', date: 27 }
])

const progressData = computed(() => {
  const total = dashboardStats.value.totalCards || 1
  const pending = dashboardStats.value.pendingAudit || 0
  const approved = (dashboardStats.value.totalCards - dashboardStats.value.pendingAudit) || 0
  return [
    { label: '待审核', percentage: Math.round((pending / total) * 100), color: 'bg-slate-900', type: 'solid', value: pending, filter: 'pending' },
    { label: '已通过', percentage: Math.round((approved / total) * 100), color: 'bg-yellow-300', type: 'solid', value: approved, filter: 'approved' },
    { label: '处理中', percentage: 60, color: 'bg-stone-300', type: 'striped', value: Math.round(total * 0.6), filter: 'processing' },
    { label: '输出', percentage: 10, color: 'border-stone-400 bg-transparent', type: 'outline', value: Math.round(total * 0.1), filter: 'output' }
  ]
})

const weekAuditData = computed(() => {
  if (!weeklyActivity.value || weeklyActivity.value.length === 0) {
    return [
      { day: '周日', value: 30, active: false, count: 3 }, { day: '周一', value: 50, active: false, count: 5 },
      { day: '周二', value: 40, active: false, count: 4 }, { day: '周三', value: 60, active: false, count: 6 },
      { day: '周四', value: 85, active: true, highlight: '8张', count: 8 }, { day: '周五', value: 0, active: false, count: 0 },
      { day: '周六', value: 0, active: false, count: 0 }
    ]
  }
  const maxCount = Math.max(...weeklyActivity.value.map(d => d.count), 1)
  return weeklyActivity.value.map((item, index) => ({
    day: item.dayOfWeek, value: (item.count / maxCount) * 90, active: index === weeklyActivity.value.length - 2,
    count: item.count, highlight: item.count > 0 ? `${item.count}张` : undefined
  }))
})

const auditProgress = computed(() => {
  const total = auditQueue.value.length || 8
  const completed = auditQueue.value.filter(item => item.status === 1).length
  return Math.round((completed / total) * 100)
})

const auditTasks = computed(() => {
  if (auditQueue.value.length === 0) {
    return [
      { icon: '📝', title: '新用户注册审核', time: '今天 08:30', completed: true },
      { icon: '🖼️', title: '图片内容审核', time: '今天 10:30', completed: true },
      { icon: '💬', title: '评论审核', time: '今天 13:00', completed: false },
      { icon: '📦', title: '卡片内容审核', time: '今天 16:45', completed: false },
      { icon: '🔗', title: '链接有效性检查', time: '今天 16:50', completed: false }
    ]
  }
  return auditQueue.value.slice(0, 5).map((item, index) => ({
    id: item.id, icon: ['📝', '🖼️', '💬', '📦', '🔗'][index] || '📦',
    title: item.title, time: item.createTime || '', completed: item.status === 1
  }))
})

function loadUserInfo() {
  const userStr = localStorage.getItem('user')
  if (userStr) user.value = JSON.parse(userStr)
}

async function loadDashboardData() {
  loading.value = true
  try {
    const [stats, queue, approved, activity] = await Promise.all([
      getDashboardStats(), getAuditQueue(), getApprovedCards(), getWeeklyActivity()
    ])
    dashboardStats.value = stats
    auditQueue.value = queue
    approvedCards.value = approved
    weeklyActivity.value = activity
  } catch (error: any) {
    console.error('加载数据失败:', error)
    showToast('⚠️ 加载数据失败，请刷新重试')
  } finally {
    loading.value = false
  }
}

function startCPUAnimation() {
  cpuTimer.value = setInterval(() => {
    cpuData.value = cpuData.value.map(() => Math.random() * 30 + 30)
  }, 2000)
}

function stopCPUAnimation() {
  if (cpuTimer.value) {
    clearInterval(cpuTimer.value)
    cpuTimer.value = null
  }
}

function handleStatusClick(filter: string) {
  activeStatusFilter.value = filter
  if (filter === 'pending') showPendingList()
  else if (filter === 'approved') showApprovedList()
  else if (filter === 'processing') showToast('🔄 处理中的任务')
  else if (filter === 'output') showToast('📊 输出统计')
}

async function handleAuditFromList(task: any, status: number, index: number) {
  try {
    const action = status === 1 ? '通过' : '拒绝'
    if (!confirm(`确定要${action}这张卡片吗？`)) return
    await auditCard(task.id, status)
    auditQueue.value.splice(index, 1)
    showToast(`✓ 审核${action}成功`)
    const stats = await getDashboardStats()
    dashboardStats.value = stats
  } catch (error: any) {
    console.error('审核失败:', error)
    alert('操作失败: ' + error.message)
  }
}

async function handleAudit(item: AuditItem, status: number) {
  try {
    const action = status === 1 ? '通过' : '拒绝'
    if (!confirm(`确定要${action}这张卡片吗？`)) return
    await auditCard(item.id, status)
    showToast(`✓ 审核${action}成功`)
    await loadDashboardData()
    if (showModal.value) showModal.value = false
  } catch (error: any) {
    console.error('审核失败:', error)
    alert('操作失败: ' + error.message)
  }
}

function showPendingList() {
  modalTitle.value = '待审核卡片'
  modalList.value = auditQueue.value
  showModal.value = true
}

function showApprovedList() {
  modalTitle.value = '已通过卡片'
  modalList.value = approvedCards.value
  showModal.value = true
}

function closeModal() {
  showModal.value = false
}

function showToast(message: string) {
  const toast = document.createElement('div')
  toast.className = 'fixed top-24 right-8 bg-slate-900 text-white px-6 py-3 rounded-2xl shadow-2xl z-50 animate-fade-in'
  toast.textContent = message
  document.body.appendChild(toast)
  setTimeout(() => {
    toast.classList.add('animate-fade-out')
    setTimeout(() => document.body.removeChild(toast), 300)
  }, 2000)
}

function goToCardDetail(cardId: number) { router.push(`/card/${cardId}`) }
function navigateTo(path: string) { router.push(path) }
function handleLogout() {
  if (confirm('确定要退出登录吗？')) {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    router.push('/login')
  }
}
function toggleMenu(key: string) { expandedMenus.value[key] = !expandedMenus.value[key] }

onMounted(() => {
  loadUserInfo()
  loadDashboardData()
  startCPUAnimation()
})

onUnmounted(() => stopCPUAnimation())
</script>

<template>
  <div class="min-h-screen bg-[#BEBEC0] p-12 flex items-center justify-center">
    <div class="w-full max-w-[1800px] bg-[#F9F8F4] rounded-[60px] shadow-2xl overflow-hidden relative">
      <div class="absolute inset-0 bg-[radial-gradient(ellipse_at_top_right,_var(--tw-gradient-stops))] from-[#FFEBA0]/40 via-[#F9F8F4] to-transparent pointer-events-none"></div>

      <!-- 导航栏 -->
      <div class="relative z-10 flex items-center justify-between px-10 pt-8 pb-6">
        <div class="px-6 py-2 rounded-full border border-gray-200 bg-transparent">
          <span class="text-sm font-bold text-[#2D2D2D]">Horizon</span>
        </div>
        <div class="flex items-center gap-1">
          <div class="px-6 py-2.5 rounded-full bg-slate-900 text-white text-sm font-medium shadow-lg">仪表盘</div>
          <button @click="navigateTo('/my')" class="px-5 py-2.5 text-sm font-medium text-gray-600 hover:text-[#2D2D2D]">用户管理</button>
          <button @click="navigateTo('/square')" class="px-5 py-2.5 text-sm font-medium text-gray-600 hover:text-[#2D2D2D]">内容审核</button>
          <button class="px-5 py-2.5 text-sm font-medium text-gray-600 hover:text-[#2D2D2D]">数据统计</button>
          <button class="px-5 py-2.5 text-sm font-medium text-gray-600 hover:text-[#2D2D2D]">系统设置</button>
          <button class="px-5 py-2.5 text-sm font-medium text-gray-600 hover:text-[#2D2D2D]">操作日志</button>
        </div>
        <div class="flex items-center gap-4">
          <button class="text-gray-600 hover:text-[#2D2D2D] text-xl">⚙️</button>
          <button class="text-gray-600 hover:text-[#2D2D2D] text-xl">🔔</button>
          <button @click="navigateTo('/profile')" class="w-10 h-10 rounded-full border-2 border-gray-300 overflow-hidden">
            <img v-if="user?.avatar" :src="user.avatar" alt="Avatar" class="w-full h-full object-cover" />
            <div v-else class="w-full h-full bg-stone-300 flex items-center justify-center text-white font-bold">
              {{ (user?.nickname || 'A').charAt(0).toUpperCase() }}
            </div>
          </button>
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="relative z-10 px-10 pb-10 space-y-6 animate-pulse">
        <div class="h-24 bg-white/40 rounded-3xl"></div>
        <div class="grid grid-cols-12 gap-6">
          <div class="col-span-3 h-96 bg-white/40 rounded-3xl"></div>
          <div class="col-span-9 h-96 bg-white/40 rounded-3xl"></div>
        </div>
      </div>

      <!-- 主内容 -->
      <div v-else class="relative z-10 px-10 pb-10">
        <div class="mb-6">
          <h1 class="text-5xl font-bold text-[#2D2D2D] mb-6">欢迎回来，{{ user?.nickname || '管理员' }}</h1>
          <div class="flex items-center gap-3 mb-8">
            <button v-for="(item, index) in progressData" :key="index" @click="handleStatusClick(item.filter)"
              class="relative h-12 rounded-full overflow-hidden flex items-center justify-center transition-all hover:scale-105 hover:shadow-lg cursor-pointer"
              :style="{ width: Math.max(item.percentage * 2, 80) + 'px' }" :title="`点击查看${item.label}（${item.value}项）`">
              <div v-if="item.type === 'solid'" :class="[item.color, 'absolute inset-0']"></div>
              <div v-if="item.type === 'striped'" class="absolute inset-0 bg-stone-300"
                style="background-image: repeating-linear-gradient(45deg, transparent, transparent 10px, rgba(255,255,255,0.3) 10px, rgba(255,255,255,0.3) 20px);"></div>
              <div v-if="item.type === 'outline'" class="absolute inset-0 border-2 border-stone-400 rounded-full"></div>
              <span :class="['relative z-10 text-xs font-semibold', item.type === 'solid' && item.color.includes('slate') ? 'text-white' : 'text-[#2D2D2D]']">{{ item.label }}</span>
            </button>
            <div class="ml-auto flex items-center gap-12">
              <div class="text-right">
                <div class="flex items-center gap-2 mb-1 justify-end"><span class="text-xs text-stone-500">👥</span><span class="text-xs text-stone-500">用户总数</span></div>
                <div class="text-6xl font-light text-[#2D2D2D]">{{ dashboardStats.totalUsers }}</div>
              </div>
              <div class="text-right">
                <div class="flex items-center gap-2 mb-1 justify-end"><span class="text-xs text-stone-500">📦</span><span class="text-xs text-stone-500">待审核</span></div>
                <div class="text-6xl font-light text-[#2D2D2D]">{{ dashboardStats.pendingAudit }}</div>
              </div>
              <div class="text-right">
                <div class="flex items-center gap-2 mb-1 justify-end"><span class="text-xs text-stone-500">📁</span><span class="text-xs text-stone-500">卡片总数</span></div>
                <div class="text-6xl font-light text-[#2D2D2D]">{{ dashboardStats.totalCards }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 网格布局 -->
        <div class="grid grid-cols-12 gap-6">
          <!-- 左侧列 -->
          <div class="col-span-3 flex flex-col gap-6">
            <!-- 管理员卡片 -->
            <div class="bg-white/60 backdrop-blur-xl border border-white/80 shadow-[0_8px_30px_rgb(0,0,0,0.06)] rounded-[40px] overflow-hidden relative" style="height: 500px;">
              <div v-if="user?.avatar" class="w-full overflow-hidden" style="height: 65%;">
                <img :src="user.avatar" alt="头像" class="w-full h-full object-cover" />
              </div>
              <div v-else class="w-full bg-gradient-to-br from-stone-300/30 to-amber-100/20 flex items-center justify-center" style="height: 65%;">
                <span class="text-9xl font-light text-stone-400/40">{{ (user?.nickname || '管').charAt(0).toUpperCase() }}</span>
              </div>
              <div class="p-8 relative" style="height: 35%;">
                <h3 class="text-2xl font-bold text-[#2D2D2D]">{{ user?.nickname || '系统管理员' }}</h3>
                <p class="text-sm text-stone-500">超级管理员</p>
                <button @click="handleLogout" class="absolute bottom-4 right-4 px-6 py-2 bg-[#4A3B32] text-white rounded-full text-xs font-semibold hover:scale-105 transition-all shadow-lg">退出登录</button>
              </div>
            </div>

            <!-- 系统管理菜单 -->
            <div class="bg-white/60 backdrop-blur-xl border border-white/80 shadow-[0_8px_30px_rgb(0,0,0,0.06)] rounded-[40px] p-8 space-y-4">
              <div><button @click="toggleMenu('system')" class="w-full flex items-center justify-between py-3 hover:bg-white/50 rounded-xl transition-colors">
                <span class="font-medium text-[#2D2D2D]">系统设置</span><span class="text-stone-400">{{ expandedMenus.system ? '∧' : '∨' }}</span></button></div>
              <div>
                <button @click="toggleMenu('users')" class="w-full flex items-center justify-between py-3 hover:bg-white/50 rounded-xl transition-colors">
                  <span class="font-medium text-[#2D2D2D]">用户管理</span><span class="text-stone-400">{{ expandedMenus.users ? '∧' : '∨' }}</span></button>
                <div v-if="expandedMenus.users" class="mt-3 pl-4">
                  <div class="flex items-center gap-3 p-3 bg-white/50 rounded-xl cursor-pointer hover:bg-white/70" @click="navigateTo('/my')">
                    <div class="w-12 h-12 bg-gradient-to-br from-blue-500 to-cyan-500 rounded-lg flex-shrink-0 flex items-center justify-center text-white text-lg">👥</div>
                    <div class="flex-1">
                      <div class="font-semibold text-[#2D2D2D]">用户列表</div>
                      <div class="text-xs text-stone-500">{{ dashboardStats.totalUsers }} 个用户</div>
                    </div>
                    <button class="text-stone-400 hover:text-[#2D2D2D]">→</button>
                  </div>
                </div>
              </div>
              <div><button @click="toggleMenu('statistics')" class="w-full flex items-center justify-between py-3 hover:bg-white/50 rounded-xl transition-colors">
                <span class="font-medium text-[#2D2D2D]">内容统计</span><span class="text-stone-400">{{ expandedMenus.statistics ? '∧' : '∨' }}</span></button></div>
              <div><button @click="toggleMenu('logs')" class="w-full flex items-center justify-between py-3 hover:bg-white/50 rounded-xl transition-colors">
                <span class="font-medium text-[#2D2D2D]">审核日志</span><span class="text-stone-400">{{ expandedMenus.logs ? '∧' : '∨' }}</span></button></div>
            </div>
          </div>

          <!-- 中间列 - 简化布局，内容省略以减少行数 -->
          <div class="col-span-6 flex flex-col gap-6">
            <div class="grid grid-cols-2 gap-6">
              <!-- 审核进度卡片 -->
              <div class="bg-white/60 backdrop-blur-xl border border-white/80 shadow-[0_8px_30px_rgb(0,0,0,0.06)] rounded-[40px] p-8">
                <div class="flex items-start justify-between mb-6">
                  <div>
                    <h3 class="text-xl font-bold text-[#2D2D2D] mb-2">审核进度</h3>
                    <div class="text-4xl font-bold text-[#2D2D2D]">{{ weeklyActivity.reduce((sum, d) => sum + d.count, 0) }}<span class="text-lg font-normal text-stone-500">张</span></div>
                    <p class="text-xs text-stone-500 mt-1">本周已审核<br/>卡片数量</p>
                  </div>
                  <button class="text-stone-400 hover:text-[#2D2D2D]" @click="showApprovedList">↗</button>
                </div>
                <div class="flex items-end justify-between h-40 gap-4">
                  <div v-for="(item, index) in weekAuditData" :key="index" class="flex flex-col items-center gap-2 flex-1 relative">
                    <div v-if="item.highlight" class="absolute -top-10 left-1/2 -translate-x-1/2 px-3 py-1 bg-yellow-300 rounded-full text-xs font-bold text-[#2D2D2D] whitespace-nowrap">{{ item.highlight }}</div>
                    <div class="w-full flex justify-center items-end" style="height: 120px;">
                      <div :class="['w-4 rounded-full transition-all', item.active ? 'bg-yellow-300' : 'bg-stone-200']" :style="{ height: item.value + 'px' }">
                        <div class="w-2 h-2 rounded-full bg-slate-900 mx-auto mt-1"></div>
                      </div>
                    </div>
                    <span class="text-xs font-medium text-stone-600">{{ item.day }}</span>
                  </div>
                </div>
              </div>

              <!-- 在线时长卡片 -->
              <div class="bg-white/60 backdrop-blur-xl border border-white/80 shadow-[0_8px_30px_rgb(0,0,0,0.06)] rounded-[40px] p-8 flex flex-col">
                <div class="flex items-center justify-between mb-6">
                  <h3 class="text-xl font-bold text-[#2D2D2D]">在线时长</h3>
                  <button class="text-stone-400 hover:text-[#2D2D2D]">↗</button>
                </div>
                <div class="flex-1 flex items-center justify-center relative">
                  <svg class="w-48 h-48 -rotate-90">
                    <circle cx="96" cy="96" r="80" stroke="#E5E7EB" stroke-width="12" fill="none" />
                    <circle cx="96" cy="96" r="80" stroke="#FCD34D" stroke-width="12" fill="none" stroke-linecap="round" :stroke-dasharray="`${onlineProgress * 5.02} 502`" />
                  </svg>
                  <div class="absolute inset-0 flex flex-col items-center justify-center">
                    <div class="text-5xl font-bold text-[#2D2D2D]">{{ onlineTime }}</div>
                    <div class="text-sm text-stone-500 mt-2">今日在线</div>
                  </div>
                </div>
                <div class="flex items-center justify-center gap-4 mt-6">
                  <button class="w-12 h-12 rounded-full bg-white border-2 border-stone-200 flex items-center justify-center hover:bg-stone-50">▶</button>
                  <button class="w-12 h-12 rounded-full bg-white border-2 border-stone-200 flex items-center justify-center hover:bg-stone-50">⏸</button>
                  <button class="w-16 h-16 rounded-full bg-slate-900 text-white flex items-center justify-center text-2xl hover:bg-slate-800">⏱</button>
                </div>
              </div>
            </div>

            <!-- 日历视图 - 精简 -->
            <div class="bg-white/60 backdrop-blur-xl border border-white/80 shadow-[0_8px_30px_rgb(0,0,0,0.06)] rounded-[40px] p-8 flex-1">
              <div class="flex items-center justify-between mb-6">
                <button class="text-sm font-medium text-stone-600 hover:text-[#2D2D2D]">上个月</button>
                <h3 class="text-lg font-bold text-[#2D2D2D]">{{ currentMonth }}</h3>
                <button class="text-sm font-medium text-stone-600 hover:text-[#2D2D2D]">下个月</button>
              </div>
              <div class="grid grid-cols-6 gap-4 mb-4">
                <div v-for="day in calendarDays" :key="day.date" class="text-center">
                  <div class="text-xs text-stone-500 mb-1">{{ day.day }}</div>
                  <div class="text-lg font-semibold text-[#2D2D2D]">{{ day.date }}</div>
                </div>
              </div>
              <div class="space-y-2">
                <div class="flex items-center gap-4"><span class="text-sm text-stone-500 w-20">9:00</span>
                  <div class="flex-1 relative">
                    <div class="absolute left-[33%] right-0 bg-slate-900 text-white px-4 py-3 rounded-2xl flex items-center gap-3">
                      <div class="flex-1"><div class="font-semibold text-sm">批量审核任务</div><div class="text-xs opacity-70">审核新提交的卡片内容</div></div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 右侧列 - 审核任务 -->
          <div class="col-span-3 flex flex-col gap-6">
            <div class="bg-white/60 backdrop-blur-xl border border-white/80 shadow-[0_8px_30px_rgb(0,0,0,0.06)] rounded-[40px] p-8 flex flex-col flex-1">
              <div class="flex items-start justify-between mb-6">
                <h3 class="text-xl font-bold text-[#2D2D2D]">审核任务</h3>
                <div class="text-5xl font-light text-[#2D2D2D]">{{ auditProgress }}%</div>
              </div>
              <div class="space-y-4 mb-6">
                <div class="flex items-center gap-3">
                  <div class="flex-1 h-12 rounded-full overflow-hidden flex items-center px-4 bg-yellow-300">
                    <span class="text-sm font-semibold text-[#2D2D2D]">待处理</span>
                  </div>
                  <span class="text-xs text-stone-500 w-12 text-right">{{ auditQueue.length }}</span>
                </div>
                <div class="flex items-center gap-3">
                  <div class="flex-1 h-12 rounded-full overflow-hidden bg-slate-900"></div>
                  <span class="text-xs text-stone-500 w-12 text-right">{{ approvedCards.length }}</span>
                </div>
                <div class="flex items-center gap-3">
                  <div class="flex-1 h-12 rounded-full overflow-hidden bg-stone-300"></div>
                  <span class="text-xs text-stone-500 w-12 text-right">0</span>
                </div>
              </div>
              <div class="flex-1 bg-[#2A2A2A] rounded-[32px] p-6 overflow-hidden">
                <div class="flex items-center justify-between mb-4">
                  <h4 class="text-white font-bold">待审核列表</h4>
                  <span class="text-white text-xl font-light">{{ auditQueue.length }}/{{ auditQueue.length + approvedCards.length }}</span>
                </div>
                <div class="space-y-3 max-h-[300px] overflow-y-auto">
                  <div v-for="(task, index) in auditTasks" :key="index" class="flex items-center gap-3 group">
                    <div class="w-10 h-10 rounded-xl bg-white/10 flex items-center justify-center text-lg flex-shrink-0">{{ task.icon }}</div>
                    <div class="flex-1 min-w-0 cursor-pointer" @click="task.id && goToCardDetail(task.id)">
                      <div class="text-sm font-medium text-white/90 group-hover:text-white transition-all truncate">{{ task.title }}</div>
                      <div class="text-xs text-white/40">{{ task.time }}</div>
                    </div>
                    <button v-if="task.completed" class="w-6 h-6 rounded-full bg-yellow-300 flex items-center justify-center flex-shrink-0">
                      <span class="text-[#2D2D2D] text-xs">✓</span>
                    </button>
                    <button v-else-if="task.id" @click="handleAuditFromList(task, 1, index)"
                      class="w-6 h-6 rounded-full bg-white/10 hover:bg-yellow-300 flex items-center justify-center flex-shrink-0 transition-all group-hover:bg-yellow-300/20" title="标记为已审核">
                      <span class="text-white/40 group-hover:text-white text-xs">✓</span>
                    </button>
                    <div v-else class="w-6 h-6 rounded-full bg-white/10 flex-shrink-0"></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 模态框 -->
    <div v-if="showModal" class="fixed inset-0 bg-black/50 backdrop-blur-sm z-50 flex items-center justify-center p-8" @click.self="closeModal">
      <div class="bg-[#F9F8F4]/95 backdrop-blur-xl rounded-[40px] p-10 max-w-3xl w-full max-h-[80vh] overflow-hidden shadow-2xl">
        <div class="flex items-center justify-between mb-6">
          <h2 class="text-3xl font-bold text-[#2D2D2D]">{{ modalTitle }}</h2>
          <button @click="closeModal" class="w-12 h-12 rounded-full bg-stone-200 hover:bg-stone-300 flex items-center justify-center transition-all text-xl">✕</button>
        </div>
        <div class="overflow-y-auto max-h-[60vh] space-y-4">
          <div v-if="modalList.length === 0" class="text-center py-16">
            <div class="text-6xl mb-4">📭</div>
            <div class="text-stone-600 text-lg">暂无数据</div>
          </div>
          <div v-for="item in modalList" :key="item.id" class="bg-white/70 backdrop-blur-xl rounded-3xl p-6 border border-white/80 hover:shadow-lg transition-all">
            <div class="flex items-start justify-between gap-4">
              <div class="flex-1 min-w-0">
                <div class="text-xl font-bold text-[#2D2D2D] mb-3 cursor-pointer hover:text-yellow-600 transition-all" @click="goToCardDetail(item.id)">{{ item.title }}</div>
                <div class="flex items-center gap-4 text-sm text-stone-600">
                  <span>📦 {{ item.cardType }}</span>
                  <span>👤 {{ item.authorName }}</span>
                  <span :class="['px-4 py-1.5 rounded-full text-xs font-bold', item.status === 0 ? 'bg-yellow-100 text-yellow-700' : 'bg-emerald-100 text-emerald-700']">
                    {{ item.status === 0 ? '待审核' : '已通过' }}
                  </span>
                </div>
              </div>
              <div v-if="item.status === 0" class="flex gap-3 flex-shrink-0">
                <button @click="handleAudit(item, 1)" class="px-6 py-3 rounded-2xl bg-emerald-500 hover:bg-emerald-600 text-white font-bold transition-all hover:scale-105 shadow-lg">✓ 通过</button>
                <button @click="handleAudit(item, 2)" class="px-6 py-3 rounded-2xl bg-rose-500 hover:bg-rose-600 text-white font-bold transition-all hover:scale-105 shadow-lg">✕ 拒绝</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@100;200;300;400;500;600;700;800;900&display=swap');
* { font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif; -webkit-font-smoothing: antialiased; -moz-osx-font-smoothing: grayscale; }
@keyframes fade-in { from { opacity: 0; transform: translateY(-10px); } to { opacity: 1; transform: translateY(0); } }
@keyframes fade-out { from { opacity: 1; transform: translateY(0); } to { opacity: 0; transform: translateY(-10px); } }
.animate-fade-in { animation: fade-in 0.3s ease-out; }
.animate-fade-out { animation: fade-out 0.3s ease-out; }
::-webkit-scrollbar { width: 6px; }
::-webkit-scrollbar-track { background: rgba(0, 0, 0, 0.05); border-radius: 10px; }
::-webkit-scrollbar-thumb { background: rgba(0, 0, 0, 0.2); border-radius: 10px; }
::-webkit-scrollbar-thumb:hover { background: rgba(0, 0, 0, 0.3); }
</style>
