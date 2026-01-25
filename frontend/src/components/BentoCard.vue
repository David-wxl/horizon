<script setup lang="ts">
import { computed, ref, onMounted } from 'vue'
import type { BentoCard } from '../api/card'
import hljs from 'highlight.js'
import 'highlight.js/styles/github-dark.css'
import { marked } from 'marked'
import { addLike, removeLike } from '../api/like'

interface Props {
  card: BentoCard
  editMode: boolean
}

const props = defineProps<Props>()
const emit = defineEmits<{
  update: [card: BentoCard]
  delete: [cardId: number]
  edit: [card: BentoCard]
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

// Markdown 渲染
const markdownContent = computed(() => {
  if (props.card.cardType !== 'text' || !props.card.content) return ''
  
  try {
    return marked(props.card.content)
  } catch {
    return props.card.content
  }
})

// 代码高亮
const highlightedCode = computed(() => {
  if (props.card.cardType !== 'code') return ''
  
  try {
    return hljs.highlightAuto(props.card.content).value
  } catch {
    return props.card.content
  }
})

// 复制代码
const copySuccess = ref(false)

async function copyCode() {
  try {
    await navigator.clipboard.writeText(props.card.content)
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
  if (!userStr || !props.card.id) return
  
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

onMounted(() => {
  likeCount.value = props.card.likeCount || 0
})
</script>

<template>
  <div
    class="glass-card p-8 relative group transition-all duration-300 hover:shadow-lg cursor-pointer"
    :class="cardClasses"
    @click="!editMode && $router.push(`/card/${card.id}`)"
  >
    <!-- 编辑模式工具栏 -->
    <div
      v-if="editMode"
      class="absolute top-4 right-4 flex gap-2 opacity-0 group-hover:opacity-100 transition-opacity"
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
    <div class="mb-4">
      <!-- 代码类型 -->
      <div
        v-if="card.cardType === 'code'"
        class="p-4 bg-slate-900 rounded-2xl overflow-x-auto relative group"
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
          v-if="card.content && (card.content.startsWith('http') || card.content.startsWith('data:'))"
          :src="card.content"
          :alt="card.title"
          class="w-full h-48 object-cover hover:scale-105 transition-transform duration-300"
        />
        <div v-else class="p-8 text-center text-stone-500">
          <span class="text-4xl">🖼️</span>
          <p class="mt-2 text-sm">暂无图片</p>
        </div>
      </div>

      <!-- 链接类型 -->
      <div
        v-else-if="card.cardType === 'link'"
        class="p-4 bg-gradient-to-br from-blue-50 to-indigo-50 rounded-2xl"
      >
        <a 
          :href="card.content" 
          target="_blank"
          rel="noopener noreferrer"
          @click.stop
          class="block group"
        >
          <div class="flex items-center gap-3 mb-2">
            <span class="text-2xl">🔗</span>
            <span class="text-sm font-medium text-blue-600 group-hover:text-blue-700 transition-colors">
              点击访问链接
            </span>
          </div>
          <div class="text-sm text-stone-600 truncate">
            {{ card.content }}
          </div>
        </a>
      </div>

      <!-- 文本类型（支持 Markdown） -->
      <div
        v-else
        class="prose prose-sm max-w-none text-stone-700 leading-relaxed markdown-content"
        v-html="markdownContent"
      ></div>
    </div>

    <!-- 卡片底部元信息 -->
    <div class="flex items-center justify-between text-xs text-stone-500 mt-auto pt-4 border-t border-white/60">
      <div class="flex items-center gap-4">
        <!-- 分类 -->
        <span v-if="card.category" class="px-3 py-1 bg-white/70 rounded-full">
          {{ card.category }}
        </span>
        
        <!-- 标签 -->
        <span v-if="card.tags" class="px-3 py-1 bg-white/70 rounded-full">
          {{ card.tags }}
        </span>
      </div>

      <div class="flex items-center gap-3">
        <!-- 点赞按钮 -->
        <button
          v-if="!editMode"
          @click.stop="handleLike"
          class="flex items-center gap-1 px-3 py-1 rounded-full transition-all duration-300 hover:bg-rose-100"
          :class="[
            isLiked ? 'text-rose-600 bg-rose-50' : 'text-stone-500',
            likeAnimating ? 'scale-125' : 'scale-100'
          ]"
        >
          {{ isLiked ? '❤️' : '🤍' }} {{ likeCount }}
        </button>
        
        <!-- 查看数 -->
        <span v-if="card.viewCount" class="flex items-center gap-1">
          👁️ {{ card.viewCount }}
        </span>

        <!-- 公开状态 -->
        <span v-if="card.isPublic === 1" class="text-emerald-600">
          🌍 公开
        </span>
        <span v-else class="text-stone-400">
          🔒 私密
        </span>
      </div>
    </div>
  </div>
</template>

<style scoped>
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
