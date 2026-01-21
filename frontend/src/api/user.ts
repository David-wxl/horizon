/**
 * 用户相关 API
 */
import { get } from './request'

/**
 * 测试后端连接
 */
export function testConnection() {
  return get<string>('/user/test')
}
