<template>
  <div class="bill-management">
    <!-- 筛选区域 -->
    <div class="filter-section">
      <a-space size="middle">
        <div class="filter-item">
          <label>分类：</label>
          <a-select
            v-model:value="selectedCategoryId"
            placeholder="全部分类"
            style="width: 180px"
            allow-clear
            show-search
            :filter-option="filterOption"
            @change="handleCategoryChange"
          >
            <a-select-option :value="-1">
              未分类
            </a-select-option>
            <a-select-option
              v-for="category in categoryStore.categories"
              :key="category.categoryId"
              :value="category.categoryId"
            >
              {{ category.categoryName }}
            </a-select-option>
          </a-select>
        </div>

        <div class="filter-item">
          <label>交易类型：</label>
          <a-select
            v-model:value="selectedTransactionType"
            placeholder="全部类型"
            style="width: 120px"
            allow-clear
            @change="handleTransactionTypeChange"
          >
            <a-select-option value="EXPENSE">支出</a-select-option>
            <a-select-option value="INCOME">收入</a-select-option>
          </a-select>
        </div>

        <div class="filter-item">
          <label>日期范围：</label>
          <a-range-picker
            v-model:value="dateRange"
            format="YYYY-MM-DD"
            :placeholder="['开始日期', '结束日期']"
            style="width: 260px"
            @change="handleDateRangeChange"
          />
        </div>

        <div class="filter-item">
          <label>金额范围：</label>
          <a-input-group compact style="display: flex; align-items: center; gap: 4px;">
            <a-input-number
              v-model:value="minAmount"
              placeholder="最小金额"
              :min="0"
              :precision="2"
              style="width: 110px"
              @change="handleAmountChange"
            />
            <span style="padding: 0 4px;">~</span>
            <a-input-number
              v-model:value="maxAmount"
              placeholder="最大金额"
              :min="0"
              :precision="2"
              style="width: 110px"
              @change="handleAmountChange"
            />
          </a-input-group>
        </div>
      </a-space>
    </div>

    <DataTable
      :columns="columns"
      :data-source="billStore.bills"
      :loading="billStore.loading"
      :pagination="paginationConfig"
      @search="handleSearch"
      @change="handleTableChange"
      @edit="handleEdit"
      @delete="handleDelete"
      @selection-change="handleSelectionChange"
    >
      <template #actions="{ selectedRows }">
        <a-space>
          <a-button type="primary" @click="handleCreate">
            <PlusOutlined />
            新增账单
          </a-button>
          <a-button
            v-if="hasActiveFilters"
            @click="handleResetFilters"
          >
            <ReloadOutlined />
            重置筛选
          </a-button>
          <a-button
            v-if="selectedRows.length > 0"
            danger
            @click="handleBatchDelete"
          >
            批量删除 ({{ selectedRows.length }})
          </a-button>
          <a-button @click="handleExport">
            <ExportOutlined />
            导出
          </a-button>
        </a-space>
      </template>

      <template #transactionType="{ value }">
        <a-tag :color="value === 'INCOME' ? 'green' : 'red'">
          {{ value === 'INCOME' ? '收入' : '支出' }}
        </a-tag>
      </template>

      <template #amount="{ value, record }">
        <span :class="['amount-text', record.transactionType === 'INCOME' ? 'income' : 'expense']">
          {{ record.transactionType === 'INCOME' ? '+' : '-' }}¥{{ value }}
        </span>
      </template>

      <template #billDate="{ value }">
        {{ formatDate(value) }}
      </template>
    </DataTable>

    <!-- 编辑弹窗 -->
    <a-modal
      v-model:open="editModalVisible"
      :title="editMode === 'create' ? '新增账单' : '编辑账单'"
      width="600px"
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
        @submit="handleSave"
      >
        <a-form-item label="日期" name="billDate">
          <a-date-picker 
            v-model:value="formData.billDate" 
            style="width: 100%" 
            format="YYYY-MM-DD"
          />
        </a-form-item>
        
        <a-form-item label="交易类型" name="transactionType">
          <a-radio-group v-model:value="formData.transactionType">
            <a-radio value="EXPENSE">支出</a-radio>
            <a-radio value="INCOME">收入</a-radio>
          </a-radio-group>
        </a-form-item>
        
        <a-form-item label="金额" name="amount">
          <a-input-number
            v-model:value="formData.amount"
            :min="0"
            :precision="2"
            style="width: 100%"
            placeholder="请输入金额"
          />
        </a-form-item>
        
        <a-form-item label="分类" name="categoryId">
          <a-select
            v-model:value="formData.categoryId"
            placeholder="请选择分类"
            show-search
            :filter-option="filterOption"
          >
            <a-select-option
              v-for="category in categoryStore.categories"
              :key="category.categoryId"
              :value="category.categoryId"
            >
              {{ category.categoryName }}
            </a-select-option>
          </a-select>
        </a-form-item>
        
        <a-form-item label="账户" name="accountId">
          <a-select
            v-model:value="formData.accountId"
            placeholder="请选择账户"
          >
            <a-select-option
              v-for="account in accountStore.accounts"
              :key="account.accountId"
              :value="account.accountId"
            >
              {{ account.accountName }}
            </a-select-option>
          </a-select>
        </a-form-item>
        
        <a-form-item label="备注" name="note">
          <a-textarea
            v-model:value="formData.note"
            placeholder="请输入备注"
            :rows="3"
          />
        </a-form-item>
      </BaseForm>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { PlusOutlined, ExportOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import dayjs, { Dayjs } from 'dayjs'

import { DataTable, BaseForm } from '@/components'
import { useBillStore, useCategoryStore, useAccountStore } from '@/stores'
import type { Bill, CreateBillDto, UpdateBillDto } from '@/types'

const billStore = useBillStore()
const categoryStore = useCategoryStore()
const accountStore = useAccountStore()

// 表格配置
const columns = [
  {
    title: '日期',
    dataIndex: 'billDate',
    key: 'billDate',
    sorter: true
  },
  {
    title: '备注',
    dataIndex: 'note',
    key: 'note',
    ellipsis: true
  },
  {
    title: '金额',
    dataIndex: 'amount',
    key: 'amount',
    sorter: true
  },
  {
    title: '分类',
    dataIndex: 'categoryName',
    key: 'categoryName'
  },
  {
    title: '类型',
    dataIndex: 'transactionType',
    key: 'transactionType'
  }
]

// 响应式数据
const editModalVisible = ref(false)
const editMode = ref<'create' | 'edit'>('create')
const currentRecord = ref<Bill | null>(null)
const saving = ref(false)
const selectedRows = ref<Bill[]>([])
const formRef = ref()
const selectedCategoryId = ref<number | undefined>()
const selectedTransactionType = ref<'INCOME' | 'EXPENSE' | undefined>()
const dateRange = ref<[Dayjs, Dayjs] | null>()
const minAmount = ref<number | undefined>()
const maxAmount = ref<number | undefined>()

const formData = reactive({
  billDate: null as Dayjs | null,
  transactionType: 'EXPENSE',
  amount: undefined as number | undefined,
  categoryId: undefined as number | undefined,
  accountId: undefined as number | undefined,
  note: ''
})

const formRules = {
  billDate: [{ required: true, message: '请选择日期' }],
  transactionType: [{ required: true, message: '请选择交易类型' }],
  amount: [{ required: true, message: '请输入金额' }],
  categoryId: [{ required: true, message: '请选择分类' }],
  accountId: [{ required: true, message: '请选择账户' }]
}

// 计算属性
const paginationConfig = computed(() => ({
  current: billStore.pagination.current,
  pageSize: billStore.pagination.pageSize,
  total: billStore.pagination.total
}))

const hasActiveFilters = computed(() => {
  return selectedCategoryId.value !== undefined ||
         selectedTransactionType.value !== undefined ||
         dateRange.value !== null ||
         minAmount.value !== undefined ||
         maxAmount.value !== undefined
})

// 方法
const formatDate = (date: string) => {
  console.log('账单页面格式化日期:', date, '类型:', typeof date)
  
  // 处理空值
  if (!date) return ''
  
  // 如果已经是 yyyy-MM-dd 格式，直接返回
  if (typeof date === 'string' && /^\d{4}-\d{2}-\d{2}$/.test(date)) {
    return date
  }
  
  // 如果是 ISO 格式 (yyyy-MM-ddTHH:mm:ss) 或其他包含时间的部分，只取日期部分
  if (typeof date === 'string' && date.includes('T')) {
    try {
      // 使用 dayjs 解析并格式化
      const formatted = dayjs(date).format('YYYY-MM-DD')
      console.log('ISO格式化结果:', formatted)
      return formatted
    } catch (error) {
      console.error('ISO日期格式化错误:', error)
      // 如果解析失败，尝试手动截取
      const datePart = date.split('T')[0]
      if (/^\d{4}-\d{2}-\d{2}$/.test(datePart)) {
        return datePart
      }
      return date
    }
  }
  
  // 处理其他可能的日期格式
  try {
    const formatted = dayjs(date).format('YYYY-MM-DD')
    console.log('通用格式化结果:', formatted)
    return formatted
  } catch (error) {
    console.error('通用日期格式化错误:', error)
    return date
  }
}

const filterOption = (input: string, option: any) => {
  return option.children.toLowerCase().includes(input.toLowerCase())
}

const handleSearch = (keyword: string) => {
  billStore.setFilters({ keyword })
  billStore.fetchBills()
}

const handleCategoryChange = (value: number | undefined) => {
  // -1 表示筛选未分类，undefined 表示不筛选（显示所有）
  // 直接传递 value 给后端，后端会处理 -1 的情况
  billStore.setFilters({ categoryId: value })
  billStore.setPagination(1, billStore.pagination.pageSize) // 重置到第一页
  billStore.fetchBills()
}

const handleTransactionTypeChange = (value: 'INCOME' | 'EXPENSE' | undefined) => {
  billStore.setFilters({ transactionType: value })
  billStore.setPagination(1, billStore.pagination.pageSize) // 重置到第一页
  billStore.fetchBills()
}

const handleDateRangeChange = (dates: [Dayjs, Dayjs] | null) => {
  if (dates && dates.length === 2) {
    billStore.setFilters({
      startDate: dates[0].format('YYYY-MM-DD'),
      endDate: dates[1].format('YYYY-MM-DD')
    })
  } else {
    // 清空日期范围
    billStore.setFilters({
      startDate: undefined,
      endDate: undefined
    })
  }
  billStore.setPagination(1, billStore.pagination.pageSize) // 重置到第一页
  billStore.fetchBills()
}

const handleAmountChange = () => {
  billStore.setFilters({
    minAmount: minAmount.value,
    maxAmount: maxAmount.value
  })
  billStore.setPagination(1, billStore.pagination.pageSize) // 重置到第一页
  billStore.fetchBills()
}

// 重置筛���条件
const resetFilters = () => {
  // 重置本地筛选状态
  selectedCategoryId.value = undefined
  selectedTransactionType.value = undefined
  dateRange.value = null
  minAmount.value = undefined
  maxAmount.value = undefined

  // 重置 store 中的筛选条件
  billStore.resetFilters()
}

const handleResetFilters = async () => {
  resetFilters()
  await billStore.fetchBills()
  message.success('已重置筛选条件')
}

const handleTableChange = (pagination: any, filters: any, sorter: any) => {
  billStore.setPagination(pagination.current, pagination.pageSize)
  billStore.fetchBills()
}

const handleCreate = () => {
  editMode.value = 'create'
  currentRecord.value = null
  resetForm()
  editModalVisible.value = true
}

const handleEdit = (record: Bill) => {
  editMode.value = 'edit'
  currentRecord.value = record
  formData.billDate = dayjs(record.billDate)
  formData.transactionType = record.transactionType
  formData.amount = record.amount
  formData.categoryId = record.categoryId
  formData.accountId = record.accountId
  formData.note = record.note || ''
  editModalVisible.value = true
}

const handleDelete = (record: Bill) => {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除这条账单吗？`,
    onOk: async () => {
      await billStore.deleteBill(record.billId)
    }
  })
}

const handleBatchDelete = () => {
  Modal.confirm({
    title: '批量删除',
    content: `确定要删除选中的 ${selectedRows.value.length} 条账单吗？`,
    onOk: async () => {
      const ids = selectedRows.value.map(row => row.billId)
      await billStore.batchDeleteBills(ids)
      selectedRows.value = []
    }
  })
}

const handleSelectionChange = (keys: any[], rows: Bill[]) => {
  selectedRows.value = rows
}

const handleSave = async () => {
  try {
    await formRef.value?.validate()
    saving.value = true
    
    // 确保必填字段有值
    if (!formData.billDate || formData.amount === undefined || 
        formData.categoryId === undefined || formData.accountId === undefined) {
      message.error('请填写所有必填字段')
      saving.value = false
      return
    }
    
    const data = {
      billDate: formData.billDate.format('YYYY-MM-DD'),
      transactionType: formData.transactionType,
      amount: formData.amount,
      categoryId: formData.categoryId,
      accountId: formData.accountId,
      note: formData.note
    }
    
    if (editMode.value === 'create') {
      await billStore.createBill(data as CreateBillDto)
    } else {
      await billStore.updateBill(currentRecord.value!.billId, data as UpdateBillDto)
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

const handleExport = () => {
  message.info('导出功能开发中...')
}

const resetForm = () => {
  formData.billDate = null
  formData.transactionType = 'EXPENSE'
  formData.amount = undefined
  formData.categoryId = undefined
  formData.accountId = undefined
  formData.note = ''
  formRef.value?.resetFields()
}

// 生命周期
onMounted(async () => {
  // 重置筛选条件
  resetFilters()

  await Promise.all([
    billStore.fetchBills(),
    categoryStore.fetchAllCategories(),
    accountStore.fetchAllAccounts()
  ])
})
</script>

<style scoped>
.bill-management {
  padding: 24px;
  padding-left: 70px; /* 为左上角的home图标留出空间 */
}

.filter-section {
  background: #fff;
  padding: 16px;
  border-radius: 6px;
  margin-bottom: 16px;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-item label {
  font-weight: 500;
  color: #333;
  white-space: nowrap;
}

.amount-text {
  font-weight: 600;
}

.amount-text.income {
  color: #52c41a;
}

.amount-text.expense {
  color: #ff4d4f;
}

@media (max-width: 768px) {
  .bill-management {
    padding: 16px;
    padding-left: 60px; /* 移动端为home图标留出空间 */
  }
}
</style>