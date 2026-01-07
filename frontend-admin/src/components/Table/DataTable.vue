<template>
  <div class="data-table">
    <!-- 搜索和操作区域 -->
    <div v-if="showSearch || showActions" class="table-header">
      <div class="search-pagination-area">
        <a-input-search
          v-if="showSearch"
          v-model:value="searchValue"
          :placeholder="searchPlaceholder"
          style="width: 300px"
          @search="handleSearch"
        />
        <a-space v-if="paginationConfig && paginationConfig.total > 0" :size="8" style="margin-left: 16px;">
          <a-button
            size="small"
            :disabled="paginationConfig.current <= 1"
            @click="handlePrevPage"
          >
            上一页
          </a-button>
          <a-button
            size="small"
            :disabled="paginationConfig.current >= Math.ceil(paginationConfig.total / paginationConfig.pageSize)"
            @click="handleNextPage"
          >
            下一页
          </a-button>
        </a-space>
      </div>
      <div class="action-area">
        <slot name="actions" :selected-rows="selectedRows" />
      </div>
    </div>
    
    <!-- 表格 -->
    <a-table
      v-bind="$attrs"
      :columns="computedColumns"
      :data-source="dataSource"
      :pagination="paginationConfig"
      :loading="loading"
      :row-selection="rowSelection"
      :scroll="scroll"
      @change="handleTableChange"
    >
      <!-- 动态插槽 -->
      <template v-for="column in columns" #[column.dataIndex]="{ record, index }" :key="column.dataIndex">
        <slot
          :name="column.dataIndex"
          :record="record"
          :value="record[column.dataIndex]"
          :index="index"
        >
          {{ record[column.dataIndex] }}
        </slot>
      </template>
      
      <!-- 操作列 -->
      <template v-if="showActions" #action="{ record, index }">
        <slot name="action" :record="record" :index="index">
          <a-space>
            <a-button type="link" size="small" @click="$emit('edit', record)">
              编辑
            </a-button>
            <a-popconfirm
              title="确定要删除这条记录吗？"
              @confirm="$emit('delete', record)"
            >
              <a-button type="link" danger size="small">
                删除
              </a-button>
            </a-popconfirm>
          </a-space>
        </slot>
      </template>
    </a-table>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import type { TableColumnsType, TableProps } from 'ant-design-vue'

interface Props {
  columns: TableColumnsType
  dataSource?: any[]
  loading?: boolean
  showSearch?: boolean
  searchPlaceholder?: string
  showActions?: boolean
  selectable?: boolean
  pagination?: TableProps['pagination']
  scroll?: TableProps['scroll']
}

const props = withDefaults(defineProps<Props>(), {
  dataSource: () => [],
  loading: false,
  showSearch: true,
  searchPlaceholder: '请输入搜索关键词',
  showActions: true,
  selectable: true,
  scroll: undefined
})

const emit = defineEmits<{
  search: [keyword: string]
  change: [pagination: any, filters: any, sorter: any]
  edit: [record: any]
  delete: [record: any]
  selectionChange: [selectedRowKeys: (string | number)[], selectedRows: any[]]
}>()

const searchValue = ref('')
const selectedRowKeys = ref<(string | number)[]>([])
const selectedRows = ref<any[]>([])

const computedColumns = computed(() => {
  const cols = [...props.columns]
  if (props.showActions && !cols.find(col => col.key === 'action')) {
    cols.push({
      title: '操作',
      key: 'action',
      fixed: 'right',
      width: 120,
      slots: { customRender: 'action' }
    })
  }
  return cols
})

const paginationConfig = computed(() => {
  if (props.pagination === false) return false

  const pageSize = Number(props.pagination?.pageSize) || 20
  const total = Number(props.pagination?.total) || 0
  const current = Number(props.pagination?.current) || 1

  return {
    current,
    pageSize,
    total,
    pageSizeOptions: ['10', '20', '50', '100', '200'],
    showSizeChanger: true,
    showQuickJumper: true,
    showTotal: (total: number, range: [number, number]) => {
      const totalPage = Math.ceil(total / pageSize)
      return `第 ${range[0]}-${range[1]} 条，共 ${total} 条 / ${totalPage} 页`
    },
    size: 'small'
  }
})

const rowSelection = computed(() => {
  if (!props.selectable) return undefined
  return {
    selectedRowKeys: selectedRowKeys.value,
    onChange: (keys: (string | number)[], rows: any[]) => {
      selectedRowKeys.value = keys
      selectedRows.value = rows
      emit('selectionChange', keys, rows)
    }
  }
})

const handleSearch = (value: string) => {
  emit('search', value)
}

const handleTableChange = (pagination: any, filters: any, sorter: any) => {
  emit('change', pagination, filters, sorter)
}

const handlePrevPage = () => {
  if (paginationConfig.value && paginationConfig.value.current > 1) {
    emit('change', {
      current: paginationConfig.value.current - 1,
      pageSize: paginationConfig.value.pageSize
    }, {}, {})
  }
}

const handleNextPage = () => {
  if (paginationConfig.value) {
    const totalPages = Math.ceil(paginationConfig.value.total / paginationConfig.value.pageSize)
    if (paginationConfig.value.current < totalPages) {
      emit('change', {
        current: paginationConfig.value.current + 1,
        pageSize: paginationConfig.value.pageSize
      }, {}, {})
    }
  }
}

defineExpose({
  selectedRowKeys,
  selectedRows
})
</script>

<style scoped>
.data-table {
  background: #fff;
  padding: 16px;
  border-radius: 6px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.search-pagination-area {
  display: flex;
  align-items: center;
}

.action-area {
  display: flex;
  gap: 8px;
}
</style>