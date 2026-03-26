<template>
  <div class="trash-management">
    <div class="header-section">
      <h2>账单回收站</h2>
      <p class="hint">已删除的账单将在回收站保留，您可以恢复或彻底删除它们</p>
    </div>

    <DataTable
      :columns="columns"
      :data-source="trashBills"
      :loading="loading"
      :pagination="paginationConfig"
      @change="handleTableChange"
      @selection-change="handleSelectionChange"
      :show-search="false"
    >
      <template #actions="{ selectedRows }">
        <a-space>
          <a-button
            v-if="selectedRows.length > 0"
            type="primary"
            @click="handleBatchRestore"
          >
            恢复选中 ({{ selectedRows.length }})
          </a-button>
          <a-button
            v-if="selectedRows.length > 0"
            danger
            @click="handleBatchPermanentDelete"
          >
            彻底删除 ({{ selectedRows.length }})
          </a-button>
          <a-button
            v-if="trashBills.length > 0"
            danger
            @click="handleEmptyTrash"
          >
            清空回收站
          </a-button>
        </a-space>
      </template>

      <template #mdTime="{ value }">
        {{ formatDateTime(value) }}
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

      <template #action="{ record }">
        <a-space>
          <a-button type="link" size="small" @click="handleRestore(record)">
            恢复
          </a-button>
          <a-popconfirm
            title="彻底删除后无法恢复，确定要删除吗？"
            @confirm="handlePermanentDelete(record)"
          >
            <a-button type="link" danger size="small">
              彻底删除
            </a-button>
          </a-popconfirm>
        </a-space>
      </template>
    </DataTable>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import dayjs from 'dayjs'

import { DataTable } from '@/components'
import { BillApi } from '@/api'
import type { Bill, PageResult } from '@/types'

const trashBills = ref<Bill[]>([])
const loading = ref(false)
const selectedRows = ref<Bill[]>([])
const pagination = ref({
  current: 1,
  pageSize: 20,
  total: 0
})

const columns = [
  {
    title: '删除时间',
    dataIndex: 'mdTime',
    key: 'mdTime',
    width: 180
  },
  {
    title: '日期',
    dataIndex: 'billDate',
    key: 'billDate',
    width: 120
  },
  {
    title: '备注',
    dataIndex: 'note',
    key: 'note',
    ellipsis: true,
    width: 150
  },
  {
    title: '金额',
    dataIndex: 'amount',
    key: 'amount',
    width: 100
  },
  {
    title: '分类',
    dataIndex: 'categoryName',
    key: 'categoryName',
    width: 100
  },
  {
    title: '类型',
    dataIndex: 'transactionType',
    key: 'transactionType',
    width: 80
  }
]

const paginationConfig = computed(() => ({
  current: pagination.value.current,
  pageSize: pagination.value.pageSize,
  total: pagination.value.total
}))

const formatDate = (date: string) => {
  if (!date) return ''
  if (typeof date === 'string' && /^\d{4}-\d{2}-\d{2}$/.test(date)) {
    return date
  }
  return dayjs(date).format('YYYY-MM-DD')
}

const formatDateTime = (datetime: string) => {
  if (!datetime) return ''
  return dayjs(datetime).format('YYYY-MM-DD HH:mm')
}

const fetchTrashBills = async () => {
  try {
    loading.value = true
    const response = await BillApi.getTrashBills({
      current: pagination.value.current,
      size: pagination.value.pageSize
    })

    if (response.code === 200 || response.code === 0) {
      const pageData = response.data as PageResult<Bill>
      trashBills.value = pageData.records
      pagination.value.total = pageData.total
    } else {
      message.error(response.message || '获取回收站列表失败')
    }
  } catch (error) {
    console.error('Failed to fetch trash bills:', error)
    message.error('获取回收站列表失败')
  } finally {
    loading.value = false
  }
}

const handleTableChange = (pag: any) => {
  pagination.value.current = pag.current
  pagination.value.pageSize = pag.pageSize
  fetchTrashBills()
}

