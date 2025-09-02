import http from '@/utils/http'
import type {
  ApiResponse,
  User,
  PageResult
} from '@/types'

export interface UserQueryParams {
  current?: number
  size?: number
  keyword?: string
}

export class UserApi {
  /**
   * 分页查询用户
   */
  static async pageQuery(params: UserQueryParams) {
    const response = await http.get<ApiResponse<PageResult<User>>>('/users', { params })
    return response.data
  }

  /**
   * 获取所有用户
   */
  static async getAllUsers() {
    const response = await http.get<ApiResponse<User[]>>('/users/all')
    return response.data
  }

  /**
   * 根据ID获取用户
   */
  static async getById(id: number) {
    const response = await http.get<ApiResponse<User>>(`/users/${id}`)
    return response.data
  }
}