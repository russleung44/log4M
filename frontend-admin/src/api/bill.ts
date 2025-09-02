import http from '@/utils/http'
import type {
  ApiResponse,
  Bill,
  CreateBillDto,
  UpdateBillDto,
  BatchDeleteDto,
  PageResult,
  DailyStatistics,
  MonthlyStatistics,
  CategoryStatistics,
  TransactionType
} from '@/types'

export interface BillQueryParams {
  current?: number
  size?: number
  categoryId?: number
  accountId?: number
  startDate?: string
  endDate?: string
  transactionType?: TransactionType
  keyword?: string
}

export class BillApi {
  /**
   * 分页查询账单
   */
  static async pageQuery(params: BillQueryParams) {
    const response = await http.get<ApiResponse<PageResult<Bill>>>('/bills', { params })
    return response.data
  }

  /**
   * 获取账单列表（简单列表）
   */
  static async list() {
    const response = await http.get<ApiResponse<Bill[]>>('/bills/list')
    return response.data
  }

  /**
   * 根据ID获取账单
   */
  static async getById(id: number) {
    const response = await http.get<ApiResponse<Bill>>(`/bills/${id}`)
    return response.data
  }

  /**
   * 创建账单
   */
  static async create(data: CreateBillDto) {
    const response = await http.post<ApiResponse<Bill>>('/bills', data)
    return response.data
  }

  /**
   * 更新账单
   */
  static async update(id: number, data: UpdateBillDto) {
    const response = await http.put<ApiResponse<boolean>>(`/bills/${id}`, data)
    return response.data
  }

  /**
   * 删除账单
   */
  static async delete(id: number) {
    const response = await http.delete<ApiResponse<boolean>>(`/bills/${id}`)
    return response.data
  }

  /**
   * 批量删除账单
   */
  static async batchDelete(data: BatchDeleteDto) {
    const response = await http.delete<ApiResponse<boolean>>('/bills/batch', { data })
    return response.data
  }

  /**
   * 获取日统计
   */
  static async getDailyStatistics(date: string) {
    const response = await http.get<ApiResponse<DailyStatistics>>('/bills/statistics/daily', {
      params: { date }
    })
    return response.data
  }

  /**
   * 获取月统计
   */
  static async getMonthlyStatistics(month: string) {
    const response = await http.get<ApiResponse<MonthlyStatistics>>('/bills/statistics/monthly', {
      params: { month }
    })
    return response.data
  }

  /**
   * 获取分类统计
   */
  static async getCategoryStatistics(startDate: string, endDate: string, transactionType?: TransactionType) {
    const response = await http.get<ApiResponse<CategoryStatistics[]>>('/bills/statistics/category', {
      params: { startDate, endDate, transactionType }
    })
    return response.data
  }
}