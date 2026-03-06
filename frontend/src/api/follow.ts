import { get, post, request } from './request'

export function followUser(followerId: number, followingId: number) {
  return post<boolean>(`/follow?followerId=${followerId}&followingId=${followingId}`)
}

export function unfollowUser(followerId: number, followingId: number) {
  return request<boolean>(`/follow?followerId=${followerId}&followingId=${followingId}`, { method: 'DELETE' })
}

export function checkFollowing(followerId: number, followingId: number) {
  return get<boolean>(`/follow/check?followerId=${followerId}&followingId=${followingId}`)
}

export function getFollowStats(userId: number) {
  return get<{ followerCount: number; followingCount: number }>(`/follow/stats/${userId}`)
}
