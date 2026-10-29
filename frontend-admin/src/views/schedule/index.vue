<template>
  <div class="schedule-management">
    <DataTable
      :columns="columns"
      :data-source="scheduleStore.schedules"
      :loading="scheduleStore.loading"
      :show-search="false"
      :selectable="false"
      :pagination="false"
      @edit="handleEdit"
      @delete="handleDelete"
    >
      <template #actions>
        <a-button type="primary" @click="handleCreate">
          <PlusOutlined />
          新增定时
        </a-button>
      </template>

      <template #execTime="{ value }">
        {{ value ? value.slice(0, 5) : '' }}
      </template>

      <template #weekDays="{ value }">
        {{ describeDays(value) }}
      </template>

      <template #amount="{ value }">
        <span>¥{{ value }}</span>
      </template>

      <template #enabled="{ record }">
        <a-switch
          :checked="record.enabled"
          checked-children="启用"
          un-checked-children="停用"
          @change="scheduleStore.toggleSchedule(record)"
        />
      </template>
    </DataTable>

    <!-- 编辑弹窗 -->
    <a-modal
      v-model:open="editModalVisible"
      :title="editMode === 'create' ? '新增定时记账' : '编辑定时记账'"
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
      >
        <a-form-item label="触发时间" name="execTime">
          <a-time-picker
            v-model:value="formData.execTime"
            value-format="HH:mm"
            format="HH:mm"
            placeholder="请选择触发时间"
            style="width: 100%"
          />
        </a-form-item>

        <a-form-item label="重复" name="weekDays">
          <a-select
            v-model:value="formData.weekDays"
            mode="multiple"
            placeholder="请选择周几（可多选）"
            :options="weekDayOptions"
          />
        </a-form-item>

        <a-form-item label="金额" name="amount">
          <a-input-number
            v-model:value="formData.amount"
            :min="0.01"
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

        <a-form-item label="备注" name="note">
          <a-input
            v-model:value="formData.note"
            placeholder="请输入账单备注"
          />
        </a-form-item>
      </BaseForm>

      <a-alert
        message="到点后自动记账，并通过 Telegram 推送通知；通知 chatId 取自机器人中最近使用的用户。"
        type="info"
        show-icon
        style="margin-top: 8px"
      />
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { PlusOutlined } from '@ant-design/icons-vue'

import { DataTable, BaseForm } from '@/components'
import { useScheduleStore, useCategoryStore } from '@/stores'
import type { BillSchedule, SaveBillScheduleDto } from '@/types'

const scheduleStore = useScheduleStore()
const categoryStore = useCategoryStore()

// 表格配置
const columns = [
  {
    title: '触发时间',
    dataIndex: 'execTime',
    key: 'execTime',
    width: 100
  },
  {
    title: '重复',
    dataIndex: 'weekDays',
    key: 'weekDays',
    width: 120
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
    width: 120
  },
  {
    title: '备注',
    dataIndex: 'note',
    key: 'note'
  },
  {
    title: '状态',
    dataIndex: 'enabled',
    key: 'enabled',
    width: 90
  }
]

const weekDayOptions = [
  { label: '周一', value: '1' },
  { label: '周二', value: '2' },
  { label: '周三', value: '3' },
  { label: '周四', value: '4' },
  { label: '周五', value: '5' },
  { label: '周六', value: '6' },
  { label: '周日', value: '7' }
]

// 响应式数据
const editModalVisible = ref(false)
const editMode = ref<'create' | 'edit'>('create')
const currentRecord = ref<BillSchedule | null>(null)
const saving = ref(false)
const formRef = ref()

const formData = reactive({
  execTime: '',
  weekDays: [] as string[],
  amount: undefined as number | undefined,
  categoryId: undefined as number | undefined,
  note: ''
})

const formRules = {
  execTime: [{ required: true, message: '请选择触发时间' }],
  weekDays: [
    {
      required: true,
      validator: (_rule: unknown, value: string[]) =>
        value && value.length > 0 ? Promise.resolve() : Promise.reject('请选择周几')
    }
  ],
  amount: [{ required: true, message: '请输入金额' }],
  categoryId: [{ required: true, message: '请选择分类' }],
  note: [{ required: true, message: '请输入账单备注' }]
}

// 方法
const describeDays = (days: string) => {
  if (days === '12345') return '工作日'
  if (days === '1234567') return '每天'
  if (days === '67') return '周末'
  return days.split('').map(d => weekDayOptions[Number(d) - 1]?.label || d).join('、')
}

const filterOption = (input: string, option: any) => {
  return option.children.toLowerCase().includes(input.toLowerCase())
}

const handleCreate = () => {
  editMode.value = 'create'
  currentRecord.value = null
  resetForm()
  editModalVisible.value = true
}

const handleEdit = (record: BillSchedule) => {
  editMode.value = 'edit'
  currentRecord.value = record
  formData.execTime = record.execTime.slice(0, 5)
  formData.weekDays = record.weekDays.split('')
  formData.amount = record.amount
  formData.categoryId = record.categoryId
  formData.note = record.note
  editModalVisible.value = true
}

const handleDelete = (record: BillSchedule) => {
  scheduleStore.deleteSchedule(record.scheduleId)
}

const handleSave = async () => {
  try {
    await formRef.value?.validate()
    saving.value = true

    if (formData.categoryId === undefined || formData.amount === undefined) {
      message.error('请完善表单')
      saving.value = false
      return
    }

    const data: SaveBillScheduleDto = {
      note: formData.note,
      categoryId: formData.categoryId,
      amount: formData.amount,
      execTime: formData.execTime,
      weekDays: [...formData.weekDays].sort().join(''),
      enabled: currentRecord.value?.enabled ?? true
    }

    const saved = editMode.value === 'create'
      ? await scheduleStore.createSchedule(data)
      : await scheduleStore.updateSchedule(currentRecord.value!.scheduleId, data)
    if (!saved) {
      return // 保存失败：保留输入，弹窗不关闭
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
  formData.execTime = ''
  formData.weekDays = []
  formData.amount = undefined
  formData.categoryId = undefined
  formData.note = ''
  formRef.value?.resetFields()
}

// 生命周期
onMounted(async () => {
  await Promise.all([
    scheduleStore.fetchSchedules(),
    categoryStore.fetchAllCategories()
  ])
})
</script>

<style scoped>
.schedule-management {
  padding: 24px;
  padding-left: 70px; /* 为左上角的home图标留出空间 */
}

@media (max-width: 768px) {
  .schedule-management {
    padding: 16px;
    padding-left: 60px; /* 移动端为home图标留出空间 */
  }
}
</style>
