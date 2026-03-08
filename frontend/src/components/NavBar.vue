<script setup lang="ts">
import { useRouter } from 'vue-router'

const props = withDefaults(defineProps<{
  variant?: 'logo' | 'back'
  title?: string
  maxWidth?: string
}>(), {
  variant: 'logo',
  title: '',
  maxWidth: 'max-w-7xl'
})

const router = useRouter()

function goBack() {
  router.back()
}
</script>

<template>
  <nav class="navbar-root sticky top-0 z-50 backdrop-blur-xl bg-white/70 border-b border-white/60 shadow-sm">
    <div :class="[maxWidth, 'mx-auto px-8 py-5']">
      <div class="flex items-center justify-between">
        <!-- 左侧：Logo 或 返回按钮 -->
        <div class="flex items-center gap-4 min-w-0">
          <template v-if="variant === 'logo'">
            <slot name="logo">
              <div class="flex items-center gap-3">
                <h1 class="text-2xl font-bold text-slate-900 tracking-tight select-none">HORIZON</h1>
                <span v-if="title" class="text-sm text-stone-500 font-medium">{{ title }}</span>
              </div>
            </slot>
          </template>
          <template v-else>
            <button
              @click="goBack"
              class="nav-back-btn flex items-center gap-2 px-4 py-2 rounded-xl text-slate-600 hover:text-slate-900 hover:bg-white/80 transition-all duration-200 font-medium text-sm"
            >
              <svg width="16" height="16" viewBox="0 0 16 16" fill="none" class="flex-shrink-0">
                <path d="M10 12L6 8L10 4" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              返回
            </button>
            <h1 v-if="title" class="text-lg font-semibold text-slate-900 tracking-tight truncate">{{ title }}</h1>
          </template>
        </div>

        <!-- 右侧：自定义内容插槽 -->
        <div class="flex items-center gap-3 flex-shrink-0">
          <slot />
        </div>
      </div>
    </div>
  </nav>
</template>
