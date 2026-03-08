<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'

const props = defineProps<{
  focusField: 'none' | 'username' | 'password' | 'other'
  textLength: number
  status: 'idle' | 'success' | 'error'
}>()

const isBlinking = ref(false)
let blinkTimer: ReturnType<typeof setInterval>

const isPasswordMode = computed(() => props.focusField === 'password')
const isHappy = computed(() => props.status === 'success')
const isSad = computed(() => props.status === 'error')
const isWatching = computed(() => props.focusField === 'username' || props.focusField === 'other')

const eyeOffsetX = computed(() => {
  if (!isWatching.value) return 0
  const norm = Math.min(props.textLength / 20, 1)
  return -4 + norm * 8
})
const eyeOffsetY = computed(() => isWatching.value ? 2 : 0)

function doBlink() {
  if (isPasswordMode.value || isHappy.value) return
  isBlinking.value = true
  setTimeout(() => { isBlinking.value = false }, 150)
}

onMounted(() => {
  blinkTimer = setInterval(doBlink, 3000 + Math.random() * 2000)
})
onUnmounted(() => clearInterval(blinkTimer))
</script>

<template>
  <div class="characters-scene" :class="{ 'scene-happy': isHappy, 'scene-sad': isSad }">
    <svg viewBox="0 0 520 600" class="characters-svg" xmlns="http://www.w3.org/2000/svg">

      <!-- ═══════ 大橙色圆球（最大，底层） ═══════ -->
      <g class="char char-orange">
        <ellipse cx="140" cy="540" rx="190" ry="190" fill="#F4845F" />
        <!-- 眼睛 -->
        <g v-if="!isPasswordMode && !isBlinking && !isHappy">
          <circle :cx="105 + eyeOffsetX" :cy="460 + eyeOffsetY" r="8" fill="#8B3A2A" />
          <circle :cx="165 + eyeOffsetX" :cy="460 + eyeOffsetY" r="8" fill="#8B3A2A" />
          <circle :cx="102 + eyeOffsetX" :cy="457 + eyeOffsetY" r="2.5" fill="white" opacity="0.8" />
          <circle :cx="162 + eyeOffsetX" :cy="457 + eyeOffsetY" r="2.5" fill="white" opacity="0.8" />
        </g>
        <g v-else-if="isHappy">
          <path d="M92 462 Q105 452 118 462" stroke="#8B3A2A" stroke-width="3" fill="none" stroke-linecap="round" />
          <path d="M152 462 Q165 452 178 462" stroke="#8B3A2A" stroke-width="3" fill="none" stroke-linecap="round" />
        </g>
        <g v-else>
          <path d="M92 460 Q105 468 118 460" stroke="#8B3A2A" stroke-width="3" fill="none" stroke-linecap="round" />
          <path d="M152 460 Q165 468 178 460" stroke="#8B3A2A" stroke-width="3" fill="none" stroke-linecap="round" />
        </g>
      </g>

      <!-- ═══════ 紫色高个（左后） ═══════ -->
      <g class="char char-purple">
        <rect x="100" y="180" width="120" height="300" rx="60" fill="#7C5CFC" />
        <!-- 眼睛 -->
        <g v-if="!isPasswordMode && !isBlinking && !isHappy">
          <circle :cx="138 + eyeOffsetX * 0.8" :cy="320 + eyeOffsetY" r="7" fill="white" />
          <circle :cx="172 + eyeOffsetX * 0.8" :cy="320 + eyeOffsetY" r="7" fill="white" />
          <circle :cx="138 + eyeOffsetX * 0.8" :cy="320 + eyeOffsetY" r="3.5" fill="#2D1B69" />
          <circle :cx="172 + eyeOffsetX * 0.8" :cy="320 + eyeOffsetY" r="3.5" fill="#2D1B69" />
        </g>
        <g v-else-if="isHappy">
          <path d="M130 322 Q140 312 150 322" stroke="white" stroke-width="2.5" fill="none" stroke-linecap="round" />
          <path d="M164 322 Q174 312 184 322" stroke="white" stroke-width="2.5" fill="none" stroke-linecap="round" />
        </g>
        <g v-else>
          <path d="M130 320 Q140 328 150 320" stroke="white" stroke-width="2.5" fill="none" stroke-linecap="round" />
          <path d="M164 320 Q174 328 184 320" stroke="white" stroke-width="2.5" fill="none" stroke-linecap="round" />
        </g>
      </g>

      <!-- ═══════ 深色高个（右后） ═══════ -->
      <g class="char char-dark">
        <rect x="210" y="210" width="110" height="280" rx="55" fill="#2D2D2D" />
        <!-- 眼睛 -->
        <g v-if="!isPasswordMode && !isBlinking && !isHappy">
          <circle :cx="248 + eyeOffsetX * 0.8" :cy="340 + eyeOffsetY" r="6" fill="white" />
          <circle :cx="278 + eyeOffsetX * 0.8" :cy="340 + eyeOffsetY" r="6" fill="white" />
          <circle :cx="248 + eyeOffsetX * 0.8" :cy="340 + eyeOffsetY" r="3" fill="#111" />
          <circle :cx="278 + eyeOffsetX * 0.8" :cy="340 + eyeOffsetY" r="3" fill="#111" />
        </g>
        <g v-else-if="isHappy">
          <path d="M240 342 Q250 332 260 342" stroke="white" stroke-width="2.5" fill="none" stroke-linecap="round" />
          <path d="M270 342 Q280 332 290 342" stroke="white" stroke-width="2.5" fill="none" stroke-linecap="round" />
        </g>
        <g v-else>
          <path d="M240 340 Q250 348 260 340" stroke="white" stroke-width="2.5" fill="none" stroke-linecap="round" />
          <path d="M270 340 Q280 348 290 340" stroke="white" stroke-width="2.5" fill="none" stroke-linecap="round" />
        </g>
      </g>

      <!-- ═══════ 黄色小个（前右） ═══════ -->
      <g class="char char-yellow">
        <rect x="290" y="310" width="100" height="190" rx="50" fill="#E8D44D" />
        <!-- 眼睛 -->
        <g v-if="!isPasswordMode && !isBlinking && !isHappy">
          <circle :cx="325 + eyeOffsetX * 0.6" :cy="395 + eyeOffsetY" r="5" fill="#6B5A10" />
          <circle :cx="355 + eyeOffsetX * 0.6" :cy="395 + eyeOffsetY" r="5" fill="#6B5A10" />
        </g>
        <g v-else-if="isHappy">
          <path d="M318 397 Q327 387 336 397" stroke="#6B5A10" stroke-width="2.5" fill="none" stroke-linecap="round" />
          <path d="M348 397 Q357 387 366 397" stroke="#6B5A10" stroke-width="2.5" fill="none" stroke-linecap="round" />
        </g>
        <g v-else>
          <path d="M318 395 Q327 403 336 395" stroke="#6B5A10" stroke-width="2.5" fill="none" stroke-linecap="round" />
          <path d="M348 395 Q357 403 366 395" stroke="#6B5A10" stroke-width="2.5" fill="none" stroke-linecap="round" />
        </g>
        <!-- 黄色角色的嘴巴（一条横线） -->
        <path v-if="isHappy" d="M322 418 Q340 430 358 418" stroke="#6B5A10" stroke-width="2" fill="none" stroke-linecap="round" />
        <path v-else-if="isSad" d="M325 425 Q340 418 355 425" stroke="#6B5A10" stroke-width="2" fill="none" stroke-linecap="round" />
        <line v-else x1="322" y1="420" x2="358" y2="420" stroke="#6B5A10" stroke-width="2.5" stroke-linecap="round" />
      </g>

      <!-- ═══════ 密码模式：遮眼效果 ═══════ -->
      <g v-if="isPasswordMode" class="password-overlay">
        <!-- 橙色角色的"手" -->
        <ellipse cx="105" cy="445" rx="28" ry="16" fill="#D8724F" opacity="0.9" />
        <ellipse cx="165" cy="445" rx="28" ry="16" fill="#D8724F" opacity="0.9" />
        <!-- 紫色角色的"手" -->
        <ellipse cx="138" cy="308" rx="20" ry="12" fill="#6A4EE0" opacity="0.9" />
        <ellipse cx="172" cy="308" rx="20" ry="12" fill="#6A4EE0" opacity="0.9" />
        <!-- 深色角色的"手" -->
        <ellipse cx="248" cy="328" rx="18" ry="11" fill="#1A1A1A" opacity="0.9" />
        <ellipse cx="278" cy="328" rx="18" ry="11" fill="#1A1A1A" opacity="0.9" />
        <!-- 黄色角色的"手" -->
        <ellipse cx="325" cy="383" rx="16" ry="10" fill="#C4B230" opacity="0.9" />
        <ellipse cx="355" cy="383" rx="16" ry="10" fill="#C4B230" opacity="0.9" />
      </g>
    </svg>
  </div>
