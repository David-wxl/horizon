/**
 * 评论相关 API
 */
import { get, post } from './request'

/**
 * 评论数据类型
 */
export interface Comment {
  id?: number
  cardId: number
  userId: number
  cardOwnerId?: number
  content: string
  createTime?: string
  username?: string
  nickname?: string
  avatar?: string
}

/**
 * 添加评论
 */
export function addComment(comment: Comment) {
  return post<Comment>('/comment/create', comment)
}

/**
 * 获取评论列表
 */
export function getComments(cardId: number) {
  return get<Comment[]>(`/comment/card/${cardId}`)
}

/**
 * 删除评论（管理员或评论作者）
 */
export function deleteComment(commentId: number, userId: number) {
  return post<boolean>(`/comment/delete/${commentId}?userId=${userId}`)
}
