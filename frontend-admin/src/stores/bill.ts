import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { message } from 'ant-design-vue'
import { BillApi, type BillQueryParams } from '@/api'
import type { Bill, CreateBillDto, UpdateBillDto, PageResult, TransactionType } from '@/types'

export const useBillStore = defineStore('bill', () => {
  // State
  const bills = ref<Bill[]>([])
  const currentBill = ref<Bill | null>(null)
  const loading = ref(false)
  const pagination = ref({
    current: 1,
    pageSize: 20,
    total: 0
  })
  
  const filters = ref<BillQueryParams>({
    categoryId: undefined,
    accountId: undefined,
    startDate: undefined,
    endDate: undefined,
    transactionType: undefined,
    keyword: undefined
  })

  // Getters
  const totalIncome = computed(() => {
    return bills.value
      .filter(bill => bill.transactionType === 'INCOME')
      .reduce((sum, bill) => sum + bill.amount, 0)
  })

  const totalExpense = computed(() => {
    return bills.value
      .filter(bill => bill.transactionType === 'EXPENSE')
      .reduce((sum, bill) => sum + bill.amount, 0)
  })

  const balance = computed(() => {
    return totalIncome.value - totalExpense.value
  })

  // Actions
  const fetchBills = async (params?: BillQueryParams) => {
    try {
      loading.value = true
      
      const queryParams = {
        current: pagination.value.current,
        size: pagination.value.pageSize,
        ...filters.value,
        ...params
      }
      
      const response = await BillApi.pageQuery(queryParams)
      
      if (response.code === 200 || response.code === 0) {
        const pageData = response.data as PageResult<Bill>
        bills.value = pageData.records
        pagination.value.total = pageData.total
        pagination.value.current = pageData.current
        pagination.value.pageSize = pageData.size
      } else {
        message.error(response.message || '获取账单列表失败')
      }
    } catch (error) {
      console.error('Failed to fetch bills:', error)
      message.error('获取账单列表失败')
    } finally {
      loading.value = false
    }
  }

  const fetchBillById = async (id: number) => {
    try {
      loading.value = true
      const response = await BillApi.getById(id)
      
      if (response.code === 200 || response.code === 0) {
        currentBill.value = response.data
        return response.data
      } else {
        message.error(response.message || '获取账单详情失败')
        return null
      }
    } catch (error) {
      console.error('Failed to fetch bill:', error)
      message.error('获取账单详情失败')
      return null
    } finally {
      loading.value = false
    }
  }

  const createBill = async (data: CreateBillDto) => {
    try {
      loading.value = true
      const response = await BillApi.create(data)
      
      if (response.code === 200 || response.code === 0) {
        message.success('创建账单成功')
        // 重新获取列表
        await fetchBills()
        return response.data
      } else {
        message.error(response.message || '创建账单失败')
        return null
      }
    } catch (error) {
      console.error('Failed to create bill:', error)
      message.error('创建账单失败')
      return null
    } finally {
      loading.value = false
    }
  }

  const updateBill = async (id: number, data: UpdateBillDto) => {
    try {
      loading.value = true
      const response = await BillApi.update(id, data)
      
      if (response.code === 200 || response.code === 0) {
        message.success('更新账单成功')
        // 重新获取列表
        await fetchBills()
        return true
      } else {
        message.error(response.message || '更新账单失败')
        return false
      }
    } catch (error) {
      console.error('Failed to update bill:', error)
      message.error('更新账单失败')
      return false
    } finally {
      loading.value = false
    }
  }

  const deleteBill = async (id: number) => {
    try {
      loading.value = true
      const response = await BillApi.delete(id)
      
      if (response.code === 200 || response.code === 0) {
        message.success('删除账单成功')
        // 重新获取列表
        await fetchBills()
        return true
      } else {
        message.error(response.message || '删除账单失败')
        return false
      }
    } catch (error) {
      console.error('Failed to delete bill:', error)
      message.error('删除账单失败')
      return false
    } finally {
      loading.value = false
    }
  }

  const batchDeleteBills = async (ids: number[]) => {
    try {
      loading.value = true
      const response = await BillApi.batchDelete({ ids })
      
      if (response.code === 200 || response.code === 0) {
        message.success(`成功删除 ${ids.length} 条账单`)
        // 重新获取列表
        await fetchBills()
        return true
      } else {
        message.error(response.message || '批量删除账单失败')
        return false
      }
    } catch (error) {
      console.error('Failed to batch delete bills:', error)
      message.error('批量删除账单失败')
      return false
    } finally {
      loading.value = false
    }
  }

  const setFilters = (newFilters: Partial<BillQueryParams>) => {
    filters.value = { ...filters.value, ...newFilters }
  }

  const resetFilters = () => {
    filters.value = {
      categoryId: undefined,
      accountId: undefined,
      startDate: undefined,
      endDate: undefined,
      transactionType: undefined,
      keyword: undefined
    }
  }

  const setPagination = (current: number, pageSize: number) => {
    pagination.value.current = current
    pagination.value.pageSize = pageSize
  }

  return {
    // State
    bills,
    currentBill,
    loading,
    pagination,
    filters,
    
    // Getters
    totalIncome,
    totalExpense,
    balance,
    
    // Actions
    fetchBills,
    fetchBillById,
    createBill,
    updateBill,
    deleteBill,
    batchDeleteBills,
    setFilters,
    resetFilters,
    setPagination
  }
})