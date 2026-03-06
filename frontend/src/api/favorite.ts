import { get, post, request } from './request'
import type { BentoCard } from './card'

export function addFavorite(userId: number, cardId: number) {
  return post<boolean>(`/favorite?userId=${userId}&cardId=${cardId}`)
}

export function removeFavorite(userId: number, cardId: number) {
  return request<boolean>(`/favorite?userId=${userId}&cardId=${cardId}`, { method: 'DELETE' })
}

export function checkFavorite(userId: number, cardId: number) {
  return get<boolean>(`/favorite/check?userId=${userId}&cardId=${cardId}`)
}

export function getUserFavorites(userId: number) {
  return get<BentoCard[]>(`/favorite/list/${userId}`)
}

export function getFavoriteCount(cardId: number) {
  return get<number>(`/favorite/count/${cardId}`)
}
