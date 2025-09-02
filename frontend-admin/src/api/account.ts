import http from '@/utils/http'
import type {
  ApiResponse,
  Account,
  PageResult
} from '@/types'

export interface AccountQueryParams {
  current?: number
  size?: number
  keyword?: string
}

export class AccountApi {
  /**
   * 分页查询账户
   */
  static async pageQuery(params: AccountQueryParams) {
    const response = await http.get<ApiResponse<PageResult<Account>>>('/accounts', { params })
    return response.data
  }

  /**
   * 获取所有账户
   */
  static async getAllAccounts() {
    const response = await http.get<ApiResponse<Account[]>>('/accounts/all')
    return response.data
  }

  /**
   * 根据ID获取账户
   */
  static async getById(id: number) {
    const response = await http.get<ApiResponse<Account>>(`/accounts/${id}`)
    return response.data
  }
}