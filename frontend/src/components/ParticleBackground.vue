<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { TresCanvas } from '@tresjs/core'
import * as THREE from 'three'

// 粒子配置
const particleCount = 600
const canvasRef = ref<HTMLCanvasElement | null>(null)
let scene: THREE.Scene
let camera: THREE.PerspectiveCamera
let renderer: THREE.WebGLRenderer
let particles: THREE.Points
let animationId: number

const mousePosition = ref({ x: 0, y: 0 })
const velocities: number[][] = []

// 鼠标移动监听
const handleMouseMove = (event: MouseEvent) => {
  mousePosition.value = {
    x: (event.clientX / window.innerWidth) * 2 - 1,
    y: -(event.clientY / window.innerHeight) * 2 + 1,
  }
}

// 初始化 Three.js 场景
const initScene = () => {
  const canvas = canvasRef.value
  if (!canvas) return

  // 场景
  scene = new THREE.Scene()
  
  // 相机
  camera = new THREE.PerspectiveCamera(
    75,
    window.innerWidth / window.innerHeight,
    0.1,
    1000
  )
  camera.position.z = 8
  
  // 渲染器
  renderer = new THREE.WebGLRenderer({
    canvas,
    alpha: true,
    antialias: true,
  })
  renderer.setSize(window.innerWidth, window.innerHeight)
  renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2))
  
  // 创建粒子
  const geometry = new THREE.BufferGeometry()
  const positions = new Float32Array(particleCount * 3)
  const colors = new Float32Array(particleCount * 3)
  
  for (let i = 0; i < particleCount; i++) {
    const i3 = i * 3
    
    // 随机位置
    positions[i3] = (Math.random() - 0.5) * 18
    positions[i3 + 1] = (Math.random() - 0.5) * 18
    positions[i3 + 2] = (Math.random() - 0.5) * 8
    
    // 彩虹色
    const hue = (i / particleCount) * 360
    const color = new THREE.Color(`hsl(${hue}, 100%, 65%)`)
    colors[i3] = color.r
    colors[i3 + 1] = color.g
    colors[i3 + 2] = color.b
    
    // 初始速度
    velocities.push([
      (Math.random() - 0.5) * 0.01,
      (Math.random() - 0.5) * 0.01,
      (Math.random() - 0.5) * 0.01,
    ])
  }
  
  geometry.setAttribute('position', new THREE.BufferAttribute(positions, 3))
  geometry.setAttribute('color', new THREE.BufferAttribute(colors, 3))
  
  const material = new THREE.PointsMaterial({
    size: 0.08,
    vertexColors: true,
    transparent: true,
    opacity: 0.8,
    sizeAttenuation: true,
    blending: THREE.AdditiveBlending,
  })
  
  particles = new THREE.Points(geometry, material)
  scene.add(particles)
  
  // 窗口大小调整
  window.addEventListener('resize', handleResize)
  
  // 开始动画
  animate()
}

// 动画循环
const animate = () => {
  const positions = particles.geometry.attributes.position.array as Float32Array
  
  for (let i = 0; i < particleCount; i++) {
    const i3 = i * 3
    
    // 当前位置
    const x = positions[i3]
    const y = positions[i3 + 1]
    const z = positions[i3 + 2]
    
    // 鼠标排斥力
    const mouseX = mousePosition.value.x * 9
    const mouseY = mousePosition.value.y * 9
    const dx = x - mouseX
    const dy = y - mouseY
    const distance = Math.sqrt(dx * dx + dy * dy)
    
    if (distance < 2.5) {
      const force = (2.5 - distance) * 0.08
      velocities[i][0] += (dx / distance) * force
      velocities[i][1] += (dy / distance) * force
    }
    
    // 应用速度
    positions[i3] += velocities[i][0]
    positions[i3 + 1] += velocities[i][1]
    positions[i3 + 2] += velocities[i][2]
    
    // 阻尼
    velocities[i][0] *= 0.97
    velocities[i][1] *= 0.97
    velocities[i][2] *= 0.97
    
    // 边界反弹
    if (Math.abs(positions[i3]) > 9) {
      velocities[i][0] *= -0.6
      positions[i3] = Math.sign(positions[i3]) * 9
    }
    if (Math.abs(positions[i3 + 1]) > 9) {
      velocities[i][1] *= -0.6
      positions[i3 + 1] = Math.sign(positions[i3 + 1]) * 9
    }
    if (Math.abs(positions[i3 + 2]) > 4) {
      velocities[i][2] *= -0.6
      positions[i3 + 2] = Math.sign(positions[i3 + 2]) * 4
    }
  }
  
  particles.geometry.attributes.position.needsUpdate = true
  
  // 渲染
  renderer.render(scene, camera)
  animationId = requestAnimationFrame(animate)
}

// 窗口调整
const handleResize = () => {
  camera.aspect = window.innerWidth / window.innerHeight
  camera.updateProjectionMatrix()
  renderer.setSize(window.innerWidth, window.innerHeight)
}

onMounted(() => {
  window.addEventListener('mousemove', handleMouseMove)
  initScene()
})

onUnmounted(() => {
  window.removeEventListener('mousemove', handleMouseMove)
  window.removeEventListener('resize', handleResize)
  if (animationId) {
    cancelAnimationFrame(animationId)
  }
  if (renderer) {
    renderer.dispose()
  }
})
</script>

<template>
  <div class="fixed inset-0 pointer-events-none z-0">
    <canvas ref="canvasRef" class="w-full h-full"></canvas>
  </div>
</template>

<style scoped>
canvas {
  display: block;
}
</style>
