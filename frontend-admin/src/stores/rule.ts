import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { message } from 'ant-design-vue'
import { RuleApi, type RuleQueryParams, type RuleTestRequest } from '@/api'
import type { Rule, CreateRuleDto, UpdateRuleDto, PageResult } from '@/types'

export const useRuleStore = defineStore('rule', () => {
  // State
  const rules = ref<Rule[]>([])
  const currentRule = ref<Rule | null>(null)
  const loading = ref(false)
  const pagination = ref({
    current: 1,
    pageSize: 20,
    total: 0
  })
  
  const filters = ref<RuleQueryParams>({
    keyword: undefined,
    isActive: undefined
  })

  // Getters
  const activeRules = computed(() => {
    return rules.value.filter(rule => rule.isActive)
  })

  const inactiveRules = computed(() => {
    return rules.value.filter(rule => !rule.isActive)
  })

  const ruleOptions = computed(() => {
    return rules.value.map(rule => ({
      label: rule.ruleName,
      value: rule.ruleId,
      active: rule.isActive
    }))
  })

  // Actions
  const fetchRules = async (params?: RuleQueryParams) => {
    try {
      loading.value = true
      
      const queryParams = {
        current: pagination.value.current,
        size: pagination.value.pageSize,
        ...filters.value,
        ...params
      }
      
      const response = await RuleApi.pageQuery(queryParams)
      
      if (response.code === 200 || response.code === 0) {
        const pageData = response.data as PageResult<Rule>
        rules.value = pageData.records
        pagination.value.total = pageData.total
        pagination.value.current = pageData.current
        pagination.value.pageSize = pageData.size
      } else {
        message.error(response.message || '获取规则列表失败')
      }
    } catch (error) {
      console.error('Failed to fetch rules:', error)
      message.error('获取规则列表失败')
    } finally {
      loading.value = false
    }
  }

  const fetchRuleById = async (id: number) => {
    try {
      loading.value = true
      const response = await RuleApi.getById(id)
      
      if (response.code === 200 || response.code === 0) {
        currentRule.value = response.data
        return response.data
      } else {
        message.error(response.message || '获取规则详情失败')
        return null
      }
    } catch (error) {
      console.error('Failed to fetch rule:', error)
      message.error('获取规则详情失败')
      return null
    } finally {
      loading.value = false
    }
  }

  const createRule = async (data: CreateRuleDto) => {
    try {
      loading.value = true
      const response = await RuleApi.create(data)
      
      if (response.code === 200 || response.code === 0) {
        message.success('创建规则成功')
        // 重新获取列表
        await fetchRules()
        return response.data
      } else {
        message.error(response.message || '创建规则失败')
        return null
      }
    } catch (error) {
      console.error('Failed to create rule:', error)
      message.error('创建规则失败')
      return null
    } finally {
      loading.value = false
    }
  }

  const updateRule = async (id: number, data: UpdateRuleDto) => {
    try {
      loading.value = true
      const response = await RuleApi.update(id, data)
      
      if (response.code === 200 || response.code === 0) {
        message.success('更新规则成功')
        // 重新获取列表
        await fetchRules()
        return true
      } else {
        message.error(response.message || '更新规则失败')
        return false
      }
    } catch (error) {
      console.error('Failed to update rule:', error)
      message.error('更新规则失败')
      return false
    } finally {
      loading.value = false
    }
  }

  const deleteRule = async (id: number) => {
    try {
      loading.value = true
      const response = await RuleApi.delete(id)
      
      if (response.code === 200 || response.code === 0) {
        message.success('删除规则成功')
        // 重新获取列表
        await fetchRules()
        return true
      } else {
        message.error(response.message || '删除规则失败')
        return false
      }
    } catch (error) {
      console.error('Failed to delete rule:', error)
      message.error('删除规则失败')
      return false
    } finally {
      loading.value = false
    }
  }

  const toggleRule = async (id: number) => {
    try {
      loading.value = true
      const response = await RuleApi.toggleRule(id)
      
      if (response.code === 200 || response.code === 0) {
        message.success('切换规则状态成功')
        // 重新获取列表
        await fetchRules()
        return true
      } else {
        message.error(response.message || '切换规则状态失败')
        return false
      }
    } catch (error) {
      console.error('Failed to toggle rule:', error)
      message.error('切换规则状态失败')
      return false
    } finally {
      loading.value = false
    }
  }

  const testRule = async (data: RuleTestRequest) => {
    try {
      loading.value = true
      const response = await RuleApi.testRule(data)
      
      if (response.code === 200 || response.code === 0) {
        return response.data
      } else {
        message.error(response.message || '测试规则失败')
        return null
      }
    } catch (error) {
      console.error('Failed to test rule:', error)
      message.error('测试规则失败')
      return null
    } finally {
      loading.value = false
    }
  }

  const setFilters = (newFilters: Partial<RuleQueryParams>) => {
    filters.value = { ...filters.value, ...newFilters }
  }

  const resetFilters = () => {
    filters.value = {
      keyword: undefined,
      isActive: undefined
    }
  }

  const setPagination = (current: number, pageSize: number) => {
    pagination.value.current = current
    pagination.value.pageSize = pageSize
  }

  return {
    // State
    rules,
    currentRule,
    loading,
    pagination,
    filters,
    
    // Getters
    activeRules,
    inactiveRules,
    ruleOptions,
    
    // Actions
    fetchRules,
    fetchRuleById,
    createRule,
    updateRule,
    deleteRule,
    toggleRule,
    testRule,
    setFilters,
    resetFilters,
    setPagination
  }
})