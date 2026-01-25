/**
 * 卡片相关 API
 */
import { get, post, request } from './request'

/**
 * 卡片数据类型
 */
export interface BentoCard {
  id?: number
  userId?: number
  title: string
  description?: string
  cardType: 'image' | 'code' | 'text' | 'media' | 'link' | 'collection'
  content: string
  gridX: number
  gridY: number
  gridWidth: number
  gridHeight: number
  bgColor?: string
  borderStyle?: string
  cardTheme?: string
  tags?: string
  category?: string
  isPublic: number
  status?: number
  likeCount?: number
  viewCount?: number
  commentCount?: number
  sortOrder?: number
  isPinned?: number
  createTime?: string
  updateTime?: string
}

/**
 * 创建卡片
 */
export function createCard(card: BentoCard) {
  return post<BentoCard>('/card/create', card)
}

/**
 * 更新卡片
 */
export function updateCard(card: BentoCard) {
  return request<boolean>('/card/update', { method: 'PUT', body: card })
}

/**
 * 删除卡片
 */
export function deleteCard(cardId: number, userId: number) {
  return request<boolean>(`/card/delete/${cardId}?userId=${userId}`, { method: 'DELETE' })
}

/**
 * 获取卡片详情
 */
export function getCardDetail(cardId: number) {
  return get<BentoCard>(`/card/detail/${cardId}`)
}

/**
 * 获取用户的所有卡片
 */
export function getUserCards(userId: number, publicOnly?: boolean) {
  const query = publicOnly === undefined ? '' : `?publicOnly=${publicOnly}`
  return get<BentoCard[]>(`/card/user/${userId}${query}`)
}

/**
 * 根据分类获取卡片
 */
export function getCardsByCategory(userId: number, category: string) {
  return get<BentoCard[]>(`/card/category/${userId}/${category}`)
}

/**
 * 获取广场卡片（分页）
 */
export interface SquareCardsResponse {
  records: BentoCard[]
  total: number
  size: number
  current: number
  pages: number
}

export function getSquareCards(pageNum: number = 1, pageSize: number = 20) {
  return get<SquareCardsResponse>(`/card/square?pageNum=${pageNum}&pageSize=${pageSize}`)
}

/**
 * 统计用户卡片数
 */
export function countUserCards(userId: number) {
  return get<number>(`/card/count/${userId}`)
}
