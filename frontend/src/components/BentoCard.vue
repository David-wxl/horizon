<script setup lang="ts">
import { computed, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import type { BentoCard } from '../api/card'
import hljs from 'highlight.js'
import 'highlight.js/styles/github-dark.css'
import { marked } from 'marked'
import { addLike, removeLike } from '../api/like'
import { addFavorite, removeFavorite, checkFavorite } from '../api/favorite'

const router = useRouter()

interface Props {
  card: BentoCard
  editMode: boolean
}

const props = defineProps<Props>()
const emit = defineEmits<{
  update: [card: BentoCard]
  delete: [cardId: number]
  edit: [card: BentoCard]
  resizeStart: [event: MouseEvent, card: BentoCard, direction: 'right' | 'bottom' | 'corner']
}>()

// 计算卡片样式类
const cardClasses = computed(() => {
  const classes: string[] = []
  
  // 根据宽度和高度设置 grid span
  if (props.card.gridWidth) {
    classes.push(`md:col-span-${Math.min(props.card.gridWidth, 4)}`)
  }
  
  // 高度类
  if (props.card.gridHeight === 2) {
    classes.push('row-span-2')
  } else if (props.card.gridHeight === 3) {
    classes.push('row-span-3')
  } else if (props.card.gridHeight === 4) {
    classes.push('row-span-4')
  }
  
  return classes.join(' ')
})

// 卡片类型图标
const cardTypeIcon = computed(() => {
  switch (props.card.cardType) {
    case 'image': return '🖼️'
    case 'code': return '💻'
    case 'text': return '📝'
    case 'media': return '🎬'
    case 'link': return '🔗'
    case 'collection': return '📚'
    default: return '📄'
  }
})

// 处理编辑
function handleEdit() {
  emit('edit', props.card)
}

// 处理删除
function handleDelete() {
  if (props.card.id) {
    emit('delete', props.card.id)
  }
}

// 格式化内容（简单处理）
const formattedContent = computed(() => {
  if (!props.card.content) return ''
  
  // 如果是代码类型，保留格式
  if (props.card.cardType === 'code') {
    return props.card.content
  }
  
  // 其他类型，限制显示长度
  const maxLength = 200
  if (props.card.content.length > maxLength) {
    return props.card.content.substring(0, maxLength) + '...'
  }
  
  return props.card.content
})

// 尝试从 JSON content 中提取字段
function parseContentJson(content: string): Record<string, any> | null {
  if (!content) return null
  try {
    const trimmed = content.trim()
    if (trimmed.startsWith('{')) {
      return JSON.parse(trimmed)
    }
  } catch {}
  return null
}

// Markdown 渲染
const markdownContent = computed(() => {
  if (props.card.cardType !== 'text' || !props.card.content) return ''
  try {
    const parsed = parseContentJson(props.card.content)
    const md = parsed?.markdown || parsed?.content || props.card.content
    return marked(md)
  } catch {
    return props.card.content
  }
})

// 提取代码文本和语言
const codeText = computed(() => {
  if (props.card.cardType !== 'code' || !props.card.content) return ''
  const parsed = parseContentJson(props.card.content)
  return parsed?.code || props.card.content
})

const codeLanguage = computed(() => {
  if (props.card.cardType !== 'code' || !props.card.content) return ''
  const parsed = parseContentJson(props.card.content)
  return parsed?.language || ''
})

// 代码高亮
const highlightedCode = computed(() => {
  if (!codeText.value) return ''
  try {
    if (codeLanguage.value && hljs.getLanguage(codeLanguage.value)) {
      return hljs.highlight(codeText.value, { language: codeLanguage.value }).value
    }
    return hljs.highlightAuto(codeText.value).value
  } catch {
    return codeText.value
  }
})

// 图片 URL 提取
const imageUrl = computed(() => {
  if (props.card.cardType !== 'image' || !props.card.content) return ''
  const parsed = parseContentJson(props.card.content)
  if (parsed?.urls && parsed.urls.length > 0) return parsed.urls[0]
  if (props.card.content.startsWith('http') || props.card.content.startsWith('data:')) return props.card.content
  return ''
})

const imageCaption = computed(() => {
  const parsed = parseContentJson(props.card.content)
  return parsed?.caption || ''
})

// 链接提取
const linkUrl = computed(() => {
  if (props.card.cardType !== 'link' || !props.card.content) return ''
  const parsed = parseContentJson(props.card.content)
  return parsed?.url || props.card.content
})

const linkTitle = computed(() => {
  const parsed = parseContentJson(props.card.content)
  return parsed?.title || parsed?.description || ''
})

// 书影音提取
const mediaInfo = computed(() => {
  if (props.card.cardType !== 'media' || !props.card.content) return null
  const parsed = parseContentJson(props.card.content)
  if (!parsed) return null
  return {
    type: parsed.type || 'book',
    title: parsed.title || '',
    author: parsed.author || parsed.artist || '',
    cover: parsed.cover || '',
    rating: parsed.rating || 0,
    comment: parsed.comment || ''
  }
})

// 复制代码
const copySuccess = ref(false)

async function copyCode() {
  try {
    await navigator.clipboard.writeText(codeText.value)
    copySuccess.value = true
    setTimeout(() => {
      copySuccess.value = false
    }, 2000)
  } catch (error) {
    console.error('复制失败:', error)
  }
}

// 点赞功能
const isLiked = ref(false)
const likeCount = ref(props.card.likeCount || 0)
const likeAnimating = ref(false)

async function handleLike() {
  const userStr = localStorage.getItem('user')
  if (!userStr) {
    if (confirm('请先登录后再点赞，是否前往登录？')) router.push('/login')
    return
  }
  if (!props.card.id) return
  
  const user = JSON.parse(userStr)
  const userId = user.id
  
  try {
    likeAnimating.value = true
    
    if (isLiked.value) {
      await removeLike(props.card.id, userId)
      isLiked.value = false
      likeCount.value = Math.max(0, likeCount.value - 1)
    } else {
      await addLike(props.card.id, userId, props.card.userId)
      isLiked.value = true
      likeCount.value += 1
    }
    
    setTimeout(() => {
      likeAnimating.value = false
    }, 300)
  } catch (error) {
    console.error('点赞操作失败:', error)
  }
}

// 收藏功能
const isFavorited = ref(false)
const favoriteAnimating = ref(false)

async function handleFavorite() {
  const userStr = localStorage.getItem('user')
  if (!userStr) {
    if (confirm('请先登录后再收藏，是否前往登录？')) router.push('/login')
    return
  }
  if (!props.card.id) return
  const user = JSON.parse(userStr)

  try {
    favoriteAnimating.value = true
    if (isFavorited.value) {
      await removeFavorite(user.id, props.card.id)
      isFavorited.value = false
    } else {
      await addFavorite(user.id, props.card.id)
      isFavorited.value = true
    }
    setTimeout(() => { favoriteAnimating.value = false }, 300)
  } catch {}
}

async function loadFavoriteStatus() {
  const userStr = localStorage.getItem('user')
  if (!userStr || !props.card.id) return
  const user = JSON.parse(userStr)
  try {
    isFavorited.value = await checkFavorite(user.id, props.card.id)
  } catch {}
}

onMounted(() => {
  likeCount.value = props.card.likeCount || 0
  loadFavoriteStatus()
})
</script>

<template>
  <div
    class="glass-card p-8 relative group transition-all duration-300 hover:shadow-lg cursor-pointer h-full flex flex-col"
    :class="cardClasses"
    @click="!editMode && $router.push(`/card/${card.id}`)"
  >
    <!-- 编辑模式工具栏 -->
    <div
      v-if="editMode"
      class="absolute top-4 right-4 flex gap-2 opacity-0 group-hover:opacity-100 transition-opacity z-10"
    >
      <button
        @click.stop="handleEdit"
        class="px-4 py-2 bg-white/90 text-slate-700 rounded-xl text-sm font-medium hover:bg-white transition-colors"
      >
        编辑
      </button>
      <button
        @click.stop="handleDelete"
        class="px-4 py-2 bg-rose-100/90 text-rose-700 rounded-xl text-sm font-medium hover:bg-rose-100 transition-colors"
      >
        删除
      </button>
    </div>

    <!-- 尺寸调整手柄 -->
    <template v-if="editMode">
      <!-- 右侧手柄 -->
      <div
        class="resize-handle resize-handle-right"
        @mousedown.stop.prevent="(e: MouseEvent) => emit('resizeStart', e, card, 'right')"
      >
        <div class="resize-bar-v"></div>
      </div>
      <!-- 底部手柄 -->
      <div
        class="resize-handle resize-handle-bottom"
        @mousedown.stop.prevent="(e: MouseEvent) => emit('resizeStart', e, card, 'bottom')"
      >
        <div class="resize-bar-h"></div>
      </div>
      <!-- 右下角手柄 -->
      <div
        class="resize-handle resize-handle-corner"
        @mousedown.stop.prevent="(e: MouseEvent) => emit('resizeStart', e, card, 'corner')"
      >
        <svg width="12" height="12" viewBox="0 0 12 12" fill="currentColor" class="text-stone-400">
          <circle cx="10" cy="2" r="1.5"/><circle cx="6" cy="6" r="1.5"/><circle cx="10" cy="6" r="1.5"/>
          <circle cx="2" cy="10" r="1.5"/><circle cx="6" cy="10" r="1.5"/><circle cx="10" cy="10" r="1.5"/>
        </svg>
      </div>
      <!-- 当前尺寸标签 -->
      <div class="absolute bottom-2 left-2 px-2 py-0.5 bg-slate-900/70 text-white text-xs rounded-lg opacity-0 group-hover:opacity-100 transition-opacity pointer-events-none">
        {{ card.gridWidth || 1 }} × {{ card.gridHeight || 1 }}
      </div>
    </template>

    <!-- 卡片头部 -->
    <div class="flex items-start justify-between mb-4">
      <div class="flex-1">
        <div class="flex items-center gap-2 mb-2">
          <span class="text-2xl">{{ cardTypeIcon }}</span>
          <span class="text-xs text-stone-500 uppercase tracking-wider">{{ card.cardType }}</span>
        </div>
        <h3 
          class="text-xl font-semibold text-slate-900 mb-2 hover:text-amber-600 transition-colors"
        >
          {{ card.title }}
        </h3>
        <p v-if="card.description" class="text-sm text-stone-600">
          {{ card.description }}
        </p>
      </div>
    </div>

    <!-- 卡片内容 -->
    <div class="mb-4 overflow-hidden flex-1 min-h-0">
      <!-- 代码类型 -->
      <div
        v-if="card.cardType === 'code'"
        class="p-4 bg-slate-900 rounded-2xl overflow-x-auto relative group max-h-48"
      >
        <pre class="text-sm font-mono"><code v-html="highlightedCode"></code></pre>
        
        <!-- 复制按钮 -->
        <button
          @click.stop="copyCode"
          class="absolute top-2 right-2 px-3 py-1 bg-white/10 hover:bg-white/20 rounded-lg text-xs text-white opacity-0 group-hover:opacity-100 transition-all duration-300"
        >
          {{ copySuccess ? '✓ 已复制' : '复制代码' }}
        </button>
      </div>

      <!-- 图片类型 -->
      <div
        v-else-if="card.cardType === 'image'"
        class="rounded-2xl overflow-hidden bg-stone-100"
      >
        <img
          v-if="imageUrl"
          :src="imageUrl"
          :alt="card.title"
          class="w-full h-48 object-cover hover:scale-105 transition-transform duration-300"
        />
        <div v-else class="p-8 text-center text-stone-500">
          <span class="text-4xl">🖼️</span>
          <p class="mt-2 text-sm">暂无图片</p>
        </div>
        <p v-if="imageCaption" class="text-xs text-stone-500 px-3 py-2">{{ imageCaption }}</p>
      </div>

      <!-- 书影音类型 -->
      <div
        v-else-if="card.cardType === 'media' && mediaInfo"
        class="p-4 bg-gradient-to-br from-amber-50 to-orange-50 rounded-2xl"
      >
        <div class="flex gap-3">
          <img v-if="mediaInfo.cover" :src="mediaInfo.cover" :alt="mediaInfo.title" class="w-16 h-20 object-cover rounded-lg flex-shrink-0" />
          <div class="flex-1 min-w-0">
            <div class="font-semibold text-slate-900 text-sm truncate">{{ mediaInfo.title }}</div>
            <div v-if="mediaInfo.author" class="text-xs text-stone-500 mt-0.5">{{ mediaInfo.author }}</div>
            <div v-if="mediaInfo.rating" class="text-xs mt-1">{{ '⭐'.repeat(mediaInfo.rating) }}</div>
            <p v-if="mediaInfo.comment" class="text-xs text-stone-600 mt-1 line-clamp-3">{{ mediaInfo.comment }}</p>
          </div>
        </div>
      </div>

      <!-- 链接类型 -->
      <div
        v-else-if="card.cardType === 'link'"
        class="p-4 bg-gradient-to-br from-blue-50 to-indigo-50 rounded-2xl"
      >
        <a
          :href="linkUrl"
          target="_blank"
          rel="noopener noreferrer"
          @click.stop
          class="block group"
        >
          <div class="flex items-center gap-3 mb-2">
            <span class="text-2xl">🔗</span>
            <span class="text-sm font-medium text-blue-600 group-hover:text-blue-700 transition-colors">
              {{ linkTitle || '点击访问链接' }}
            </span>
          </div>
          <div class="text-xs text-stone-500 truncate">{{ linkUrl }}</div>
        </a>
      </div>

      <!-- 文本类型（支持 Markdown） -->
      <div
        v-else
        class="prose prose-sm max-w-none text-stone-700 leading-relaxed markdown-content max-h-48 overflow-hidden"
        v-html="markdownContent"
      ></div>
    </div>

    <!-- 卡片底部元信息 -->
    <div class="flex items-center justify-between text-xs text-stone-500 mt-auto pt-4 border-t border-white/60">
      <div class="flex items-center gap-2">
        <span v-if="card.category" class="px-2 py-0.5 bg-white/70 rounded-full">
          {{ card.category }}
        </span>
        <span v-if="editMode" class="px-2 py-0.5 rounded-full" :class="card.isPublic === 1 ? 'text-emerald-600 bg-emerald-50' : 'text-stone-400 bg-stone-100'">
          {{ card.isPublic === 1 ? '公开' : '私密' }}
        </span>
      </div>

      <div class="flex items-center gap-2">
        <button
          v-if="!editMode"
          @click.stop="handleLike"
          class="flex items-center gap-1 px-2 py-0.5 rounded-full transition-all duration-300 hover:bg-rose-100"
          :class="[
            isLiked ? 'text-rose-600 bg-rose-50' : 'text-stone-500',
            likeAnimating ? 'scale-125' : 'scale-100'
          ]"
        >
          {{ isLiked ? '❤️' : '🤍' }} {{ likeCount }}
        </button>

        <button
          v-if="!editMode"
          @click.stop="handleFavorite"
          class="flex items-center gap-1 px-2 py-0.5 rounded-full transition-all duration-300 hover:bg-amber-100"
          :class="[
            isFavorited ? 'text-amber-600 bg-amber-50' : 'text-stone-500',
            favoriteAnimating ? 'scale-125' : 'scale-100'
          ]"
        >
          {{ isFavorited ? '⭐' : '☆' }}
        </button>

        <span v-if="card.viewCount" class="flex items-center gap-1">
          👁️ {{ card.viewCount }}
        </span>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 尺寸调整手柄 */
