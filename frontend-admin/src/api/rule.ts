import http from '@/utils/http'
import type {
  ApiResponse,
  Rule,
  CreateRuleDto,
  UpdateRuleDto,
  PageResult
} from '@/types'

export interface RuleQueryParams {
  current?: number
  size?: number
  keyword?: string
  isActive?: boolean
}

export interface RuleTestRequest {
  text: string
}

export interface RuleTestResponse {
  text: string
  matched: boolean
  matchedRule?: Rule
  message: string
}

export class RuleApi {
  /**
   * 分页查询规则
   */
  static async pageQuery(params: RuleQueryParams) {
    const response = await http.get<ApiResponse<PageResult<Rule>>>('/rules', { params })
    return response.data
  }

  /**
   * 根据ID获取规则
   */
  static async getById(id: number) {
    const response = await http.get<ApiResponse<Rule>>(`/rules/${id}`)
    return response.data
  }

  /**
   * 创建规则
   */
  static async create(data: CreateRuleDto) {
    const response = await http.post<ApiResponse<Rule>>('/rules', data)
    return response.data
  }

  /**
   * 更新规则
   */
  static async update(id: number, data: UpdateRuleDto) {
    const response = await http.put<ApiResponse<boolean>>(`/rules/${id}`, data)
    return response.data
  }

  /**
   * 删除规则
   */
  static async delete(id: number) {
    const response = await http.delete<ApiResponse<boolean>>(`/rules/${id}`)
    return response.data
  }

  /**
   * 测试规则匹配
   */
  static async testRule(data: RuleTestRequest) {
    const response = await http.post<ApiResponse<RuleTestResponse>>('/rules/test', data)
    return response.data
  }

  /**
   * 启用/禁用规则
   */
  static async toggleRule(id: number) {
    const response = await http.put<ApiResponse<boolean>>(`/rules/${id}/toggle`)
    return response.data
  }
}