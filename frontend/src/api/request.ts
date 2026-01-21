/**
 * API 请求工具
 */

const API_BASE_URL = 'http://localhost:8080/api'

interface RequestOptions {
  method?: string
  headers?: Record<string, string>
  body?: any
}

/**
 * 封装 fetch 请求
 */
export async function request<T>(
  endpoint: string,
  options: RequestOptions = {}
): Promise<T> {
  const { method = 'GET', headers = {}, body } = options

  const config: RequestInit = {
    method,
    headers: {
      'Content-Type': 'application/json',
      ...headers,
    },
  }

  if (body) {
    config.body = JSON.stringify(body)
  }

  const response = await fetch(`${API_BASE_URL}${endpoint}`, config)
  const data = await response.json()

  if (data.code !== 200) {
    throw new Error(data.message || '请求失败')
  }

  return data.data
}

/**
 * GET 请求
 */
export function get<T>(endpoint: string): Promise<T> {
  return request<T>(endpoint, { method: 'GET' })
}

/**
 * POST 请求
 */
export function post<T>(endpoint: string, body: any): Promise<T> {
  return request<T>(endpoint, { method: 'POST', body })
}
