/**
 * Vue Router 配置
 */
import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import Login from '../views/Login.vue'
import Home from '../views/Home.vue'
import Square from '../views/Square.vue'
import CardDetail from '../views/CardDetail.vue'
import Profile from '../views/Profile.vue'
import UserHome from '../views/UserHome.vue'
import AdminDashboard from '../views/AdminDashboard.vue'
import Favorites from '../views/Favorites.vue'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/square'  // 首页重定向到广场
  },
  {
    path: '/square',
    name: 'Square',
    component: Square,
    meta: { requiresAuth: false }  // 广场支持访客模式
  },
  {
    path: '/my',
    name: 'Home',
    component: Home,
    meta: { requiresAuth: true }  // 个人主页需要登录
  },
  {
    path: '/profile',
    name: 'Profile',
    component: Profile,
    meta: { requiresAuth: true }  // 个人资料需要登录
  },
  {
    path: '/user/:userId',
    name: 'UserHome',
    component: UserHome,
    meta: { requiresAuth: false }  // 用户主页支持访客模式
  },
  {
    path: '/card/:id',
    name: 'CardDetail',
    component: CardDetail,
    meta: { requiresAuth: false }  // 卡片详情支持访客模式
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/favorites',
    name: 'Favorites',
    component: Favorites,
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    name: 'AdminDashboard',
    component: AdminDashboard,
    meta: { requiresAuth: true, requiresAdmin: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userStr = localStorage.getItem('user')
  const user = userStr ? JSON.parse(userStr) : null
  
  // 检查管理员权限
  if (to.meta.requiresAdmin) {
    if (!token) {
      // 未登录，跳转到登录页
      next('/login')
      return
    }
    if (!user || user.role !== 'ADMIN') {
      // 非管理员，跳转到首页
      alert('您没有访问权限')
      next('/square')
      return
    }
  }
  
  if (to.meta.requiresAuth && !token) {
    // 需要登录但未登录，跳转到登录页
    next('/login')
  } else if (to.path === '/login' && token) {
    // 已登录访问登录页，跳转到广场
    next('/square')
  } else {
    next()
  }
})

export default router
