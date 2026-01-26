/**
 * 管理员API接口
 */
import { get, post, del } from './request'

// 统计数据接口
export interface AdminStats {
  totalUsers: number
  todayNewUsers: number
  totalCards: number
  todayNewCards: number
  totalComments: number
  totalLikes: number
  apiCalls: number
}

// 系统监控接口
export interface SystemMonitor {
  cpuUsage: number
  memoryUsage: number
  totalMemory: number
  usedMemory: number
  totalStorage: number
  usedStorage: number
  storageUsage: number
}

// 仪表盘统计数据
export interface DashboardStats {
  totalUsers: number
  totalCards: number
  totalComments: number
  pendingAudit: number
}

// 审核队列项
export interface AuditItem {
  id: number
  title: string
  cardType: string
  authorName: string
  createTime: string
  status: number
}

// 周活动数据
export interface WeekActivity {
  date: string
  dayOfWeek: string
  count: number
}

// 获取统计数据
export const getAdminStats = () => {
  return get<AdminStats>('/admin/stats')
}

// 获取系统监控数据
export const getSystemMonitor = () => {
  return get<SystemMonitor>('/admin/monitor')
}

// 获取审核列表
export const getAuditList = () => {
  return get<any>('/admin/audit-list')
}

// 获取仪表盘统计数据
export const getDashboardStats = () => {
  return get<DashboardStats>('/admin/dashboard-stats')
}

// 获取审核队列（待审核）
export const getAuditQueue = () => {
  return get<AuditItem[]>('/admin/audit-queue')
}

// 获取已通过的卡片
export const getApprovedCards = () => {
  return get<AuditItem[]>('/admin/approved-cards')
}

// 获取周活动数据
export const getWeeklyActivity = () => {
  return get<WeekActivity[]>('/admin/weekly-activity')
}

// 审核卡片
export const auditCard = (cardId: number, status: number, reason?: string) => {
  const params: any = { status }
  if (reason) params.reason = reason
  return post<boolean>(`/admin/audit/${cardId}`, null, { params })
}

// 切换用户状态
export const toggleUserStatus = (userId: number) => {
  return post<boolean>(`/admin/user/toggle-status`, null, { params: { userId } })
}

// 删除卡片（管理员）
export const adminDeleteCard = (cardId: number) => {
  return del<boolean>(`/admin/card/delete/${cardId}`)
}

// 删除评论（管理员）
export const adminDeleteComment = (commentId: number) => {
  return del<boolean>(`/admin/comment/delete/${commentId}`)
}