</template>

<style scoped>
.characters-scene {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  overflow: hidden;
}

.characters-svg {
  width: 100%;
  max-width: 520px;
  height: auto;
  filter: drop-shadow(0 20px 60px rgba(0, 0, 0, 0.3));
}

.char {
  transition: transform 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.char circle, .char path, .char line {
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.password-overlay ellipse {
  animation: hands-appear 0.35s cubic-bezier(0.34, 1.56, 0.64, 1) both;
}

@keyframes hands-appear {
  from {
    opacity: 0;
    transform: translateY(12px);
  }
  to {
    opacity: 0.9;
    transform: translateY(0);
  }
}

/* 开心弹跳 */
.scene-happy .char-orange { animation: bounce-1 0.6s ease 0s both; }
.scene-happy .char-purple { animation: bounce-1 0.6s ease 0.08s both; }
.scene-happy .char-dark   { animation: bounce-1 0.6s ease 0.16s both; }
.scene-happy .char-yellow { animation: bounce-1 0.6s ease 0.24s both; }

@keyframes bounce-1 {
  0%, 100% { transform: translateY(0); }
  30% { transform: translateY(-18px); }
  60% { transform: translateY(-6px); }
}

/* 难过摇晃 */
.scene-sad .char-orange { animation: shake-1 0.5s ease 0s both; }
.scene-sad .char-purple { animation: shake-1 0.5s ease 0.06s both; }
.scene-sad .char-dark   { animation: shake-1 0.5s ease 0.12s both; }
.scene-sad .char-yellow { animation: shake-1 0.5s ease 0.18s both; }

@keyframes shake-1 {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-6px); }
  50% { transform: translateX(6px); }
  75% { transform: translateX(-3px); }
}

/* 悬浮动画 */
.char-orange { animation: float-0 4s ease-in-out infinite; }
.char-purple { animation: float-1 4.5s ease-in-out infinite; }
.char-dark   { animation: float-2 5s ease-in-out infinite; }
.char-yellow { animation: float-3 3.8s ease-in-out infinite; }

@keyframes float-0 {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-6px); }
}
@keyframes float-1 {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}
@keyframes float-2 {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}
@keyframes float-3 {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-12px); }
}

/* 开心/难过时覆盖悬浮动画 */
.scene-happy .char-orange,
.scene-happy .char-purple,
.scene-happy .char-dark,
.scene-happy .char-yellow,
.scene-sad .char-orange,
.scene-sad .char-purple,
.scene-sad .char-dark,
.scene-sad .char-yellow {
  animation-duration: 0s;
}
</style>
