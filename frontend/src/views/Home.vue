<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { getUserCards, createCard, updateCard, deleteCard, type BentoCard } from '../api/card'
import BentoCardComponent from '../components/BentoCard.vue'
import NotificationBell from '../components/NotificationBell.vue'
import { useRouter } from 'vue-router'

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

// 加载卡片
async function loadCards() {
  loading.value = true
  try {
    console.log('加载卡片，userId:', userId.value)
    const data = await getUserCards(userId.value)
    console.log('获取到的卡片数据：', data)
    cards.value = data || []
  } catch (error: any) {
    console.error('加载卡片失败:', error)
    alert('加载卡片失败: ' + error.message)
  } finally {
    loading.value = false
  }
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
    console.log('创建卡片，数据：', newCard.value)
    const result = await createCard(newCard.value as BentoCard)
    console.log('创建成功，返回：', result)
    
    await loadCards()  // 重新加载卡片列表
    console.log('重新加载后的卡片列表：', cards.value)
    
    showAddCardDialog.value = false
    alert('卡片创建成功！')
  } catch (error: any) {
    console.error('添加卡片失败:', error)
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

onMounted(() => {
  loadUserInfo()
  loadCards()
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
            <span class="text-sm text-stone-500">{{ user?.nickname || user?.username }}</span>
          </div>

          <!-- 操作按钮 -->
          <div class="flex items-center gap-4">
            <!-- 非编辑模式按钮 -->
            <template v-if="!isEditMode">
              <!-- 通知铃铛 -->
              <NotificationBell />
              
              <button
                v-if="user?.role === 'ADMIN'"
                @click="$router.push('/admin')"
                class="px-6 py-3 rounded-2xl bg-gradient-to-r from-purple-300 via-pink-200 to-rose-200 text-slate-900 font-semibold shadow-md hover:scale-105 transition-all duration-300"
              >
                🔐 管理后台
              </button>
              
              <button
                @click="$router.push('/square')"
                class="px-6 py-3 rounded-2xl bg-white/80 text-slate-700 border border-white/60 hover:bg-white transition-all duration-300"
              >
                🌍 社区广场
              </button>

              <button
                @click="openAddCardDialog"
                class="px-6 py-3 rounded-2xl bg-gradient-to-r from-amber-300 via-orange-200 to-stone-200 text-slate-900 font-semibold shadow-md hover:scale-105 transition-all duration-300"
              >
                + 添加卡片
              </button>

              <button
                @click="toggleEditMode"
                class="px-6 py-3 rounded-2xl bg-white/80 text-slate-700 border border-white/60 hover:bg-white transition-all duration-300"
              >
                ✎ 编辑
              </button>

              <button
                @click="$router.push('/profile')"
                class="px-6 py-3 rounded-2xl bg-white/80 text-slate-700 border border-white/60 hover:bg-white transition-all duration-300"
              >
                👤 个人资料
              </button>

              <button
                @click="handleLogout"
                class="px-6 py-3 rounded-2xl bg-white/80 text-slate-700 border border-white/60 hover:bg-white transition-all duration-300"
              >
                登出
              </button>
            </template>

            <!-- 编辑模式按钮 -->
            <template v-else>
              <button
                @click="openAddCardDialog"
                class="px-6 py-3 rounded-2xl bg-gradient-to-r from-amber-300 via-orange-200 to-stone-200 text-slate-900 font-semibold shadow-md hover:scale-105 transition-all duration-300"
              >
                + 添加
              </button>

              <button
                @click="toggleSelectAll"
                class="px-6 py-3 rounded-2xl bg-white/80 text-slate-700 border border-white/60 hover:bg-white transition-all duration-300"
              >
                {{ selectedCardIds.size === cards.length && cards.length > 0 ? '✓ 取消全选' : '☑️ 全选' }}
              </button>

              <button
                v-if="selectedCardIds.size > 0"
                @click="batchDeleteCards"
                class="px-6 py-3 rounded-2xl bg-gradient-to-r from-rose-300 via-red-200 to-pink-200 text-slate-900 font-semibold shadow-md hover:scale-105 transition-all duration-300"
              >
                🗑️ 删除 ({{ selectedCardIds.size }})
              </button>

              <button
                @click="toggleEditMode"
                class="px-6 py-3 rounded-2xl bg-gradient-to-r from-emerald-300 via-green-200 to-teal-200 text-slate-900 font-semibold shadow-md hover:scale-105 transition-all duration-300"
              >
                ✓ 完成
              </button>
            </template>
          </div>
        </div>
      </div>
    </nav>

    <!-- 主内容 -->
    <main class="max-w-7xl mx-auto px-8 py-12">
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

      <!-- Bento Grid 布局 -->
      <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        <div
          v-for="card in cards"
          :key="card.id"
          class="relative"
        >
          <!-- 编辑模式复选框 -->
          <div
            v-if="isEditMode"
            class="absolute top-4 left-4 z-10"
            @click.stop="toggleCardSelection(card.id!)"
          >
            <div
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
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800;900&display=swap');

* {
  font-family: 'Inter', sans-serif;
}
</style>
