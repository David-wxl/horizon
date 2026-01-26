/**
 * 通知API
 */
import { get, put, del } from './request'

/**
 * 通知接口
 */
export interface Notification {
  id: number
  userId: number
  type: 'audit_pass' | 'audit_reject' | 'comment' | 'like' | 'system'
  title: string
  content: string
  cardId?: number
  cardTitle?: string
  isRead: number  // 0-未读，1-已读
  createTime: string
  readTime?: string
}

/**
 * 获取用户通知列表
 */
export const getNotifications = (userId: number, limit?: number) => {
  const params: any = { userId }
  if (limit) params.limit = limit
  return get<Notification[]>('/notification/list', params)
}

/**
 * 获取未读通知数量
 */
export const getUnreadCount = (userId: number) => {
  return get<{ count: number }>('/notification/unread-count', { userId })
}

/**
 * 标记单个通知为已读
 */
export const markNotificationAsRead = (notificationId: number, userId: number) => {
  return put<boolean>(`/notification/read/${notificationId}?userId=${userId}`)
}

/**
 * 标记所有通知为已读
 */
export const markAllNotificationsAsRead = (userId: number) => {
  return put<boolean>(`/notification/read-all?userId=${userId}`)
}

/**
 * 删除通知
 */
export const deleteNotification = (notificationId: number, userId: number) => {
  return del<boolean>(`/notification/delete/${notificationId}?userId=${userId}`)
}