const handleSelectionChange = (keys: any[], rows: Bill[]) => {
  selectedRows.value = rows
}

const handleRestore = async (record: Bill) => {
  try {
    loading.value = true
    const response = await BillApi.restoreBill(record.billId)
    if (response.code === 200 || response.code === 0) {
      message.success('恢复成功')
      await fetchTrashBills()
    } else {
      message.error(response.message || '恢复失败')
    }
  } catch (error) {
    console.error('Failed to restore bill:', error)
    message.error('恢复失败')
  } finally {
    loading.value = false
  }
}

const handlePermanentDelete = async (record: Bill) => {
  try {
    loading.value = true
    const response = await BillApi.permanentDeleteBill(record.billId)
    if (response.code === 200 || response.code === 0) {
      message.success('已彻底删除')
      await fetchTrashBills()
    } else {
      message.error(response.message || '删除失败')
    }
  } catch (error) {
    console.error('Failed to permanent delete bill:', error)
    message.error('删除失败')
  } finally {
    loading.value = false
  }
}

const handleBatchRestore = async () => {
  Modal.confirm({
    title: '批量恢复',
    content: `确定要恢复选中的 ${selectedRows.value.length} 条账单吗？`,
    onOk: async () => {
      try {
        loading.value = true
        const ids = selectedRows.value.map(row => row.billId)
        const response = await BillApi.batchRestoreBills(ids)
        if (response.code === 200 || response.code === 0) {
          message.success(`成功恢复 ${ids.length} 条账单`)
          selectedRows.value = []
          await fetchTrashBills()
        } else {
          message.error(response.message || '批量恢复失败')
        }
      } catch (error) {
        console.error('Failed to batch restore bills:', error)
        message.error('批量恢复失败')
      } finally {
        loading.value = false
      }
    }
  })
}

const handleBatchPermanentDelete = () => {
  Modal.confirm({
    title: '批量彻底删除',
    content: `彻底删除后无法恢复，确定要删除选中的 ${selectedRows.value.length} 条账单吗？`,
    okType: 'danger',
    onOk: async () => {
      try {
        loading.value = true
        const ids = selectedRows.value.map(row => row.billId)
        const response = await BillApi.batchPermanentDeleteBills(ids)
        if (response.code === 200 || response.code === 0) {
          message.success(`已彻底删除 ${ids.length} 条账单`)
          selectedRows.value = []
          await fetchTrashBills()
        } else {
          message.error(response.message || '批量删除失败')
        }
      } catch (error) {
        console.error('Failed to batch permanent delete bills:', error)
        message.error('批量删除失败')
      } finally {
        loading.value = false
      }
    }
  })
}

const handleEmptyTrash = () => {
  Modal.confirm({
    title: '清空回收站',
    content: '清空回收站后所有账单将被彻底删除，无法恢复。确定要清空吗？',
    okType: 'danger',
    onOk: async () => {
      try {
        loading.value = true
        // 获取所有回收站账单的ID
        const allIds = trashBills.value.map(bill => bill.billId)
        if (allIds.length === 0) {
          message.info('回收站已为空')
          return
        }
        const response = await BillApi.batchPermanentDeleteBills(allIds)
        if (response.code === 200 || response.code === 0) {
          message.success('回收站已清空')
          selectedRows.value = []
          await fetchTrashBills()
        } else {
          message.error(response.message || '清空失败')
        }
      } catch (error) {
        console.error('Failed to empty trash:', error)
        message.error('清空失败')
      } finally {
        loading.value = false
      }
    }
  })
}

onMounted(() => {
  fetchTrashBills()
})
</script>

<style scoped>
.trash-management {
  padding: 24px;
  padding-left: 70px;
}

.header-section {
  margin-bottom: 24px;
}

.header-section h2 {
  margin-bottom: 8px;
}

.hint {
  color: #999;
  font-size: 14px;
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
  .trash-management {
    padding: 16px;
    padding-left: 60px;
  }
}
</style>