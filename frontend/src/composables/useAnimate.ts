/**
 * 轻量动画工具 —— 基于 CSS + IntersectionObserver，无需第三方库
 */

/** 将动画 class 应用到元素并在动画结束后清除 transform/opacity 限制 */
export function animateIn(
  el: Element | Element[] | NodeListOf<Element> | null,
  options: {
    delay?: number
    stagger?: number
    duration?: number
    from?: { opacity?: number; y?: number; scale?: number }
    ease?: string
  } = {}
) {
  if (!el) return
  const { delay = 0, stagger = 60, duration = 550, from = { opacity: 0, y: 24, scale: 1 } } = options

  const elements = el instanceof Element
    ? [el]
    : Array.from(el as NodeListOf<Element>)

  elements.forEach((item, i) => {
    const htmlItem = item as HTMLElement
    // 初始隐藏状态
    htmlItem.style.opacity = String(from.opacity ?? 0)
    htmlItem.style.transform = `translateY(${from.y ?? 0}px) scale(${from.scale ?? 1})`
    htmlItem.style.transition = `opacity ${duration}ms cubic-bezier(0.4,0,0.2,1) ${delay + i * stagger}ms, transform ${duration}ms cubic-bezier(0.4,0,0.2,1) ${delay + i * stagger}ms`

    requestAnimationFrame(() => {
      requestAnimationFrame(() => {
        htmlItem.style.opacity = '1'
        htmlItem.style.transform = 'translateY(0px) scale(1)'
      })
    })
  })
}

/** 使用 IntersectionObserver 监听卡片进入视口，批量触发动画 */
export function observeCards(container: HTMLElement | null, itemSelector = '.bento-card-item') {
  if (!container) return

  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          const el = entry.target as HTMLElement
          el.style.opacity = '1'
          el.style.transform = 'translateY(0px) scale(1)'
          observer.unobserve(el)
        }
      })
    },
    { threshold: 0.1, rootMargin: '0px 0px -40px 0px' }
  )

  const items = container.querySelectorAll(itemSelector)
  items.forEach((item, i) => {
    const el = item as HTMLElement
    el.style.opacity = '0'
    el.style.transform = 'translateY(32px) scale(0.97)'
    el.style.transition = `opacity 0.5s cubic-bezier(0.4,0,0.2,1) ${i * 55}ms, transform 0.5s cubic-bezier(0.4,0,0.2,1) ${i * 55}ms`
    observer.observe(el)
  })

  return observer
}
