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

  // 自动添加 JWT Token
  const token = localStorage.getItem('token')
  const config: RequestInit = {
    method,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { 'Authorization': token } : {}),
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
export function get<T>(endpoint: string, params?: Record<string, any>): Promise<T> {
  let url = endpoint
  if (params) {
    const queryParams = new URLSearchParams()
    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null) {
        queryParams.append(key, String(value))
      }
    })
    const queryString = queryParams.toString()
    if (queryString) {
      url = `${endpoint}?${queryString}`
    }
  }
  return request<T>(url, { method: 'GET' })
}

/**
 * POST 请求
 */
export function post<T>(endpoint: string, body?: any, config?: { params?: Record<string, any> }): Promise<T> {
  let url = endpoint
  if (config?.params) {
    const params = new URLSearchParams()
    Object.entries(config.params).forEach(([key, value]) => {
      params.append(key, String(value))
    })
    url = `${endpoint}?${params.toString()}`
  }
  return request<T>(url, { method: 'POST', body })
}

/**
 * PUT 请求
 */
export function put<T>(endpoint: string, body?: any): Promise<T> {
  return request<T>(endpoint, { method: 'PUT', body })
}

/**
 * DELETE 请求
 */
export function del<T>(endpoint: string): Promise<T> {
  return request<T>(endpoint, { method: 'DELETE' })
}
