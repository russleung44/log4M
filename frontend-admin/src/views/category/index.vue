<template>
  <div class="category-management">
    <DataTable
      :columns="columns"
      :data-source="categoryStore.categories"
      :loading="categoryStore.loading"
      @search="handleSearch"
      @edit="handleEdit"
      @delete="handleDelete"
    >
      <template #actions>
        <a-space>
          <a-button type="primary" @click="handleCreate">
            <PlusOutlined />
            新增分类
          </a-button>
        </a-space>
      </template>

      <template #categoryName="{ record }">
        <div class="category-name">
          <span v-if="record.parentCategoryId" class="sub-category">└ </span>
          {{ record.categoryName }}
        </div>
      </template>

      <template #parentCategoryName="{ value }">
        <span v-if="value" class="parent-category">{{ value }}</span>
        <span v-else class="root-category">根分类</span>
      </template>
    </DataTable>

    <!-- 编辑弹窗 -->
    <a-modal
      v-model:open="editModalVisible"
      :title="editMode === 'create' ? '新增分类' : '编辑分类'"
      width="500px"
      @ok="handleSave"
      @cancel="handleCancel"
      :confirm-loading="saving"
    >
      <BaseForm
        ref="formRef"
        :form="formData"
        :rules="formRules"
        :loading="saving"
        :show-buttons="false"
      >
        <a-form-item label="分类名称" name="categoryName">
          <a-input
            v-model:value="formData.categoryName"
            placeholder="请输入分类名称"
          />
        </a-form-item>
        
        <a-form-item label="父分类" name="parentCategoryId">
          <a-tree-select
            v-model:value="formData.parentCategoryId"
            :tree-data="categoryTreeData"
            placeholder="请选择父分类（可选）"
            allow-clear
            tree-default-expand-all
          />
        </a-form-item>
      </BaseForm>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { PlusOutlined } from '@ant-design/icons-vue'

import { DataTable, BaseForm } from '@/components'
import { useCategoryStore } from '@/stores'
import type { Category, CreateCategoryDto, UpdateCategoryDto } from '@/types'

const categoryStore = useCategoryStore()

// 表格配置
const columns = [
  {
    title: '分类名称',
    dataIndex: 'categoryName',
    key: 'categoryName',
    width: 200
  },
  {
    title: '父分类',
    dataIndex: 'parentCategoryName',
    key: 'parentCategoryName',
    width: 150
  },
  {
    title: '排序',
    dataIndex: 'sort',
    key: 'sort',
    width: 80
  },
  {
    title: '是否默认',
    dataIndex: 'isDefault',
    key: 'isDefault',
    width: 100
  },
  {
    title: '创建时间',
    dataIndex: 'crTime',
    key: 'crTime',
    width: 180
  }
]

// 响应式数据
const editModalVisible = ref(false)
const editMode = ref<'create' | 'edit'>('create')
const currentRecord = ref<Category | null>(null)
const saving = ref(false)
const formRef = ref()

const formData = reactive({
  categoryName: '',
  parentCategoryId: null
})

const formRules = {
  categoryName: [
    { required: true, message: '请输入分类名称' },
    { min: 1, max: 50, message: '分类名称长度在1-50个字符' }
  ]
}

// 计算属性
const categoryTreeData = computed(() => {
  const buildTree = (categories: Category[], parentId: number | null = null): any[] => {
    return categories
      .filter(cat => cat.parentCategoryId === parentId)
      .map(cat => ({
        title: cat.categoryName,
        value: cat.categoryId,
        key: cat.categoryId,
        children: buildTree(categories, cat.categoryId)
      }))
  }
  
  return buildTree(categoryStore.categories)
})

// 方法
const handleSearch = (keyword: string) => {
  // 实现搜索逻辑
  console.log('搜索分类:', keyword)
}

const handleCreate = () => {
  editMode.value = 'create'
  currentRecord.value = null
  resetForm()
  editModalVisible.value = true
}

const handleEdit = (record: Category) => {
  editMode.value = 'edit'
  currentRecord.value = record
  formData.categoryName = record.categoryName
  formData.parentCategoryId = record.parentCategoryId
  editModalVisible.value = true
}

const handleDelete = (record: Category) => {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除分类 "${record.categoryName}" 吗？删除后该分类下的账单将变为未分类。`,
    onOk: async () => {
      await categoryStore.deleteCategory(record.categoryId)
    }
  })
}

const handleSave = async () => {
  try {
    await formRef.value?.validate()
    saving.value = true
    
    const data = {
      categoryName: formData.categoryName,
      parentCategoryId: formData.parentCategoryId
    }
    
    if (editMode.value === 'create') {
      await categoryStore.createCategory(data as CreateCategoryDto)
    } else {
      await categoryStore.updateCategory(currentRecord.value!.categoryId, data as UpdateCategoryDto)
    }
    
    editModalVisible.value = false
    resetForm()
  } catch (error) {
    console.error('保存失败:', error)
  } finally {
    saving.value = false
  }
}

const handleCancel = () => {
  editModalVisible.value = false
  resetForm()
}

const resetForm = () => {
  formData.categoryName = ''
  formData.parentCategoryId = null
  formRef.value?.resetFields()
}

// 生命周期
onMounted(async () => {
  await categoryStore.fetchCategories()
})
</script>

<style scoped>
.category-management {
  padding: 24px;
}

.category-name {
  display: flex;
  align-items: center;
}

.sub-category {
  color: #8c8c8c;
  margin-right: 4px;
}

.parent-category {
  color: #1890ff;
}

.root-category {
  color: #8c8c8c;
}
</style>