<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { 
  getDashboardStats, getAuditQueue, getApprovedCards, getWeeklyActivity, auditCard,
  getChartData, type DashboardStats, type AuditItem, type WeekActivity, type ChartData
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

// ECharts 相关
const chartData = ref<ChartData | null>(null)
const pieChartRef = ref<HTMLElement | null>(null)
const lineChartRef = ref<HTMLElement | null>(null)
const barChartRef = ref<HTMLElement | null>(null)
const statusChartRef = ref<HTMLElement | null>(null)
let pieChart: echarts.ECharts | null = null
let lineChart: echarts.ECharts | null = null
let barChart: echarts.ECharts | null = null
let statusChart: echarts.ECharts | null = null

async function loadChartData() {
  try {
    chartData.value = await getChartData()
    await nextTick()
    renderCharts()
  } catch {}
}

function renderCharts() {
  if (!chartData.value) return
  
  // 卡片类型饼图
  if (pieChartRef.value) {
    pieChart = echarts.init(pieChartRef.value)
    pieChart.setOption({
      tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
      color: ['#fbbf24', '#2d2d2d', '#a8a29e', '#fb923c', '#6ee7b7', '#93c5fd'],
      series: [{
        type: 'pie', radius: ['40%', '70%'], padAngle: 3,
        itemStyle: { borderRadius: 6 },
        label: { show: true, fontSize: 12 },
        data: chartData.value.typeDistribution
      }]
    })
  }

  // 每日新增折线图
  if (lineChartRef.value) {
    lineChart = echarts.init(lineChartRef.value)
    const dates = chartData.value.dailyCards.map(d => d.date.slice(5))
    lineChart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: 40, right: 20, top: 30, bottom: 30 },
      xAxis: { type: 'category', data: dates, axisLabel: { fontSize: 10, interval: 4 } },
      yAxis: { type: 'value', minInterval: 1 },
      series: [
        { name: '新增卡片', type: 'line', data: chartData.value.dailyCards.map(d => d.count), smooth: true, lineStyle: { color: '#fbbf24', width: 3 }, itemStyle: { color: '#fbbf24' }, areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: 'rgba(251,191,36,0.3)' }, { offset: 1, color: 'rgba(251,191,36,0.02)' }] } } },
        { name: '新增用户', type: 'line', data: chartData.value.dailyUsers.map(d => d.count), smooth: true, lineStyle: { color: '#2d2d2d', width: 3 }, itemStyle: { color: '#2d2d2d' }, areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: 'rgba(45,45,45,0.15)' }, { offset: 1, color: 'rgba(45,45,45,0.01)' }] } } }
      ]
    })
  }

  // 热门卡片柱状图
  if (barChartRef.value && chartData.value.hotCards.length > 0) {
    barChart = echarts.init(barChartRef.value)
    barChart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: 80, right: 20, top: 30, bottom: 30 },
      yAxis: { type: 'category', data: chartData.value.hotCards.map(c => c.title).reverse(), axisLabel: { fontSize: 11 } },
      xAxis: { type: 'value', minInterval: 1 },
      series: [
        { name: '点赞', type: 'bar', data: chartData.value.hotCards.map(c => c.likes).reverse(), itemStyle: { color: '#fbbf24', borderRadius: [0, 6, 6, 0] }, barWidth: 14 },
        { name: '浏览', type: 'bar', data: chartData.value.hotCards.map(c => c.views).reverse(), itemStyle: { color: '#d6d3d1', borderRadius: [0, 6, 6, 0] }, barWidth: 14 }
      ]
    })
  }

  // 卡片状态环形图
  if (statusChartRef.value) {
    statusChart = echarts.init(statusChartRef.value)
    statusChart.setOption({
      tooltip: { trigger: 'item' },
      color: ['#fbbf24', '#2d2d2d', '#ef4444'],
      series: [{
        type: 'pie', radius: ['55%', '80%'], padAngle: 4,
        itemStyle: { borderRadius: 8 },
        label: { show: true, fontSize: 12 },
        data: chartData.value.statusDistribution
      }]
    })
  }
}

function disposeCharts() {
  pieChart?.dispose()
  lineChart?.dispose()
  barChart?.dispose()
  statusChart?.dispose()
}

function resizeCharts() {
  pieChart?.resize()
  lineChart?.resize()
  barChart?.resize()
  statusChart?.resize()
}

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

