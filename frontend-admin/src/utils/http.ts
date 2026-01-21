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
      // 检查是否是 ResultVO 格式的响应
      const data = response.data
      if (data && typeof data === 'object') {
        // ResultVO 使用 msg 字段
        const errorMsg = data.msg || data.message
        if (errorMsg) {
          message.error(errorMsg)
          return Promise.reject(error)
        }
        if (typeof data === 'string') {
          message.error(data)
          return Promise.reject(error)
        }
      }

      // 其他情况按状态码处理
      switch (response.status) {
        case 400:
          message.error('请求参数错误')
          break
        case 401:
          message.error('未授权，请重新登录')
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
          message.error(`请求失败 ${response.status}`)
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