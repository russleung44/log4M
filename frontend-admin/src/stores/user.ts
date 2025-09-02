import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { message } from 'ant-design-vue'
import { UserApi, type UserQueryParams } from '@/api'
import type { User, PageResult } from '@/types'

export const useUserStore = defineStore('user', () => {
  // State
  const users = ref<User[]>([])
  const currentUser = ref<User | null>(null)
  const loading = ref(false)
  const pagination = ref({
    current: 1,
    pageSize: 20,
    total: 0
  })

  // Getters
  const userOptions = computed(() => {
    return users.value.map(user => ({
      label: user.username || `${user.firstName} ${user.lastName}`.trim(),
      value: user.userId
    }))
  })

  const totalUsers = computed(() => {
    return users.value.length
  })

  // Actions
  const fetchUsers = async (params?: UserQueryParams) => {
    try {
      loading.value = true
      
      const queryParams = {
        current: pagination.value.current,
        size: pagination.value.pageSize,
        ...params
      }
      
      const response = await UserApi.pageQuery(queryParams)
      
      if (response.code === 200 || response.code === 0) {
        const pageData = response.data as PageResult<User>
        users.value = pageData.records
        pagination.value.total = pageData.total
        pagination.value.current = pageData.current
        pagination.value.pageSize = pageData.size
      } else {
        message.error(response.message || '获取用户列表失败')
      }
    } catch (error) {
      console.error('Failed to fetch users:', error)
      message.error('获取用户列表失败')
    } finally {
      loading.value = false
    }
  }

  const fetchAllUsers = async () => {
    try {
      loading.value = true
      const response = await UserApi.getAllUsers()
      
      if (response.code === 200 || response.code === 0) {
        users.value = response.data
      } else {
        message.error(response.message || '获取所有用户失败')
      }
    } catch (error) {
      console.error('Failed to fetch all users:', error)
      message.error('获取所有用户失败')
    } finally {
      loading.value = false
    }
  }

  const fetchUserById = async (id: number) => {
    try {
      loading.value = true
      const response = await UserApi.getById(id)
      
      if (response.code === 200 || response.code === 0) {
        currentUser.value = response.data
        return response.data
      } else {
        message.error(response.message || '获取用户详情失败')
        return null
      }
    } catch (error) {
      console.error('Failed to fetch user:', error)
      message.error('获取用户详情失败')
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
    users,
    currentUser,
    loading,
    pagination,
    
    // Getters
    userOptions,
    totalUsers,
    
    // Actions
    fetchUsers,
    fetchAllUsers,
    fetchUserById,
    setPagination
  }
})