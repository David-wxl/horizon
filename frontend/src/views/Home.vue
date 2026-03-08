<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { getUserCards, createCard, updateCard, deleteCard, type BentoCard } from '../api/card'
import BentoCardComponent from '../components/BentoCard.vue'
import NotificationBell from '../components/NotificationBell.vue'
import NavBar from '../components/NavBar.vue'
import { useRouter } from 'vue-router'
import { animateIn } from '../composables/useAnimate'

const router = useRouter()

// 用户信息
const user = ref<any>(null)
const userId = ref<number>(0)

// 统一的编辑模式（包含编辑和批量操作）
const isEditMode = ref(false)

// 卡片列表
const cards = ref<BentoCard[]>([])
const loading = ref(true)

// 批量选择
const selectedCardIds = ref<Set<number>>(new Set())

// 是否显示添加卡片对话框
const showAddCardDialog = ref(false)

// 新卡片数据
const newCard = ref<Partial<BentoCard>>({
  title: '',
  description: '',
  cardType: 'text',
  content: '',
  gridX: 0,
  gridY: 0,
  gridWidth: 1,
  gridHeight: 1,
  isPublic: 1,  // 默认公开到社区广场
  category: '',
  tags: ''
})

// 编辑卡片相关
const showEditCardDialog = ref(false)
const editingCard = ref<BentoCard | null>(null)

// 图片预览
const imagePreview = ref('')
const editImagePreview = ref('')

// 加载用户信息
function loadUserInfo() {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    user.value = JSON.parse(userStr)
    userId.value = user.value.id
  }
}

// 分类筛选
const activeCategory = ref<string>('all')
const categories = ref<string[]>([])

