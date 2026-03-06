<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCardDetail, deleteCard, type BentoCard } from '../api/card'
import { request } from '../api/request'
import { addComment, getComments, deleteComment, type Comment } from '../api/comment'
import { addLike, removeLike } from '../api/like'
import hljs from 'highlight.js'
import 'highlight.js/styles/github-dark.css'
import { marked } from 'marked'

const route = useRoute()
const router = useRouter()

// 卡片数据
const card = ref<BentoCard | null>(null)
const loading = ref(true)

// 评论数据
const comments = ref<Comment[]>([])
const newComment = ref('')
const submittingComment = ref(false)

// 用户信息
const user = ref<any>(null)

// 点赞状态
const isLiked = ref(false)
const likeCount = ref(0)
const likeAnimating = ref(false)

// 卡片作者
const cardAuthor = ref<any>(null)

async function loadAuthorInfo(userId: number) {
  try {
    cardAuthor.value = await request<any>(`/user/${userId}`, { method: 'GET' })
  } catch {}
}

// 加载卡片详情
async function loadCardDetail() {
  loading.value = true
  try {
    const cardId = Number(route.params.id)
    card.value = await getCardDetail(cardId)
    likeCount.value = card.value.likeCount || 0
    if (card.value.userId) {
      await loadAuthorInfo(card.value.userId)
    }
    await loadComments()
  } catch (error: any) {
    alert('加载卡片详情失败')
  } finally {
    loading.value = false
  }
}

// 加载评论
async function loadComments() {
  if (!card.value?.id) return
  
  try {
    comments.value = await getComments(card.value.id)
  } catch (error) {
    console.error('加载评论失败:', error)
  }
}

// 提交评论
async function submitComment() {
  // 检查是否登录
  if (!user.value) {
    if (confirm('请先登录后再评论，是否前往登录？')) {
      router.push('/login')
    }
    return
  }
  
  if (!newComment.value.trim() || !card.value?.id) return
  
  submittingComment.value = true
  try {
    await addComment({
      cardId: card.value.id,
      userId: user.value.id,
      cardOwnerId: card.value.userId,
      content: newComment.value
    })
    
    newComment.value = ''
    await loadComments()
  } catch (error: any) {
    const errorMsg = error?.message || error?.toString() || '评论失败，请重试'
    alert('评论失败: ' + errorMsg)
    console.error('评论失败详情:', error)
  } finally {
    submittingComment.value = false
  }
}

