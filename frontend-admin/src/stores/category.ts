import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { message } from 'ant-design-vue'
import { CategoryApi, type CategoryQueryParams } from '@/api'
import type { Category, CreateCategoryDto, UpdateCategoryDto, PageResult, CategoryType } from '@/types'

export interface CategoryTreeNode extends Category {
  children?: CategoryTreeNode[]
}

export const useCategoryStore = defineStore('category', () => {
  // State
  const categories = ref<Category[]>([]) // 分页数据，用于列表展示
  const allCategories = ref<Category[]>([]) // ���量数据，用于选择器
  const categoryTree = ref<CategoryTreeNode[]>([])
  const currentCategory = ref<Category | null>(null)
  const loading = ref(false)
  const pagination = ref({
    current: 1,
    pageSize: 20,
    total: 0
  })

  // Getters
  const incomeCategories = computed(() => {
    return categories.value.filter(cat => cat.categoryType === 'INCOME')
  })

  const expenseCategories = computed(() => {
    return categories.value.filter(cat => cat.categoryType === 'EXPENSE')
  })

  const categoryOptions = computed(() => {
    return allCategories.value.map(cat => ({
      label: cat.categoryName,
      value: cat.categoryId,
      type: cat.categoryType
    }))
  })

  // Actions
  const fetchCategories = async (params?: CategoryQueryParams) => {
    try {
      loading.value = true
      
      const queryParams = {
        current: pagination.value.current,
        size: pagination.value.pageSize,
        ...params
      }
      
      const response = await CategoryApi.pageQuery(queryParams)
      
      if (response.code === 200 || response.code === 0) {
        const pageData = response.data as PageResult<Category>
        categories.value = pageData.records
        pagination.value.total = pageData.total
        pagination.value.current = pageData.current
        pagination.value.pageSize = pageData.size
      } else {
        message.error(response.message || '获取分类列表失败')
      }
    } catch (error) {
      console.error('Failed to fetch categories:', error)
      message.error('获取分类列表失败')
    } finally {
      loading.value = false
    }
  }

  const fetchAllCategories = async () => {
    try {
      loading.value = true
      const response = await CategoryApi.getAllCategories()

      console.log('getAllCategories 响应:', response)

      if (response.code === 200 || response.code === 0 || response.code === 1) {
        allCategories.value = response.data
        console.log('更新后的 allCategories:', allCategories.value)
      } else {
        message.error(response.msg || response.message || '获取所有分类失败')
      }
    } catch (error) {
      console.error('Failed to fetch all categories:', error)
      message.error('获取所有分类失败')
    } finally {
      loading.value = false
    }
  }

  const fetchCategoryTree = async () => {
    try {
      loading.value = true
      const response = await CategoryApi.getCategoryTree()
      
      if (response.code === 200 || response.code === 0) {
        // 构建树形结构
        categoryTree.value = buildCategoryTree(response.data)
      } else {
        message.error(response.message || '获取分类树失败')
      }
    } catch (error) {
      console.error('Failed to fetch category tree:', error)
      message.error('获取分类树失败')
    } finally {
      loading.value = false
    }
  }

  const fetchCategoryById = async (id: number) => {
    try {
      loading.value = true
      const response = await CategoryApi.getById(id)
      
      if (response.code === 200 || response.code === 0) {
        currentCategory.value = response.data
        return response.data
      } else {
        message.error(response.message || '获取分类详情失败')
        return null
      }
    } catch (error) {
      console.error('Failed to fetch category:', error)
      message.error('获取分类详情失败')
      return null
    } finally {
      loading.value = false
    }
  }

  const createCategory = async (data: CreateCategoryDto) => {
    try {
      loading.value = true
      const response = await CategoryApi.create(data)
      
      if (response.code === 200 || response.code === 0) {
        message.success('创建分类成功')
        // 重新获取列表
        await fetchCategories()
        await fetchAllCategories()
        return response.data
      } else {
        message.error(response.message || '创建分类失败')
        return null
      }
    } catch (error) {
      console.error('Failed to create category:', error)
      message.error('创建分类失败')
      return null
    } finally {
      loading.value = false
    }
  }

  const updateCategory = async (id: number, data: UpdateCategoryDto) => {
    try {
      loading.value = true
      const response = await CategoryApi.update(id, data)
      
      if (response.code === 200 || response.code === 0) {
        message.success('更新分类成功')
        // 重新获取列表
        await fetchCategories()
        await fetchAllCategories()
        return true
      } else {
        message.error(response.message || '更新分类失败')
        return false
      }
    } catch (error) {
      console.error('Failed to update category:', error)
      message.error('更新分类失败')
      return false
    } finally {
      loading.value = false
    }
  }

  const deleteCategory = async (id: number) => {
    try {
      loading.value = true
      const response = await CategoryApi.delete(id)

      if (response.code === 200 || response.code === 0) {
        message.success('删除分类成功')
        // 重新获取列表
        await fetchCategories()
        await fetchAllCategories()
        return true
      } else {
        // HTTP 拦截器已经显示了错误消息
        return false
      }
    } catch (error) {
      // HTTP 拦截器已经显示了错误消息
      console.error('Failed to delete category:', error)
      return false
    } finally {
      loading.value = false
    }
  }

  const setPagination = (current: number, pageSize: number) => {
    pagination.value.current = current
    pagination.value.pageSize = pageSize
  }

  // 构建树形结构的辅助函数
  const buildCategoryTree = (categories: Category[]): CategoryTreeNode[] => {
    const map = new Map<number, CategoryTreeNode>()
    const roots: CategoryTreeNode[] = []

    // 创建节点映射
    categories.forEach(category => {
      map.set(category.categoryId, { ...category, children: [] })
    })

    // 构建树形结构
    categories.forEach(category => {
      const node = map.get(category.categoryId)!
      if (category.parentCategoryId && map.has(category.parentCategoryId)) {
        const parent = map.get(category.parentCategoryId)!
        parent.children!.push(node)
      } else {
        roots.push(node)
      }
    })

    return roots
  }

  return {
    // State
    categories,
    allCategories,
    categoryTree,
    currentCategory,
    loading,
    pagination,

    // Getters
    incomeCategories,
    expenseCategories,
    categoryOptions,

    // Actions
    fetchCategories,
    fetchAllCategories,
    fetchCategoryTree,
    fetchCategoryById,
    createCategory,
    updateCategory,
    deleteCategory,
    setPagination
  }
})