.resize-handle {
  position: absolute;
  z-index: 20;
  opacity: 0;
  transition: opacity 0.2s;
}
.glass-card:hover .resize-handle {
  opacity: 1;
}
.resize-handle-right {
  top: 20%;
  right: -4px;
  bottom: 20%;
  width: 12px;
  cursor: ew-resize;
  display: flex;
  align-items: center;
  justify-content: center;
}
.resize-handle-bottom {
  left: 20%;
  right: 20%;
  bottom: -4px;
  height: 12px;
  cursor: ns-resize;
  display: flex;
  align-items: center;
  justify-content: center;
}
.resize-handle-corner {
  right: -2px;
  bottom: -2px;
  width: 20px;
  height: 20px;
  cursor: nwse-resize;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  background: rgba(255,255,255,0.9);
  box-shadow: 0 1px 4px rgba(0,0,0,0.15);
}
.resize-bar-v {
  width: 4px;
  height: 32px;
  border-radius: 2px;
  background: rgba(251,191,36,0.7);
}
.resize-bar-h {
  height: 4px;
  width: 32px;
  border-radius: 2px;
  background: rgba(251,191,36,0.7);
}
.resize-handle:hover .resize-bar-v,
.resize-handle:hover .resize-bar-h {
  background: rgba(251,191,36,1);
}
.resize-handle-corner:hover {
  background: rgba(251,191,36,0.3);
}

