import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import { message } from 'ant-design-vue'
import type { ApiResponse } from '@/types'

// 创建axios实例
const http: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://10.147.17.83:9001/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
http.interceptors.request.use(
  (config: AxiosRequestConfig) => {
    // 可以在这里添加token等认证信息
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
http.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    const { data } = response
    
    // 如果返回的数据不是标准的ApiResponse格式，直接返回
    if (typeof data !== 'object' || !('code' in data)) {
      return response
    }
    
    // 处理业务错误
    if (data.code !== 200 && data.code !== 1 && data.code !== 0) {
      message.error(data.message || '请求失败')
      return Promise.reject(new Error(data.message || '请求失败'))
    }
    
    return response
  },
  (error) => {
    // 处理HTTP错误
    const { response } = error
    
    if (response) {
      switch (response.status) {
        case 401:
          message.error('未授权，请重新登录')
          // 可以在这里处理跳转到登录页
          break
        case 403:
          message.error('拒绝访问')
          break
        case 404:
          message.error('请求的资源不存在')
          break
        case 500:
          message.error('服务器内部错误')
          break
        default:
          // 检查响应数据中是否有错误信息
          if (response.data && typeof response.data === 'object' && 'message' in response.data) {
            message.error(response.data.message || `请求失败 ${response.status}`)
          } else {
            message.error(`请求失败 ${response.status}`)
          }
      }
    } else if (error.request) {
      message.error('网络错误，请检查网络连接')
    } else {
      message.error('请求失败: ' + (error.message || '未知错误'))
    }
    
    return Promise.reject(error)
  }
)

export default http