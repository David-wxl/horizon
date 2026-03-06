import { get, post } from './request'

export function getUserInfo(userId: number) {
  return get<any>(`/user/${userId}`)
}

export function updateProfile(user: any) {
  return post<any>('/user/updateProfile', user)
}

export function changePassword(userId: number, oldPassword: string, newPassword: string) {
  return post<boolean>('/user/changePassword', { userId: String(userId), oldPassword, newPassword })
}

export function getUserProfile(userId: number) {
  return get<any>(`/profile/${userId}`)
}

export function updateUserProfile(profile: any) {
  return post<boolean>('/profile/update', profile)
}