// 点赞处理
async function handleLike() {
  // 检查是否登录
  if (!user.value) {
    if (confirm('请先登录后再点赞，是否前往登录？')) {
      router.push('/login')
    }
    return
  }
  
  if (!card.value?.id) return
  
  try {
    likeAnimating.value = true
    
    if (isLiked.value) {
      await removeLike(card.value.id, user.value.id)
      isLiked.value = false
      likeCount.value = Math.max(0, likeCount.value - 1)
    } else {
      await addLike(card.value.id, user.value.id, card.value.userId)
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

// 返回
function goBack() {
  router.back()
}

// 判断是否可以删除（作者或管理员）
const canDelete = computed(() => {
  if (!user.value || !card.value) return false
  // 作者可以删除
  if (user.value.id === card.value.userId) return true
  // 管理员可以删除
  if (user.value.role === 'ADMIN') return true
  return false
})

// 删除卡片
async function handleDeleteCard() {
  if (!card.value || !user.value) return
  
  // 重新从 localStorage 获取最新用户信息
  const userStr = localStorage.getItem('user')
  if (userStr) {
    user.value = JSON.parse(userStr)
  }
  
  const isAdmin = user.value.role === 'ADMIN'
  const message = isAdmin 
    ? '⚠️ 管理员操作：确定要删除这张卡片吗？此操作不可恢复！' 
    : '确定要删除这张卡片吗？此操作不可恢复！'
  
  if (!confirm(message)) return
  
  try {
    await deleteCard(card.value.id!, user.value.id)
    alert('✓ 删除成功')
    // 返回上一页
    router.back()
  } catch (error: any) {
    console.error('删除失败:', error)
    alert('删除失败: ' + (error?.message || '未知错误'))
  }
}

// 判断是否可以删除评论（作者或管理员）
function canDeleteComment(comment: Comment): boolean {
  if (!user.value) return false
  // 作者可以删除
  if (user.value.id === comment.userId) return true
  // 管理员可以删除
  if (user.value.role === 'ADMIN') return true
  return false
}

// 删除评论
async function handleDeleteComment(comment: Comment) {
  if (!user.value) return
  
  const isAdmin = user.value.role === 'ADMIN'
  const message = isAdmin 
    ? '⚠️ 管理员操作：确定要删除这条评论吗？' 
    : '确定要删除这条评论吗？'
  
  if (!confirm(message)) return
  
  try {
    await deleteComment(comment.id!, user.value.id)
    // 重新加载评论列表
    await loadComments()
    alert('✓ 删除成功')
  } catch (error: any) {
    console.error('删除评论失败:', error)
    alert('删除失败: ' + (error?.message || '未知错误'))
  }
}

// JSON 内容解析
function parseContentJson(content: string): Record<string, any> | null {
  if (!content) return null
  try {
    const trimmed = content.trim()
    if (trimmed.startsWith('{')) return JSON.parse(trimmed)
  } catch {}
  return null
}

// 代码高亮
const codeText = computed(() => {
  if (!card.value || card.value.cardType !== 'code') return ''
  const parsed = parseContentJson(card.value.content)
  return parsed?.code || card.value.content
})

const codeLanguage = computed(() => {
  if (!card.value || card.value.cardType !== 'code') return ''
  const parsed = parseContentJson(card.value.content)
  return parsed?.language || ''
})

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

// Markdown 渲染
const markdownContent = computed(() => {
  if (!card.value || card.value.cardType !== 'text') return ''
  try {
    const parsed = parseContentJson(card.value.content)
    const md = parsed?.markdown || parsed?.content || card.value.content
    return marked(md)
  } catch {
    return card.value.content
  }
})

// 图片
const imageUrl = computed(() => {
  if (!card.value || card.value.cardType !== 'image') return ''
  const parsed = parseContentJson(card.value.content)
  if (parsed?.urls?.length > 0) return parsed.urls[0]
  if (card.value.content.startsWith('http') || card.value.content.startsWith('data:')) return card.value.content
  return ''
})

const imageCaption = computed(() => {
  if (!card.value) return ''
  const parsed = parseContentJson(card.value.content)
  return parsed?.caption || ''
})

// 链接
const linkUrl = computed(() => {
  if (!card.value || card.value.cardType !== 'link') return ''
  const parsed = parseContentJson(card.value.content)
  return parsed?.url || card.value.content
})

const linkInfo = computed(() => {
  if (!card.value) return { title: '', description: '' }
  const parsed = parseContentJson(card.value.content)
  return { title: parsed?.title || '', description: parsed?.description || '' }
})

// 书影音
const mediaInfo = computed(() => {
  if (!card.value || card.value.cardType !== 'media') return null
  const parsed = parseContentJson(card.value.content)
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

// 卡片类型图标
const cardTypeIcon = computed(() => {
  if (!card.value) return '📄'
  
  switch (card.value.cardType) {
    case 'image': return '🖼️'
    case 'code': return '💻'
    case 'text': return '📝'
    case 'media': return '🎬'
    case 'link': return '🔗'
    case 'collection': return '📚'
    default: return '📄'
  }
})

onMounted(() => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    user.value = JSON.parse(userStr)
  }
  loadCardDetail()
})
</script>

<template>
  <div class="min-h-screen relative overflow-hidden text-slate-800">
    <!-- 顶部导航栏 -->
    <nav class="sticky top-0 z-50 backdrop-blur-xl bg-white/60 border-b border-white/60">
      <div class="max-w-5xl mx-auto px-8 py-6">
        <div class="flex items-center justify-between">
          <button
            @click="goBack"
            class="flex items-center gap-2 text-slate-700 hover:text-slate-900 transition-colors"
          >
            ← 返回
          </button>
          
          <h1 class="text-xl font-semibold text-slate-900 tracking-tight">卡片详情</h1>
          
          <!-- 删除按钮（作者或管理员可见） -->
          <div>
            <button
              v-if="canDelete"
              @click="handleDeleteCard"
              class="flex items-center gap-2 px-4 py-2 rounded-xl bg-rose-500 hover:bg-rose-600 text-white font-semibold transition-all hover:scale-105"
              :title="user?.role === 'ADMIN' ? '管理员删除' : '删除卡片'"
            >
              <span v-if="user?.role === 'ADMIN'">🔐</span>
              🗑️ 删除
            </button>
          </div>
        </div>
      </div>
    </nav>

    <!-- 主内容 -->
    <main class="max-w-5xl mx-auto px-8 py-12">
      <!-- 加载中 -->
      <div v-if="loading" class="text-center py-20">
        <div class="text-stone-500">加载中...</div>
      </div>

      <!-- 卡片详情 -->
      <div v-else-if="card" class="space-y-8">
        <!-- 卡片内容 -->
        <div class="glass-card p-12">
          <!-- 卡片头部 -->
          <div class="mb-8">
            <div class="flex items-center gap-2 mb-4">
              <span class="text-3xl">{{ cardTypeIcon }}</span>
              <span class="text-sm text-stone-500 uppercase tracking-wider">{{ card.cardType }}</span>
            </div>
            <h2 class="text-3xl font-bold text-slate-900 mb-4">{{ card.title }}</h2>
            <p v-if="card.description" class="text-lg text-stone-600">
              {{ card.description }}
            </p>
          </div>

          <!-- 卡片内容 -->
          <div class="mb-8">
            <!-- 代码类型 -->
            <div v-if="card.cardType === 'code'" class="relative">
              <div v-if="codeLanguage" class="absolute top-3 left-4 text-xs text-white/40 uppercase tracking-wider">{{ codeLanguage }}</div>
              <div class="p-6 pt-10 bg-slate-900 rounded-2xl overflow-x-auto">
                <pre class="text-sm font-mono leading-relaxed"><code v-html="highlightedCode"></code></pre>
              </div>
            </div>

            <!-- 图片类型 -->
            <div v-else-if="card.cardType === 'image'" class="rounded-2xl overflow-hidden bg-stone-100">
              <img v-if="imageUrl" :src="imageUrl" :alt="card.title" class="w-full object-cover" />
              <div v-else class="p-16 text-center text-stone-500">
                <span class="text-6xl">🖼️</span>
                <p class="mt-4 text-lg">图片预览</p>
              </div>
              <p v-if="imageCaption" class="text-sm text-stone-500 px-4 py-3">{{ imageCaption }}</p>
            </div>

            <!-- 书影音类型 -->
            <div v-else-if="card.cardType === 'media' && mediaInfo" class="p-8 bg-gradient-to-br from-amber-50 to-orange-50 rounded-2xl">
              <div class="flex gap-6">
                <img v-if="mediaInfo.cover" :src="mediaInfo.cover" :alt="mediaInfo.title" class="w-32 h-44 object-cover rounded-xl shadow-lg flex-shrink-0" />
                <div class="flex-1">
                  <div class="text-xs text-stone-500 uppercase tracking-wider mb-1">{{ mediaInfo.type === 'book' ? '书籍' : mediaInfo.type === 'music' ? '音乐' : '影视' }}</div>
                  <h3 class="text-2xl font-bold text-slate-900 mb-1">{{ mediaInfo.title }}</h3>
                  <div v-if="mediaInfo.author" class="text-sm text-stone-600 mb-3">{{ mediaInfo.author }}</div>
                  <div v-if="mediaInfo.rating" class="text-lg mb-3">{{ '⭐'.repeat(mediaInfo.rating) }}</div>
                  <p v-if="mediaInfo.comment" class="text-stone-700 leading-relaxed">{{ mediaInfo.comment }}</p>
                </div>
              </div>
            </div>

            <!-- 链接类型 -->
            <div v-else-if="card.cardType === 'link'" class="p-8 bg-gradient-to-br from-blue-50 to-indigo-50 rounded-2xl">
              <a :href="linkUrl" target="_blank" rel="noopener noreferrer" class="block group">
                <div class="flex items-center gap-4 mb-3">
                  <span class="text-4xl">🔗</span>
                  <span class="text-lg font-semibold text-blue-600 group-hover:text-blue-700 transition-colors">
                    {{ linkInfo.title || '点击访问链接' }}
                  </span>
                </div>
                <p v-if="linkInfo.description" class="text-stone-600 mb-2">{{ linkInfo.description }}</p>
                <div class="text-sm text-blue-500 break-all">{{ linkUrl }}</div>
              </a>
            </div>

            <!-- 文本类型（Markdown） -->
            <div
              v-else
              class="prose prose-lg max-w-none text-stone-700 leading-relaxed markdown-content"
              v-html="markdownContent"
            ></div>
          </div>

          <!-- 作者信息 -->
          <div v-if="cardAuthor" class="flex items-center gap-4 pt-6 mb-6 border-t border-white/60">
            <div 
              class="cursor-pointer"
              @click="router.push(`/user/${card.userId}`)"
            >
              <div 
                v-if="cardAuthor.avatar"
                class="w-12 h-12 rounded-full overflow-hidden ring-2 ring-white/60 hover:ring-amber-300 transition-all"
              >
                <img :src="cardAuthor.avatar" :alt="cardAuthor.nickname" class="w-full h-full object-cover" />
              </div>
              <div 
                v-else
                class="w-12 h-12 rounded-full bg-gradient-to-br from-amber-300 to-orange-200 flex items-center justify-center text-slate-900 font-semibold text-lg ring-2 ring-white/60 hover:ring-amber-300 transition-all"
              >
                {{ (cardAuthor.nickname || cardAuthor.username || '?').charAt(0).toUpperCase() }}
              </div>
            </div>
            <div class="flex-1">
              <span 
                class="font-semibold text-slate-900 hover:text-amber-600 cursor-pointer transition-colors"
                @click="router.push(`/user/${card.userId}`)"
              >
                {{ cardAuthor.nickname || cardAuthor.username }}
              </span>
              <p class="text-xs text-stone-500">@{{ cardAuthor.username }}</p>
            </div>
            <button
              v-if="user && card.userId !== user.id"
              @click="router.push(`/user/${card.userId}`)"
              class="px-6 py-2 rounded-2xl bg-gradient-to-r from-amber-300 via-orange-200 to-stone-200 text-slate-900 text-sm font-semibold hover:scale-105 transition-all duration-300 shadow-md"
            >
              查看主页
            </button>
          </div>

          <!-- 卡片元信息 -->
          <div class="flex items-center justify-between pt-6 border-t border-white/60">
            <div class="flex items-center gap-4 text-sm text-stone-500">
              <!-- 分类 -->
              <span v-if="card.category" class="px-4 py-2 bg-white/70 rounded-full">
                {{ card.category }}
              </span>
              
              <!-- 标签 -->
              <span v-if="card.tags" class="px-4 py-2 bg-white/70 rounded-full">
                {{ card.tags }}
              </span>
            </div>

            <div class="flex items-center gap-4">
              <!-- 点赞按钮 -->
              <button
                @click="handleLike"
                class="flex items-center gap-2 px-6 py-3 rounded-2xl transition-all duration-300"
                :class="[
                  isLiked ? 'text-rose-600 bg-rose-50 hover:bg-rose-100' : 'text-stone-500 bg-white/70 hover:bg-white',
                  likeAnimating ? 'scale-125' : 'scale-100'
                ]"
              >
                {{ isLiked ? '❤️' : '🤍' }}
                <span class="font-semibold">{{ likeCount }}</span>
              </button>
              
              <!-- 查看数 -->
              <span v-if="card.viewCount" class="flex items-center gap-2 px-4 py-2 bg-white/70 rounded-full text-stone-500">
                👁️ {{ card.viewCount }}
              </span>
            </div>
          </div>
        </div>

        <!-- 评论区 -->
        <div class="glass-card p-12">
          <h3 class="text-2xl font-semibold text-slate-900 mb-8">
            评论 ({{ comments.length }})
          </h3>

          <!-- 评论输入框 -->
          <div class="mb-8">
            <textarea
              v-model="newComment"
              rows="4"
              placeholder="写下你的想法..."
              class="w-full px-6 py-4 bg-white/80 border border-white/60 rounded-3xl text-slate-900 placeholder-stone-400 focus:outline-none focus:border-amber-300 focus:ring-2 focus:ring-amber-200/60 transition-all resize-none"
            ></textarea>
            <div class="flex justify-end mt-4">
              <button
                @click="submitComment"
                :disabled="!newComment.trim() || submittingComment"
                class="px-8 py-3 rounded-2xl bg-gradient-to-r from-amber-300 via-orange-200 to-stone-200 text-slate-900 font-semibold hover:scale-105 transition-all duration-300 shadow-md hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {{ submittingComment ? '发送中...' : '发表评论' }}
              </button>
            </div>
          </div>

          <!-- 评论列表 -->
          <div v-if="comments.length > 0" class="space-y-6">
            <div
              v-for="comment in comments"
              :key="comment.id"
              class="p-6 bg-white/60 rounded-2xl border border-white/60"
            >
              <div class="flex items-start gap-4 mb-3">
                <!-- 头像 -->
                <div 
                  class="flex-shrink-0 cursor-pointer"
                  @click="router.push(`/user/${comment.userId}`)"
                >
                  <div 
                    v-if="comment.avatar"
                    class="w-12 h-12 rounded-full overflow-hidden ring-2 ring-white/60 hover:ring-amber-300 transition-all"
                  >
                    <img :src="comment.avatar" :alt="comment.nickname || comment.username" class="w-full h-full object-cover" />
                  </div>
                  <div 
                    v-else
                    class="w-12 h-12 rounded-full bg-gradient-to-br from-amber-300 to-orange-200 flex items-center justify-center text-slate-900 font-semibold text-lg ring-2 ring-white/60 hover:ring-amber-300 transition-all"
                  >
                    {{ (comment.nickname || comment.username || '?').charAt(0).toUpperCase() }}
                  </div>
                </div>

                <!-- 评论内容 -->
                <div class="flex-1">
                  <div class="flex items-center gap-2 mb-2">
                    <span 
                      class="font-semibold text-slate-900 hover:text-amber-600 cursor-pointer transition-colors"
                      @click="router.push(`/user/${comment.userId}`)"
                    >
                      {{ comment.nickname || comment.username }}
                    </span>
                    <span class="text-xs text-stone-500">
                      {{ comment.createTime }}
                    </span>
                  </div>
                  <p class="text-stone-700 leading-relaxed">{{ comment.content }}</p>
                </div>

                <!-- 删除按钮（作者或管理员可见） -->
                <button
                  v-if="canDeleteComment(comment)"
                  @click="handleDeleteComment(comment)"
                  class="flex-shrink-0 px-3 py-1 rounded-lg text-sm text-rose-600 hover:bg-rose-50 transition-all"
                  :title="user?.role === 'ADMIN' ? '管理员删除' : '删除评论'"
                >
                  {{ user?.role === 'ADMIN' ? '🔐 删除' : '删除' }}
                </button>
              </div>
            </div>
          </div>

          <!-- 空状态 -->
          <div v-else class="text-center py-12 text-stone-500">
            还没有评论，来发表第一条评论吧！
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

/* Markdown 样式 */
.markdown-content :deep(h1) {
  font-size: 2rem;
  font-weight: 700;
  margin-top: 1.5rem;
  margin-bottom: 0.75rem;
  color: #1e293b;
}

.markdown-content :deep(h2) {
  font-size: 1.5rem;
  font-weight: 600;
  margin-top: 1.25rem;
  margin-bottom: 0.75rem;
  color: #1e293b;
}

.markdown-content :deep(h3) {
  font-size: 1.25rem;
  font-weight: 600;
  margin-top: 1rem;
  margin-bottom: 0.5rem;
  color: #334155;
}

.markdown-content :deep(p) {
  margin-bottom: 1rem;
}

.markdown-content :deep(ul),
.markdown-content :deep(ol) {
  margin-left: 2rem;
  margin-bottom: 1rem;
}

.markdown-content :deep(li) {
  margin-bottom: 0.5rem;
}

.markdown-content :deep(code) {
  background-color: #f1f5f9;
  padding: 0.25rem 0.5rem;
  border-radius: 0.375rem;
  font-size: 0.875rem;
  font-family: monospace;
  color: #dc2626;
}

.markdown-content :deep(pre) {
  background-color: #1e293b;
  padding: 1.5rem;
  border-radius: 0.75rem;
  overflow-x: auto;
  margin-bottom: 1rem;
}

.markdown-content :deep(pre code) {
  background-color: transparent;
  padding: 0;
  color: #10b981;
}

.markdown-content :deep(blockquote) {
  border-left: 4px solid #f59e0b;
  padding-left: 1.5rem;
  margin-left: 0;
  margin-bottom: 1rem;
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
  margin: 1.5rem 0;
}
</style>