/* 支持不同的列跨度 */
.md\:col-span-1 {
  @media (min-width: 768px) {
    grid-column: span 1 / span 1;
  }
}

.md\:col-span-2 {
  @media (min-width: 768px) {
    grid-column: span 2 / span 2;
  }
}

.md\:col-span-3 {
  @media (min-width: 768px) {
    grid-column: span 3 / span 3;
  }
}

.md\:col-span-4 {
  @media (min-width: 768px) {
    grid-column: span 4 / span 4;
  }
}

/* 行跨度 */
.row-span-2 {
  grid-row: span 2 / span 2;
}

.row-span-3 {
  grid-row: span 3 / span 3;
}

.row-span-4 {
  grid-row: span 4 / span 4;
}

/* Markdown 样式 */
.markdown-content :deep(h1) {
  font-size: 1.5rem;
  font-weight: 700;
  margin-top: 1rem;
  margin-bottom: 0.5rem;
  color: #1e293b;
}

.markdown-content :deep(h2) {
  font-size: 1.25rem;
  font-weight: 600;
  margin-top: 0.75rem;
  margin-bottom: 0.5rem;
  color: #1e293b;
}

.markdown-content :deep(h3) {
  font-size: 1.125rem;
  font-weight: 600;
  margin-top: 0.5rem;
  margin-bottom: 0.25rem;
  color: #334155;
}