// 管理员自定义 Todo 列表（localStorage 持久化）
interface AdminTodo { id: string; title: string; completed: boolean; createdAt: string }
const adminTodos = ref<AdminTodo[]>([])
const newTodoText = ref('')

function loadAdminTodos() {
  try {
    const saved = localStorage.getItem('admin_todos')
    if (saved) {
      adminTodos.value = JSON.parse(saved)
    } else {
      adminTodos.value = [
        { id: '1', title: '审核今日新提交的卡片', completed: false, createdAt: new Date().toISOString() },
        { id: '2', title: '检查社区广场内容质量', completed: false, createdAt: new Date().toISOString() },
        { id: '3', title: '回复用户反馈', completed: false, createdAt: new Date().toISOString() },
      ]
      saveAdminTodos()
    }
  } catch { adminTodos.value = [] }
}

function saveAdminTodos() {
  localStorage.setItem('admin_todos', JSON.stringify(adminTodos.value))
}

function toggleTodo(id: string) {
  const todo = adminTodos.value.find(t => t.id === id)
  if (todo) { todo.completed = !todo.completed; saveAdminTodos() }
}

function addTodo() {
  if (!newTodoText.value.trim()) return
  adminTodos.value.unshift({
    id: Date.now().toString(),
    title: newTodoText.value.trim(),
    completed: false,
    createdAt: new Date().toISOString()
  })
  newTodoText.value = ''
  saveAdminTodos()
}

function removeTodo(id: string) {
  adminTodos.value = adminTodos.value.filter(t => t.id !== id)
  saveAdminTodos()
}

