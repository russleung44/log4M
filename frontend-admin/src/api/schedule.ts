import http from '@/utils/http'
import type { ApiResponse, BillSchedule, SaveBillScheduleDto } from '@/types'

export class BillScheduleApi {
  /**
   * 查询定时记账列表
   */
  static async list() {
    const response = await http.get<ApiResponse<BillSchedule[]>>('/schedules')
    return response.data
  }

  /**
   * 创建定时记账
   */
  static async create(data: SaveBillScheduleDto) {
    const response = await http.post<ApiResponse<BillSchedule>>('/schedules', data)
    return response.data
  }

  /**
   * 更新定时记账
   */
  static async update(id: number, data: SaveBillScheduleDto) {
    const response = await http.put<ApiResponse<boolean>>(`/schedules/${id}`, data)
    return response.data
  }

  /**
   * 删除定时记账
   */
  static async delete(id: number) {
    const response = await http.delete<ApiResponse<boolean>>(`/schedules/${id}`)
    return response.data
  }
}
