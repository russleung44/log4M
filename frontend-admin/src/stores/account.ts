import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { message } from 'ant-design-vue'
import { AccountApi, type AccountQueryParams } from '@/api'
import type { Account, PageResult } from '@/types'

export const useAccountStore = defineStore('account', () => {
  // State
  const accounts = ref<Account[]>([])
  const currentAccount = ref<Account | null>(null)
  const loading = ref(false)
  const pagination = ref({
    current: 1,
    pageSize: 20,
    total: 0
  })

  // Getters
  const accountOptions = computed(() => {
    return accounts.value.map(account => ({
      label: account.accountName,
      value: account.accountId,
      type: account.accountType
    }))
  })

  const defaultAccount = computed(() => {
    return accounts.value.find(account => account.isDefault)
  })

  // Actions
  const fetchAccounts = async (params?: AccountQueryParams) => {
    try {
      loading.value = true
      
      const queryParams = {
        current: pagination.value.current,
        size: pagination.value.pageSize,
        ...params
      }
      
      const response = await AccountApi.pageQuery(queryParams)
      
      if (response.code === 200 || response.code === 0) {
        const pageData = response.data as PageResult<Account>
        accounts.value = pageData.records
        pagination.value.total = pageData.total
        pagination.value.current = pageData.current
        pagination.value.pageSize = pageData.size
      } else {
        message.error(response.message || '获取账户列表失败')
      }
    } catch (error) {
      console.error('Failed to fetch accounts:', error)
      message.error('获取账户列表失败')
    } finally {
      loading.value = false
    }
  }

  const fetchAllAccounts = async () => {
    try {
      loading.value = true
      const response = await AccountApi.getAllAccounts()
      
      if (response.code === 200 || response.code === 0) {
        accounts.value = response.data
      } else {
        message.error(response.message || '获取所有账户失败')
      }
    } catch (error) {
      console.error('Failed to fetch all accounts:', error)
      message.error('获取所有账户失败')
    } finally {
      loading.value = false
    }
  }

  const fetchAccountById = async (id: number) => {
    try {
      loading.value = true
      const response = await AccountApi.getById(id)
      
      if (response.code === 200 || response.code === 0) {
        currentAccount.value = response.data
        return response.data
      } else {
        message.error(response.message || '获取账户详情失败')
        return null
      }
    } catch (error) {
      console.error('Failed to fetch account:', error)
      message.error('获取账户详情失败')
      return null
    } finally {
      loading.value = false
    }
  }

  const setPagination = (current: number, pageSize: number) => {
    pagination.value.current = current
    pagination.value.pageSize = pageSize
  }

  return {
    // State
    accounts,
    currentAccount,
    loading,
    pagination,
    
    // Getters
    accountOptions,
    defaultAccount,
    
    // Actions
    fetchAccounts,
    fetchAllAccounts,
    fetchAccountById,
    setPagination
  }
})