const todoProgress = computed(() => {
  if (adminTodos.value.length === 0) return 0
  return Math.round((adminTodos.value.filter(t => t.completed).length / adminTodos.value.length) * 100)
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
    let reason = ''
    
    // 如果是拒绝，要求输入理由
    if (status === 2) {
      reason = prompt('请输入拒绝理由（必填）：') || ''
      if (!reason.trim()) {
        alert('拒绝理由不能为空')
        return
      }
    } else {
      if (!confirm(`确定要${action}这张卡片吗？`)) return
    }
    
    await auditCard(task.id, status, reason)
    auditQueue.value.splice(index, 1)
    showToast(`✓ 审核${action}成功${status === 2 ? '，已通知用户' : ''}`)
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
    let reason = ''
    
    // 如果是拒绝，要求输入理由
    if (status === 2) {
      reason = prompt('请输入拒绝理由（必填）：') || ''
      if (!reason.trim()) {
        alert('拒绝理由不能为空')
        return
      }
    } else {
      if (!confirm(`确定要${action}这张卡片吗？`)) return
    }
    
    await auditCard(item.id, status, reason)
    showToast(`✓ 审核${action}成功${status === 2 ? '，已通知用户' : ''}`)
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

const chartsSection = ref<HTMLElement | null>(null)
function scrollToCharts() {
  chartsSection.value?.scrollIntoView({ behavior: 'smooth', block: 'center' })
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

// 监听 storage 变化（多标签页同步）
function handleStorageChange(e: StorageEvent) {
  if (e.key === 'token' || e.key === 'user') {
    const token = localStorage.getItem('token')
    const userStr = localStorage.getItem('user')
    
    if (!token) {
      // Token 被清除，跳转到登录页
      router.push('/login')
    } else if (userStr) {
      const newUser = JSON.parse(userStr)
      // 检查新用户是否还是管理员
      if (newUser.role !== 'ADMIN') {
        // 不再是管理员，跳转到广场
        alert('您已不再是管理员')
        router.push('/square')
      } else {
        // 重新加载数据
        loadUserInfo()
        loadDashboardData()
      }
    }
  }
}

onMounted(() => {
  loadUserInfo()
  loadDashboardData()
  startCPUAnimation()
  loadChartData()
  loadAdminTodos()
  window.addEventListener('storage', handleStorageChange)
  window.addEventListener('resize', resizeCharts)
})

onUnmounted(() => {
  stopCPUAnimation()
  disposeCharts()
  window.removeEventListener('storage', handleStorageChange)
  window.removeEventListener('resize', resizeCharts)
})
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
          <div class="px-6 py-2.5 rounded-full bg-slate-900 text-white text-sm font-medium shadow-lg cursor-default">仪表盘</div>
          <button @click="showPendingList" class="px-5 py-2.5 text-sm font-medium text-gray-600 hover:text-[#2D2D2D] hover:bg-white/50 rounded-full transition-all">内容审核</button>
          <button @click="navigateTo('/square')" class="px-5 py-2.5 text-sm font-medium text-gray-600 hover:text-[#2D2D2D] hover:bg-white/50 rounded-full transition-all">社区广场</button>
          <button @click="scrollToCharts" class="px-5 py-2.5 text-sm font-medium text-gray-600 hover:text-[#2D2D2D] hover:bg-white/50 rounded-full transition-all">数据统计</button>
          <button @click="navigateTo('/profile')" class="px-5 py-2.5 text-sm font-medium text-gray-600 hover:text-[#2D2D2D] hover:bg-white/50 rounded-full transition-all">个人设置</button>
          <button @click="navigateTo('/my')" class="px-5 py-2.5 text-sm font-medium text-gray-600 hover:text-[#2D2D2D] hover:bg-white/50 rounded-full transition-all">我的主页</button>
        </div>
        <div class="flex items-center gap-4">
          <button @click="navigateTo('/profile')" class="text-gray-600 hover:text-[#2D2D2D] text-xl" title="个人设置">⚙️</button>
          <button @click="showPendingList" class="text-gray-600 hover:text-[#2D2D2D] text-xl" title="待审核">🔔</button>
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

            <!-- 快捷操作 -->
            <div class="bg-white/60 backdrop-blur-xl border border-white/80 shadow-[0_8px_30px_rgb(0,0,0,0.06)] rounded-[40px] p-8 space-y-3">
              <h3 class="font-bold text-[#2D2D2D] mb-2">快捷操作</h3>
              <div class="flex items-center gap-3 p-3 bg-white/50 rounded-xl cursor-pointer hover:bg-white/70 transition-all" @click="showPendingList">
                <div class="w-10 h-10 bg-amber-100 rounded-lg flex-shrink-0 flex items-center justify-center text-lg">📦</div>
                <div class="flex-1">
                  <div class="font-semibold text-sm text-[#2D2D2D]">内容审核</div>
                  <div class="text-xs text-stone-500">{{ dashboardStats.pendingAudit }} 条待审核</div>
                </div>
                <span class="text-stone-400">→</span>
              </div>
              <div class="flex items-center gap-3 p-3 bg-white/50 rounded-xl cursor-pointer hover:bg-white/70 transition-all" @click="navigateTo('/square')">
                <div class="w-10 h-10 bg-blue-100 rounded-lg flex-shrink-0 flex items-center justify-center text-lg">🌍</div>
                <div class="flex-1">
                  <div class="font-semibold text-sm text-[#2D2D2D]">社区广场</div>
                  <div class="text-xs text-stone-500">{{ dashboardStats.totalCards }} 张卡片</div>
                </div>
                <span class="text-stone-400">→</span>
              </div>
              <div class="flex items-center gap-3 p-3 bg-white/50 rounded-xl cursor-pointer hover:bg-white/70 transition-all" @click="scrollToCharts">
                <div class="w-10 h-10 bg-emerald-100 rounded-lg flex-shrink-0 flex items-center justify-center text-lg">📊</div>
                <div class="flex-1">
                  <div class="font-semibold text-sm text-[#2D2D2D]">数据统计</div>
                  <div class="text-xs text-stone-500">查看趋势图表</div>
                </div>
                <span class="text-stone-400">→</span>
              </div>
              <div class="flex items-center gap-3 p-3 bg-white/50 rounded-xl cursor-pointer hover:bg-white/70 transition-all" @click="navigateTo('/profile')">
                <div class="w-10 h-10 bg-purple-100 rounded-lg flex-shrink-0 flex items-center justify-center text-lg">⚙️</div>
                <div class="flex-1">
                  <div class="font-semibold text-sm text-[#2D2D2D]">个人设置</div>
                  <div class="text-xs text-stone-500">修改资料和密码</div>
                </div>
                <span class="text-stone-400">→</span>
              </div>
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

              <!-- 卡片类型分布 -->
              <div class="bg-white/60 backdrop-blur-xl border border-white/80 shadow-[0_8px_30px_rgb(0,0,0,0.06)] rounded-[40px] p-8 flex flex-col">
                <div class="flex items-center justify-between mb-4">
                  <h3 class="text-xl font-bold text-[#2D2D2D]">卡片类型分布</h3>
                </div>
                <div ref="pieChartRef" style="width: 100%; height: 260px;"></div>
                <div class="mt-2">
                  <h4 class="text-sm font-bold text-[#2D2D2D] mb-2">审核状态</h4>
                  <div ref="statusChartRef" style="width: 100%; height: 140px;"></div>
                </div>
              </div>
            </div>

            <!-- 数据趋势图 -->
            <div ref="chartsSection" class="bg-white/60 backdrop-blur-xl border border-white/80 shadow-[0_8px_30px_rgb(0,0,0,0.06)] rounded-[40px] p-8 flex-1">
              <div class="flex items-center justify-between mb-4">
                <h3 class="text-lg font-bold text-[#2D2D2D]">数据趋势（近30天）</h3>
              </div>
              <div ref="lineChartRef" style="width: 100%; height: 280px;"></div>
            </div>
          </div>

          <!-- 右侧列 - 审核任务 & 热门卡片 -->
          <div class="col-span-3 flex flex-col gap-6">
            <!-- 热门卡片排行 -->
            <div class="bg-white/60 backdrop-blur-xl border border-white/80 shadow-[0_8px_30px_rgb(0,0,0,0.06)] rounded-[40px] p-8">
              <h3 class="text-xl font-bold text-[#2D2D2D] mb-4">热门卡片 TOP10</h3>
              <div ref="barChartRef" style="width: 100%; height: 300px;"></div>
            </div>

            <div class="bg-white/60 backdrop-blur-xl border border-white/80 shadow-[0_8px_30px_rgb(0,0,0,0.06)] rounded-[40px] p-8 flex flex-col flex-1">
              <div class="flex items-start justify-between mb-6">
                <h3 class="text-xl font-bold text-[#2D2D2D]">待办事项</h3>
                <div class="text-5xl font-light text-[#2D2D2D]">{{ todoProgress }}%</div>
              </div>

              <!-- 添加新待办 -->
              <form @submit.prevent="addTodo" class="flex gap-2 mb-4">
                <input v-model="newTodoText" placeholder="添加新待办..."
                  class="flex-1 px-4 py-3 bg-white/70 border border-white/80 rounded-2xl text-sm text-slate-900 placeholder-stone-400 focus:outline-none focus:ring-2 focus:ring-amber-200/60" />
                <button type="submit" class="px-5 py-3 bg-slate-900 text-white rounded-2xl text-sm font-semibold hover:scale-105 transition-all">+</button>
              </form>

              <!-- Todo 列表 -->
              <div class="flex-1 bg-[#2A2A2A] rounded-[32px] p-6 overflow-hidden">
                <div class="flex items-center justify-between mb-4">
                  <h4 class="text-white font-bold">任务列表</h4>
                  <span class="text-white/60 text-sm">{{ adminTodos.filter(t => t.completed).length }}/{{ adminTodos.length }}</span>
                </div>
                <div class="space-y-3 max-h-[300px] overflow-y-auto">
                  <div v-if="adminTodos.length === 0" class="text-center py-8 text-white/30 text-sm">暂无待办事项</div>
                  <div v-for="todo in adminTodos" :key="todo.id" class="flex items-center gap-3 group">
                    <button @click="toggleTodo(todo.id)"
                      class="w-7 h-7 rounded-full flex items-center justify-center flex-shrink-0 transition-all"
                      :class="todo.completed ? 'bg-yellow-300' : 'bg-white/10 hover:bg-yellow-300/30'">
                      <span v-if="todo.completed" class="text-[#2D2D2D] text-xs font-bold">✓</span>
                    </button>
                    <div class="flex-1 min-w-0">
                      <div class="text-sm font-medium transition-all truncate"
                        :class="todo.completed ? 'text-white/30 line-through' : 'text-white/90'">
                        {{ todo.title }}
                      </div>
                    </div>
                    <button @click="removeTodo(todo.id)"
                      class="w-6 h-6 rounded-full bg-white/5 hover:bg-rose-500/30 flex items-center justify-center flex-shrink-0 opacity-0 group-hover:opacity-100 transition-all"
                      title="删除">
                      <span class="text-white/40 hover:text-rose-300 text-xs">✕</span>
                    </button>
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
