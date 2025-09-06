// 基础响应类型
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

// 分页参数
export interface PageParams {
  current: number
  size: number
}

// 分页响应
export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

// 交易类型
export enum TransactionType {
  INCOME = 'INCOME',
  EXPENSE = 'EXPENSE'
}

// 分类类型
export enum CategoryType {
  INCOME = 'INCOME',
  EXPENSE = 'EXPENSE'
}

// 账单接口
export interface Bill {
  billId: number
  billDate: string
  amount: number
  note: string  // 改为 note
  categoryId: number
  categoryName?: string
  accountId: number
  accountName?: string
  tagId?: number
  tagName?: string
  transactionType: TransactionType
  crTime: string
  upTime: string
}

// 创建账单DTO
export interface CreateBillDto {
  billDate: string
  amount: number
  note: string  // 改为 note
  categoryId: number
  accountId: number
  tagId?: number
  transactionType: TransactionType
}

// 更新账单DTO
export interface UpdateBillDto {
  billDate: string
  amount: number
  note: string  // 改为 note
  categoryId: number
  accountId: number
  tagId?: number
  transactionType: TransactionType
}

// 分类接口
export interface Category {
  categoryId: number
  categoryName: string
  parentCategoryId?: number
  categoryType: CategoryType
  icon?: string
  color?: string
  description?: string
  crTime: string
  upTime: string
}

// 创建分类DTO
export interface CreateCategoryDto {
  categoryName: string
  parentCategoryId?: number
  categoryType: CategoryType
  icon?: string
  color?: string
  description?: string
}

// 更新分类DTO
export interface UpdateCategoryDto {
  categoryName: string
  parentCategoryId?: number
  categoryType: CategoryType
  icon?: string
  color?: string
  description?: string
}

// 规则接口
export interface Rule {
  ruleId: number
  ruleName: string
  keywords: string
  categoryId: number
  amount?: number
  transactionType: TransactionType
  priority: number
  isActive: boolean
  crTime: string
  upTime: string
}

// 创建规则DTO
export interface CreateRuleDto {
  ruleName: string
  keywords: string
  categoryId: number
  amount?: number
  transactionType: TransactionType
  priority?: number
  isActive?: boolean
}

// 更新规则DTO
export interface UpdateRuleDto {
  ruleName: string
  keywords: string
  categoryId: number
  amount?: number
  transactionType: TransactionType
  priority?: number
  isActive?: boolean
}

// 账户接口
export interface Account {
  accountId: number
  accountName: string
  accountType: string
  balance?: number
  currency?: string
  isDefault?: boolean
  crTime: string
  upTime: string
}

// 用户接口
export interface User {
  userId: number
  chatId: number
  username?: string
  firstName?: string
  lastName?: string
  languageCode?: string
  crTime: string
  upTime: string
}

// 统计数据接口
export interface DailyStatistics {
  date: string
  income: number
  expense: number
  balance: number
  count: number
}

export interface MonthlyStatistics {
  month: string
  income: number
  expense: number
  balance: number
  count: number
}

export interface CategoryStatistics {
  categoryName: string
  amount: number
  count: number
}

export interface TrendStatistics {
  date: string
  income: number
  expense: number
}

// 年统计接口
export interface YearlyStatistics {
  year: string
  expense: number
}

// 批量删除DTO
export interface BatchDeleteDto {
  ids: number[]
}
