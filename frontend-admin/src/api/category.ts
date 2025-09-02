import http from '@/utils/http'
import type {
  ApiResponse,
  Category,
  CreateCategoryDto,
  UpdateCategoryDto,
  PageResult
} from '@/types'

export interface CategoryQueryParams {
  current?: number
  size?: number
  keyword?: string
}

export class CategoryApi {
  /**
   * 分页查询分类
   */
  static async pageQuery(params: CategoryQueryParams) {
    const response = await http.get<ApiResponse<PageResult<Category>>>('/categories', { params })
    return response.data
  }

  /**
   * 获取分类树
   */
  static async getCategoryTree() {
    const response = await http.get<ApiResponse<Category[]>>('/categories/tree')
    return response.data
  }

  /**
   * 获取所有分类
   */
  static async getAllCategories() {
    const response = await http.get<ApiResponse<Category[]>>('/categories/all')
    return response.data
  }

  /**
   * 根据ID获取分类
   */
  static async getById(id: number) {
    const response = await http.get<ApiResponse<Category>>(`/categories/${id}`)
    return response.data
  }

  /**
   * 创建分类
   */
  static async create(data: CreateCategoryDto) {
    const response = await http.post<ApiResponse<Category>>('/categories', data)
    return response.data
  }

  /**
   * 更新分类
   */
  static async update(id: number, data: UpdateCategoryDto) {
    const response = await http.put<ApiResponse<boolean>>(`/categories/${id}`, data)
    return response.data
  }

  /**
   * 删除分类
   */
  static async delete(id: number) {
    const response = await http.delete<ApiResponse<boolean>>(`/categories/${id}`)
    return response.data
  }
}