// 加载卡片
async function loadCards() {
  loading.value = true
  try {
    const data = await getUserCards(userId.value)
    cards.value = data || []
    extractCategories()
  } catch (error: any) {
    alert('加载卡片失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

function extractCategories() {
  const cats = new Set<string>()
  cards.value.forEach(c => { if (c.category) cats.add(c.category) })
  categories.value = Array.from(cats)
}

const filteredCards = computed(() => {
  if (activeCategory.value === 'all') return cards.value
  return cards.value.filter(c => c.category === activeCategory.value)
})

function setCategory(cat: string) {
  activeCategory.value = cat
}

// 拖拽排序
const dragIndex = ref<number | null>(null)
const dragOverIndex = ref<number | null>(null)

function onDragStart(index: number) {
  dragIndex.value = index
}

function onDragOver(e: DragEvent, index: number) {
  e.preventDefault()
  dragOverIndex.value = index
}

function onDragLeave() {
  dragOverIndex.value = null
}

function onDragEnd() {
  dragIndex.value = null
  dragOverIndex.value = null
}

async function onDrop(targetIndex: number) {
  dragOverIndex.value = null
  if (dragIndex.value === null || dragIndex.value === targetIndex) {
    dragIndex.value = null
    return
  }
  const arr = [...filteredCards.value]
  const [moved] = arr.splice(dragIndex.value, 1)
  arr.splice(targetIndex, 0, moved)
  cards.value = arr
  dragIndex.value = null
  for (let i = 0; i < arr.length; i++) {
    if (arr[i].sortOrder !== i) {
      arr[i].sortOrder = i
      try { await updateCard(arr[i]) } catch {}
    }
  }
}

// 调整卡片大小
const gridRef = ref<HTMLElement | null>(null)
const resizingCard = ref<BentoCard | null>(null)
const resizeDirection = ref<'right' | 'bottom' | 'corner'>('corner')
const resizeStartX = ref(0)
const resizeStartY = ref(0)
const resizeInitW = ref(1)
const resizeInitH = ref(1)
const resizePreviewW = ref(1)
const resizePreviewH = ref(1)
const isResizing = ref(false)

const BASE_ROW_HEIGHT = 180

function getGridCellSize() {
  if (!gridRef.value) return { cellW: 200, cellH: BASE_ROW_HEIGHT }
  const gridRect = gridRef.value.getBoundingClientRect()
  const style = getComputedStyle(gridRef.value)
  const gap = Number.parseFloat(style.gap) || 24
  const width = gridRect.width
  let cols = 1
  if (width >= 1280) cols = 4
  else if (width >= 1024) cols = 3
  else if (width >= 768) cols = 2
  const cellW = (width - (cols - 1) * gap) / cols
  return { cellW: cellW + gap, cellH: BASE_ROW_HEIGHT + gap }
}

function onResizeStart(event: MouseEvent, card: BentoCard, direction: 'right' | 'bottom' | 'corner') {
  resizingCard.value = card
  resizeDirection.value = direction
  resizeStartX.value = event.clientX
  resizeStartY.value = event.clientY
  resizeInitW.value = card.gridWidth || 1
  resizeInitH.value = card.gridHeight || 1
  resizePreviewW.value = resizeInitW.value
  resizePreviewH.value = resizeInitH.value
  isResizing.value = true
  document.addEventListener('mousemove', onResizeMove)
  document.addEventListener('mouseup', onResizeEnd)
}

function onResizeMove(e: MouseEvent) {
  if (!resizingCard.value) return
  const { cellW, cellH } = getGridCellSize()
  const deltaX = e.clientX - resizeStartX.value
  const deltaY = e.clientY - resizeStartY.value

  if (resizeDirection.value === 'right' || resizeDirection.value === 'corner') {
    const newW = Math.round(resizeInitW.value + deltaX / cellW)
    resizePreviewW.value = Math.max(1, Math.min(4, newW))
  }
  if (resizeDirection.value === 'bottom' || resizeDirection.value === 'corner') {
    const newH = Math.round(resizeInitH.value + deltaY / cellH)
    resizePreviewH.value = Math.max(1, Math.min(4, newH))
  }

  resizingCard.value.gridWidth = resizePreviewW.value
  resizingCard.value.gridHeight = resizePreviewH.value
}

async function onResizeEnd() {
  document.removeEventListener('mousemove', onResizeMove)
  document.removeEventListener('mouseup', onResizeEnd)
  if (resizingCard.value) {
    try {
      await updateCard(resizingCard.value)
    } catch {}
  }
  isResizing.value = false
  resizingCard.value = null
}

// 切换编辑模式
function toggleEditMode() {
  isEditMode.value = !isEditMode.value
  if (!isEditMode.value) {
    // 退出编辑模式时清空选择
    selectedCardIds.value.clear()
  }
}

// 打开添加卡片对话框
function openAddCardDialog() {
  showAddCardDialog.value = true
  imagePreview.value = ''
  // 重置表单
  newCard.value = {
    userId: userId.value,  // 添加用户ID
    title: '',
    description: '',
    cardType: 'text',
    content: '',
    gridX: 0,
    gridY: 0,
    gridWidth: 1,
    gridHeight: 1,
    isPublic: 1,  // 默认公开到社区广场
    status: 0,  // 明确设置状态为0（正常）
    category: '',
    tags: ''
  }
}

// 添加卡片
async function handleAddCard() {
  try {
    await createCard(newCard.value as BentoCard)
    await loadCards()
    showAddCardDialog.value = false
    alert('卡片创建成功！')
  } catch (error: any) {
    alert('添加卡片失败: ' + error.message)
  }
}

// 打开编辑对话框
function openEditCardDialog(card: BentoCard) {
  editingCard.value = { ...card }
  editImagePreview.value = card.cardType === 'image' ? card.content : ''
  showEditCardDialog.value = true
}

// 更新卡片
async function handleUpdateCard() {
  if (!editingCard.value) return
  
  try {
    await updateCard(editingCard.value)
    await loadCards()
    showEditCardDialog.value = false
    alert('卡片更新成功！')
  } catch (error: any) {
    alert('更新卡片失败: ' + error.message)
  }
}

// 删除卡片
async function handleDeleteCard(cardId: number) {
  if (!confirm('确定要删除这张卡片吗？')) return
  
  try {
    await deleteCard(cardId, userId.value)
    cards.value = cards.value.filter(c => c.id !== cardId)
  } catch (error: any) {
    alert('删除卡片失败: ' + error.message)
  }
}

// 处理图片上传（添加卡片）
function handleImageUpload(event: Event) {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  
  if (file) {
    const reader = new FileReader()
    reader.onload = (e) => {
      imagePreview.value = e.target?.result as string
      newCard.value.content = imagePreview.value
    }
    reader.readAsDataURL(file)
  }
}

// 处理图片上传（编辑卡片）
function handleEditImageUpload(event: Event) {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  
  if (file && editingCard.value) {
    const reader = new FileReader()
    reader.onload = (e) => {
      editImagePreview.value = e.target?.result as string
      editingCard.value!.content = editImagePreview.value
    }
    reader.readAsDataURL(file)
  }
}

// 登出
function handleLogout() {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  globalThis.location.href = '/login'
}

// 切换卡片选中状态
function toggleCardSelection(cardId: number) {
  if (selectedCardIds.value.has(cardId)) {
    selectedCardIds.value.delete(cardId)
  } else {
    selectedCardIds.value.add(cardId)
  }
}

// 全选/取消全选
function toggleSelectAll() {
  if (selectedCardIds.value.size === cards.value.length) {
    selectedCardIds.value.clear()
  } else {
    cards.value.forEach(card => {
      if (card.id) selectedCardIds.value.add(card.id)
    })
  }
}

// 批量删除
async function batchDeleteCards() {
  if (selectedCardIds.value.size === 0) {
    alert('请选择要删除的卡片')
    return
  }
  
  const count = selectedCardIds.value.size
  if (!confirm(`确定要删除选中的 ${count} 张卡片吗？`)) {
    return
  }
  
  try {
    const deletePromises = Array.from(selectedCardIds.value).map(cardId => 
      deleteCard(cardId, userId.value)
    )
    
    await Promise.all(deletePromises)
    
    alert(`成功删除 ${count} 张卡片！`)
    selectedCardIds.value.clear()
    await loadCards()
  } catch (error: any) {
    alert('批量删除失败: ' + error.message)
  }
}

// 监听 storage 变化（多标签页同步）
function handleStorageChange(e: StorageEvent) {
  if (e.key === 'token' || e.key === 'user') {
    const token = localStorage.getItem('token')
    if (!token) {
      // 如果 token 被清除，跳转到登录页
      router.push('/login')
    } else {
      // 重新加载用户信息和卡片
      loadUserInfo()
      loadCards()
    }
  }
}

function runEntryAnimation() {
  const header = document.querySelector('.home-header') as HTMLElement | null
  if (header) animateIn(header, { delay: 50, duration: 600, from: { opacity: 0, y: 24, scale: 1 } })
}

onMounted(() => {
  loadUserInfo()
  loadCards()
  window.addEventListener('storage', handleStorageChange)
  runEntryAnimation()
})

onUnmounted(() => {
  window.removeEventListener('storage', handleStorageChange)
})
</script>

<template>
  <div class="min-h-screen relative text-slate-800">
    <!-- 顶部导航栏 -->
    <NavBar variant="logo" :title="user?.nickname || user?.username || ''">
      <!-- 非编辑模式 -->
      <template v-if="!isEditMode">
        <NotificationBell />
        <button v-if="user?.role === 'ADMIN'" @click="$router.push('/admin')" class="btn-admin">🔐 管理后台</button>
        <button @click="$router.push('/square')" class="btn-secondary">🌍 社区广场</button>
        <button @click="$router.push('/favorites')" class="btn-secondary">⭐ 我的收藏</button>
        <button @click="openAddCardDialog" class="btn-primary">+ 添加卡片</button>
        <button @click="toggleEditMode" class="btn-secondary">✎ 编辑</button>
        <button @click="$router.push('/profile')" class="btn-secondary">👤 个人资料</button>
        <button @click="handleLogout" class="btn-secondary">登出</button>
      </template>
      <!-- 编辑模式 -->
      <template v-else>
        <button @click="openAddCardDialog" class="btn-primary">+ 添加</button>
        <button @click="toggleSelectAll" class="btn-secondary">
          {{ selectedCardIds.size === cards.length && cards.length > 0 ? '✓ 取消全选' : '☑️ 全选' }}
        </button>
        <button v-if="selectedCardIds.size > 0" @click="batchDeleteCards" class="btn-danger">
          🗑️ 删除 ({{ selectedCardIds.size }})
        </button>
        <button @click="toggleEditMode" class="btn-success">✓ 完成</button>
      </template>
    </NavBar>

    <!-- 主内容 -->
    <main class="max-w-7xl mx-auto px-8 py-12 home-header">
      <!-- 分类筛选标签 -->
      <div v-if="categories.length > 0 && !loading" class="mb-8 flex items-center gap-3 flex-wrap">
        <span class="text-sm text-stone-500">分类:</span>
        <button
          @click="setCategory('all')"
          class="px-5 py-2 rounded-2xl text-sm transition-all duration-300"
          :class="activeCategory === 'all' ? 'bg-gradient-to-r from-amber-300 via-orange-200 to-stone-200 text-slate-900 font-semibold shadow-md' : 'bg-white/80 text-slate-700 border border-white/60 hover:bg-white'"
        >全部</button>
        <button
          v-for="cat in categories"
          :key="cat"
          @click="setCategory(cat)"
          class="px-5 py-2 rounded-2xl text-sm transition-all duration-300"
          :class="activeCategory === cat ? 'bg-gradient-to-r from-amber-300 via-orange-200 to-stone-200 text-slate-900 font-semibold shadow-md' : 'bg-white/80 text-slate-700 border border-white/60 hover:bg-white'"
        >{{ cat }}</button>
      </div>

      <!-- 加载中 -->
      <div v-if="loading" class="text-center py-20">
        <div class="text-stone-500">加载中...</div>
      </div>

      <!-- 空状态 -->
      <div v-else-if="cards.length === 0" class="text-center py-20">
        <div class="glass-card p-16 max-w-2xl mx-auto">
          <h2 class="text-2xl font-semibold text-slate-900 mb-4">欢迎来到你的视界</h2>
          <p class="text-stone-600 mb-8">
            开始创建你的第一张 Bento 卡片吧！
          </p>
          <button
            @click="openAddCardDialog"
            class="px-8 py-4 rounded-3xl bg-gradient-to-r from-amber-300 via-orange-200 to-stone-200 text-slate-900 font-semibold hover:scale-105 transition-all duration-300 shadow-md hover:shadow-lg"
          >
            + 创建第一张卡片
          </button>
        </div>
      </div>

      <!-- 编辑模式提示 -->
      <div v-if="isEditMode && filteredCards.length > 1" class="mb-4 p-4 bg-amber-50/80 rounded-2xl text-sm text-amber-800 flex items-center gap-2">
        <span>💡</span> 拖拽卡片调整顺序 | 拖拽卡片右侧/底部/右下角边框调整大小 | 勾选可批量删除
      </div>

      <!-- Bento Grid 布局 -->
      <div v-if="!loading && filteredCards.length > 0" ref="gridRef" class="bento-grid grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6" :class="{ 'select-none': isResizing }">
        <div
          v-for="(card, index) in filteredCards"
          :key="card.id"
          class="relative transition-all duration-200 h-full"
          :class="[
            isEditMode ? 'drag-item' : '',
            isEditMode && dragIndex === index ? 'opacity-50 scale-95' : '',
            isEditMode && dragOverIndex === index && dragOverIndex !== dragIndex ? 'ring-2 ring-amber-300 ring-offset-2 rounded-3xl' : '',
            (card.gridWidth || 1) >= 2 ? `md:col-span-${Math.min(card.gridWidth || 1, 4)}` : '',
            (card.gridHeight || 1) >= 2 ? `row-span-${Math.min(card.gridHeight || 1, 4)}` : ''
          ]"
          :draggable="isEditMode"
          @dragstart="onDragStart(index)"
          @dragover="onDragOver($event, index)"
          @dragleave="onDragLeave"
          @drop="onDrop(index)"
          @dragend="onDragEnd"
        >
          <!-- 编辑模式工具栏 -->
          <div
            v-if="isEditMode"
            class="absolute top-4 left-4 z-10 flex items-center gap-2"
          >
            <!-- 拖拽手柄 -->
            <div class="w-8 h-8 rounded-lg bg-white/90 shadow flex items-center justify-center cursor-grab active:cursor-grabbing text-stone-400 hover:text-slate-700 transition-colors">
              <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 24 24">
                <circle cx="9" cy="5" r="1.5"/><circle cx="15" cy="5" r="1.5"/>
                <circle cx="9" cy="12" r="1.5"/><circle cx="15" cy="12" r="1.5"/>
                <circle cx="9" cy="19" r="1.5"/><circle cx="15" cy="19" r="1.5"/>
              </svg>
            </div>

            <!-- 复选框 -->
            <div
              @click.stop="toggleCardSelection(card.id!)"
              class="w-8 h-8 rounded-lg cursor-pointer transition-all duration-200"
              :class="selectedCardIds.has(card.id!) 
                ? 'bg-gradient-to-r from-amber-300 to-orange-200 shadow-lg scale-110' 
                : 'bg-white/80 border-2 border-stone-300 hover:border-amber-300'"
            >
              <svg
                v-if="selectedCardIds.has(card.id!)"
                class="w-full h-full p-1 text-slate-900"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M5 13l4 4L19 7"></path>
              </svg>
            </div>
          </div>

          <BentoCardComponent
            :card="card"
            :edit-mode="isEditMode"
            @edit="openEditCardDialog"
            @delete="handleDeleteCard"
            @resize-start="onResizeStart"
          />
        </div>
      </div>
    </main>

    <!-- 编辑卡片对话框 -->
    <div
      v-if="showEditCardDialog && editingCard"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/30 backdrop-blur-sm"
      @click.self="showEditCardDialog = false"
    >
      <div class="glass-card p-10 max-w-2xl w-full mx-8 max-h-[90vh] overflow-y-auto">
        <div class="flex items-center justify-between mb-8">
          <h2 class="text-2xl font-semibold text-slate-900">编辑卡片</h2>
          <button
            @click="showEditCardDialog = false"
            class="text-stone-500 hover:text-slate-900 transition-colors"
          >
            ✕
          </button>
        </div>

        <form @submit.prevent="handleUpdateCard" class="space-y-6">
          <!-- 标题 -->
          <div>
            <label for="edit-card-title" class="block text-stone-600 mb-2">标题</label>
            <input
              id="edit-card-title"
              v-model="editingCard.title"
              type="text"
              required
              class="w-full px-6 py-4 bg-white/80 border border-white/60 rounded-3xl text-slate-900 placeholder-stone-400 focus:outline-none focus:border-amber-300 focus:ring-2 focus:ring-amber-200/60 transition-all"
              placeholder="请输入卡片标题"
            />
          </div>

          <!-- 描述 -->
          <div>
            <label for="edit-card-description" class="block text-stone-600 mb-2">描述</label>
            <textarea
              id="edit-card-description"
              v-model="editingCard.description"
              rows="3"
              class="w-full px-6 py-4 bg-white/80 border border-white/60 rounded-3xl text-slate-900 placeholder-stone-400 focus:outline-none focus:border-amber-300 focus:ring-2 focus:ring-amber-200/60 transition-all resize-none"
              placeholder="请输入卡片描述（可选）"
            />
          </div>

          <!-- 图片上传（只在类型为 image 时显示） -->
          <div v-if="editingCard.cardType === 'image'">
            <label for="edit-card-image" class="block text-stone-600 mb-2">上传图片</label>
            <input
              id="edit-card-image"
              type="file"
              accept="image/*"
              @change="handleEditImageUpload"
              class="w-full px-6 py-4 bg-white/80 border border-white/60 rounded-3xl text-slate-900 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-amber-100 file:text-slate-900 hover:file:bg-amber-200 transition-all"
            />
            
            <!-- 图片预览 -->
            <div v-if="editImagePreview" class="mt-4 rounded-2xl overflow-hidden border border-white/60">
              <img :src="editImagePreview" alt="预览" class="w-full object-cover" />
            </div>
          </div>

          <!-- 内容（非图片类型） -->
          <div v-else>
            <label for="edit-card-content" class="block text-stone-600 mb-2">
              内容
              <span v-if="editingCard.cardType === 'code'" class="text-xs text-amber-600 ml-2">
                💡 支持代码语法
              </span>
              <span v-else-if="editingCard.cardType === 'text'" class="text-xs text-amber-600 ml-2">
                💡 支持 Markdown 语法
              </span>
              <span v-else-if="editingCard.cardType === 'link'" class="text-xs text-amber-600 ml-2">
                💡 输入完整 URL (如: https://example.com)
              </span>
            </label>
            <textarea
              id="edit-card-content"
              v-model="editingCard.content"
              :rows="editingCard.cardType === 'code' ? 10 : 5"
              required
              class="w-full px-6 py-4 bg-white/80 border border-white/60 rounded-3xl text-slate-900 placeholder-stone-400 focus:outline-none focus:border-amber-300 focus:ring-2 focus:ring-amber-200/60 transition-all resize-none"
              :class="editingCard.cardType === 'code' ? 'font-mono text-sm' : ''"
              :placeholder="editingCard.cardType === 'code' ? '请输入代码...' : 
                           editingCard.cardType === 'link' ? '请输入链接 URL...' : 
                           editingCard.cardType === 'text' ? '支持 **粗体** _斜体_ `代码` 等 Markdown 语法...' : 
                           '请输入内容...'"
            />
          </div>

          <!-- 类型 -->
          <div>
            <label for="edit-card-type" class="block text-stone-600 mb-2">卡片类型</label>
            <select
              id="edit-card-type"
              v-model="editingCard.cardType"
              class="w-full px-6 py-4 bg-white/80 border border-white/60 rounded-3xl text-slate-900 focus:outline-none focus:border-amber-300 focus:ring-2 focus:ring-amber-200/60 transition-all"
            >
              <option value="text">文本</option>
              <option value="image">图片</option>
              <option value="code">代码</option>
              <option value="media">书影音</option>
              <option value="link">链接</option>
              <option value="collection">合集</option>
            </select>
          </div>

          <!-- 卡片尺寸提示 -->
          <div class="text-sm text-stone-500 bg-amber-50 p-4 rounded-2xl">
            💡 所有卡片统一尺寸，自适应网格布局
          </div>

          <!-- 分类和标签 -->
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label for="edit-card-category" class="block text-stone-600 mb-2">分类</label>
              <input
                id="edit-card-category"
                v-model="editingCard.category"
                type="text"
                class="w-full px-6 py-4 bg-white/80 border border-white/60 rounded-3xl text-slate-900 placeholder-stone-400 focus:outline-none focus:border-amber-300 focus:ring-2 focus:ring-amber-200/60 transition-all"
                placeholder="如: ootd, code, reading"
              />
            </div>

            <div>
              <label for="edit-card-tags" class="block text-stone-600 mb-2">标签</label>
              <input
                id="edit-card-tags"
                v-model="editingCard.tags"
                type="text"
                class="w-full px-6 py-4 bg-white/80 border border-white/60 rounded-3xl text-slate-900 placeholder-stone-400 focus:outline-none focus:border-amber-300 focus:ring-2 focus:ring-amber-200/60 transition-all"
                placeholder="用逗号分隔"
              />
            </div>
          </div>

          <!-- 是否公开 -->
          <div class="flex items-center gap-3">
            <input
              v-model="editingCard.isPublic"
              type="checkbox"
              :true-value="1"
              :false-value="0"
              id="edit-is-public"
              class="w-5 h-5 rounded border-white/60"
            />
            <label for="edit-is-public" class="text-stone-600">公开到社区广场</label>
          </div>

          <!-- 提交按钮 -->
          <div class="flex gap-4 pt-4">
            <button
              type="button"
              @click="showEditCardDialog = false"
              class="flex-1 px-8 py-4 rounded-3xl bg-white/80 border border-white/60 text-slate-700 font-semibold hover:bg-white transition-all duration-300"
            >
              取消
            </button>
            <button
              type="submit"
              class="flex-1 px-8 py-4 rounded-3xl bg-gradient-to-r from-amber-300 via-orange-200 to-stone-200 text-slate-900 font-semibold hover:scale-105 transition-all duration-300 shadow-md hover:shadow-lg"
            >
              保存修改
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- 添加卡片对话框 -->
    <div
      v-if="showAddCardDialog"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/30 backdrop-blur-sm"
      @click.self="showAddCardDialog = false"
    >
      <div class="glass-card p-10 max-w-2xl w-full mx-8 max-h-[90vh] overflow-y-auto">
        <div class="flex items-center justify-between mb-8">
          <h2 class="text-2xl font-semibold text-slate-900">添加新卡片</h2>
          <button
            @click="showAddCardDialog = false"
            class="text-stone-500 hover:text-slate-900 transition-colors"
          >
            ✕
          </button>
        </div>

        <form @submit.prevent="handleAddCard" class="space-y-6">
          <!-- 标题 -->
          <div>
            <label for="card-title" class="block text-stone-600 mb-2">标题</label>
            <input
              id="card-title"
              v-model="newCard.title"
              type="text"
              required
              class="w-full px-6 py-4 bg-white/80 border border-white/60 rounded-3xl text-slate-900 placeholder-stone-400 focus:outline-none focus:border-amber-300 focus:ring-2 focus:ring-amber-200/60 transition-all"
              placeholder="请输入卡片标题"
            />
          </div>

          <!-- 描述 -->
          <div>
            <label for="card-description" class="block text-stone-600 mb-2">描述</label>
            <textarea
              id="card-description"
              v-model="newCard.description"
              rows="3"
              class="w-full px-6 py-4 bg-white/80 border border-white/60 rounded-3xl text-slate-900 placeholder-stone-400 focus:outline-none focus:border-amber-300 focus:ring-2 focus:ring-amber-200/60 transition-all resize-none"
              placeholder="请输入卡片描述（可选）"
            />
          </div>

          <!-- 图片上传（只在类型为 image 时显示） -->
          <div v-if="newCard.cardType === 'image'">
            <label for="card-image" class="block text-stone-600 mb-2">上传图片</label>
            <input
              id="card-image"
              type="file"
              accept="image/*"
              @change="handleImageUpload"
              class="w-full px-6 py-4 bg-white/80 border border-white/60 rounded-3xl text-slate-900 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-amber-100 file:text-slate-900 hover:file:bg-amber-200 transition-all"
            />
            
            <!-- 图片预览 -->
            <div v-if="imagePreview" class="mt-4 rounded-2xl overflow-hidden border border-white/60">
              <img :src="imagePreview" alt="预览" class="w-full object-cover" />
            </div>
          </div>

          <!-- 内容（非图片类型） -->
          <div v-else>
            <label for="card-content" class="block text-stone-600 mb-2">
              内容
              <span v-if="newCard.cardType === 'code'" class="text-xs text-amber-600 ml-2">
                💡 支持代码语法
              </span>
              <span v-else-if="newCard.cardType === 'text'" class="text-xs text-amber-600 ml-2">
                💡 支持 Markdown 语法
              </span>
              <span v-else-if="newCard.cardType === 'link'" class="text-xs text-amber-600 ml-2">
                💡 输入完整 URL (如: https://example.com)
              </span>
            </label>
            <textarea
              id="card-content"
              v-model="newCard.content"
              :rows="newCard.cardType === 'code' ? 10 : 5"
              required
              class="w-full px-6 py-4 bg-white/80 border border-white/60 rounded-3xl text-slate-900 placeholder-stone-400 focus:outline-none focus:border-amber-300 focus:ring-2 focus:ring-amber-200/60 transition-all resize-none"
              :class="newCard.cardType === 'code' ? 'font-mono text-sm' : ''"
              :placeholder="newCard.cardType === 'code' ? '请输入代码...' : 
                           newCard.cardType === 'link' ? '请输入链接 URL...' : 
                           newCard.cardType === 'text' ? '支持 **粗体** _斜体_ `代码` 等 Markdown 语法...' : 
                           '请输入内容...'"
            />
          </div>

          <!-- 类型 -->
          <div>
            <label for="card-type" class="block text-stone-600 mb-2">卡片类型</label>
            <select
              id="card-type"
              v-model="newCard.cardType"
              class="w-full px-6 py-4 bg-white/80 border border-white/60 rounded-3xl text-slate-900 focus:outline-none focus:border-amber-300 focus:ring-2 focus:ring-amber-200/60 transition-all"
            >
              <option value="text">文本</option>
              <option value="image">图片</option>
              <option value="code">代码</option>
              <option value="media">书影音</option>
              <option value="link">链接</option>
              <option value="collection">合集</option>
            </select>
          </div>

          <!-- 卡片尺寸提示 -->
          <div class="text-sm text-stone-500 bg-amber-50 p-4 rounded-2xl">
            💡 所有卡片统一尺寸，自适应网格布局
          </div>

          <!-- 分类和标签 -->
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label for="card-category" class="block text-stone-600 mb-2">分类</label>
              <input
                id="card-category"
                v-model="newCard.category"
                type="text"
                class="w-full px-6 py-4 bg-white/80 border border-white/60 rounded-3xl text-slate-900 placeholder-stone-400 focus:outline-none focus:border-amber-300 focus:ring-2 focus:ring-amber-200/60 transition-all"
                placeholder="如: ootd, code, reading"
              />
            </div>

            <div>
              <label for="card-tags" class="block text-stone-600 mb-2">标签</label>
              <input
                id="card-tags"
                v-model="newCard.tags"
                type="text"
                class="w-full px-6 py-4 bg-white/80 border border-white/60 rounded-3xl text-slate-900 placeholder-stone-400 focus:outline-none focus:border-amber-300 focus:ring-2 focus:ring-amber-200/60 transition-all"
                placeholder="用逗号分隔"
              />
            </div>
          </div>

          <!-- 是否公开 -->
          <div class="flex items-center gap-3">
            <input
              v-model="newCard.isPublic"
              type="checkbox"
              :true-value="1"
              :false-value="0"
              id="is-public"
              class="w-5 h-5 rounded border-white/60"
            />
            <label for="is-public" class="text-stone-600">公开到社区广场</label>
          </div>

          <!-- 提交按钮 -->
          <div class="flex gap-4 pt-4">
            <button
              type="button"
              @click="showAddCardDialog = false"
              class="flex-1 px-8 py-4 rounded-3xl bg-white/80 border border-white/60 text-slate-700 font-semibold hover:bg-white transition-all duration-300"
            >
              取消
            </button>
            <button
              type="submit"
              class="flex-1 px-8 py-4 rounded-3xl bg-gradient-to-r from-amber-300 via-orange-200 to-stone-200 text-slate-900 font-semibold hover:scale-105 transition-all duration-300 shadow-md hover:shadow-lg"
            >
              创建卡片
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>

.drag-item {
  cursor: grab;
}
.drag-item:active {
  cursor: grabbing;
}

/* Bento Grid 固定行高，使 row-span 生效 */
.bento-grid {
  grid-auto-rows: minmax(180px, auto);
}

/* Grid span（动态类名 Tailwind 无法编译，需手写） */
.md\:col-span-2 { @media (min-width: 768px) { grid-column: span 2 / span 2; } }
.md\:col-span-3 { @media (min-width: 768px) { grid-column: span 3 / span 3; } }
.md\:col-span-4 { @media (min-width: 768px) { grid-column: span 4 / span 4; } }
.row-span-2 { grid-row: span 2 / span 2; }
.row-span-3 { grid-row: span 3 / span 3; }
.row-span-4 { grid-row: span 4 / span 4; }

.select-none {
  user-select: none;
  -webkit-user-select: none;
}
</style>
