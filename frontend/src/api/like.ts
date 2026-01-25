/**
 * 点赞相关 API
 */
import { post, request } from './request'

/**
 * 添加点赞
 */
export function addLike(cardId: number, userId: number, cardOwnerId: number) {
  return post<boolean>(`/like/card?userId=${userId}&cardId=${cardId}&cardOwnerId=${cardOwnerId}`)
}

/**
 * 取消点赞
 */
export function removeLike(cardId: number, userId: number) {
  return request<boolean>(`/like/card?userId=${userId}&cardId=${cardId}`, { method: 'DELETE' })
}

/**
 * 检查是否已点赞
 */
export function checkLiked(cardId: number, userId: number) {
  return request<boolean>(`/like/check?userId=${userId}&cardId=${cardId}`, { method: 'GET' })
}