.markdown-content :deep(p) {
  margin-bottom: 0.75rem;
}

.markdown-content :deep(ul),
.markdown-content :deep(ol) {
  margin-left: 1.5rem;
  margin-bottom: 0.75rem;
}

.markdown-content :deep(li) {
  margin-bottom: 0.25rem;
}

.markdown-content :deep(code) {
  background-color: #f1f5f9;
  padding: 0.125rem 0.375rem;
  border-radius: 0.25rem;
  font-size: 0.875rem;
  font-family: monospace;
  color: #dc2626;
}

.markdown-content :deep(pre) {
  background-color: #1e293b;
  padding: 1rem;
  border-radius: 0.5rem;
  overflow-x: auto;
  margin-bottom: 0.75rem;
}

.markdown-content :deep(pre code) {
  background-color: transparent;
  padding: 0;
  color: #10b981;
  font-size: 0.875rem;
}

.markdown-content :deep(blockquote) {
  border-left: 4px solid #f59e0b;
  padding-left: 1rem;
  margin-left: 0;
  margin-bottom: 0.75rem;
  color: #57534e;
  font-style: italic;
}

.markdown-content :deep(a) {
  color: #f59e0b;
  text-decoration: underline;
}

.markdown-content :deep(a:hover) {
  color: #d97706;
}

.markdown-content :deep(strong) {
  font-weight: 600;
  color: #1e293b;
}

.markdown-content :deep(em) {
  font-style: italic;
}

.markdown-content :deep(hr) {
  border: none;
  border-top: 1px solid #e7e5e4;
  margin: 1rem 0;
}
</style>
