<template>
  <div class="rule-management">
    <DataTable
      :columns="columns"
      :data-source="ruleStore.rules"
      :loading="ruleStore.loading"
      @search="handleSearch"
      @edit="handleEdit"
      @delete="handleDelete"
    >
      <template #actions>
        <a-space>
          <a-button type="primary" @click="handleCreate">
            <PlusOutlined />
            新增规则
          </a-button>
          <a-button @click="showTestModal">
            <ExperimentOutlined />
            测试规则
          </a-button>
        </a-space>
      </template>

      <template #transactionType="{ value }">
        <a-tag :color="value === 'INCOME' ? 'green' : 'red'">
          {{ value === 'INCOME' ? '收入' : '支出' }}
        </a-tag>
      </template>

      <template #keywords="{ value }">
        <a-tooltip :title="value">
          <span class="keywords-text">{{ value }}</span>
        </a-tooltip>
      </template>

      <template #amount="{ value }">
        <span v-if="value">¥{{ value }}</span>
        <span v-else class="no-amount">未设置</span>
      </template>
    </DataTable>

    <!-- 编辑弹窗 -->
    <a-modal
      v-model:open="editModalVisible"
      :title="editMode === 'create' ? '新增规则' : '编辑规则'"
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
        <a-form-item label="规则名称" name="ruleName">
          <a-input
            v-model:value="formData.ruleName"
            placeholder="请输入规则名称"
          />
        </a-form-item>
        
        <a-form-item label="关键词" name="keywords">
          <a-textarea
            v-model:value="formData.keywords"
            placeholder="请输入关键词，多个关键词用逗号分隔"
            :rows="3"
          />
        </a-form-item>
        
        <a-form-item label="交易类型" name="transactionType">
          <a-radio-group v-model:value="formData.transactionType">
            <a-radio value="EXPENSE">支出</a-radio>
            <a-radio value="INCOME">收入</a-radio>
          </a-radio-group>
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
        
        <a-form-item label="默认金额" name="amount">
          <a-input-number
            v-model:value="formData.amount"
            :min="0"
            :precision="2"
            style="width: 100%"
            placeholder="可选，不填则需要手动输入金额"
          />
        </a-form-item>
      </BaseForm>
    </a-modal>

    <!-- 测试规则弹窗 -->
    <a-modal
      v-model:open="testModalVisible"
      title="测试规则匹配"
      width="500px"
      @ok="handleTest"
      @cancel="testModalVisible = false"
      :confirm-loading="testing"
    >
      <a-form layout="vertical">
        <a-form-item label="测试文本">
          <a-textarea
            v-model:value="testText"
            placeholder="请输入要测试的记账文本，例如：今天午餐花了25元"
            :rows="3"
          />
        </a-form-item>
      </a-form>
      
      <div v-if="testResult" class="test-result">
        <a-divider>测试结果</a-divider>
        <a-descriptions :column="1" size="small">
          <a-descriptions-item label="是否匹配">
            <a-tag :color="testResult.matched ? 'green' : 'red'">
              {{ testResult.matched ? '匹配成功' : '未匹配' }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item v-if="testResult.matchedRule" label="匹配规则">
            {{ testResult.matchedRule.ruleName }}
          </a-descriptions-item>
          <a-descriptions-item label="结果说明">
            {{ testResult.message }}
          </a-descriptions-item>
        </a-descriptions>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { PlusOutlined, ExperimentOutlined } from '@ant-design/icons-vue'

import { DataTable, BaseForm } from '@/components'
import { useRuleStore, useCategoryStore } from '@/stores'
import type { Rule, CreateRuleDto, UpdateRuleDto } from '@/types'

const ruleStore = useRuleStore()
const categoryStore = useCategoryStore()

// 表格配置
const columns = [
  {
    title: '规则名称',
    dataIndex: 'ruleName',
    key: 'ruleName'
  },
  {
    title: '关键词',
    dataIndex: 'keywords',
    key: 'keywords',
    ellipsis: true
  },
  {
    title: '类型',
    dataIndex: 'transactionType',
    key: 'transactionType'
  },
  {
    title: '默认金额',
    dataIndex: 'amount',
    key: 'amount'
  },
  {
    title: '创建时间',
    dataIndex: 'crTime',
    key: 'crTime'
  }
]

// 响应式数据
const editModalVisible = ref(false)
const testModalVisible = ref(false)
const editMode = ref<'create' | 'edit'>('create')
const currentRecord = ref<Rule | null>(null)
const saving = ref(false)
const testing = ref(false)
const testText = ref('')
const testResult = ref<any>(null)
const formRef = ref()

const formData = reactive({
  ruleName: '',
  keywords: '',
  transactionType: 'EXPENSE',
  categoryId: undefined as number | undefined,
  amount: undefined as number | undefined
})

const formRules = {
  ruleName: [
    { required: true, message: '请输入规则名称' },
    { min: 1, max: 50, message: '规则名称长度在1-50个字符' }
  ],
  keywords: [
    { required: true, message: '请输入关键词' },
    { min: 1, max: 200, message: '关键词长度在1-200个字符' }
  ],
  transactionType: [{ required: true, message: '请选择交易类型' }],
  categoryId: [{ required: true, message: '请选择分类' }]
}

// 方法
const filterOption = (input: string, option: any) => {
  return option.children.toLowerCase().includes(input.toLowerCase())
}

const handleSearch = (keyword: string) => {
  ruleStore.setFilters({ keyword })
  ruleStore.fetchRules()
}

const handleCreate = () => {
  editMode.value = 'create'
  currentRecord.value = null
  resetForm()
  editModalVisible.value = true
}

const handleEdit = (record: Rule) => {
  editMode.value = 'edit'
  currentRecord.value = record
  formData.ruleName = record.ruleName
  formData.keywords = record.keywords
  formData.transactionType = record.transactionType
  formData.categoryId = record.categoryId !== null ? record.categoryId : undefined
  formData.amount = record.amount !== null ? record.amount : undefined
  editModalVisible.value = true
}

const handleDelete = (record: Rule) => {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除规则 "${record.ruleName}" 吗？`,
    onOk: async () => {
      await ruleStore.deleteRule(record.ruleId)
    }
  })
}

const handleSave = async () => {
  try {
    await formRef.value?.validate()
    saving.value = true
    
    // 确保必填字段有值
    if (formData.categoryId === undefined) {
      message.error('请选择分类')
      saving.value = false
      return
    }
    
    const data = {
      ruleName: formData.ruleName,
      keywords: formData.keywords,
      transactionType: formData.transactionType,
      categoryId: formData.categoryId,
      amount: formData.amount
    }
    
    if (editMode.value === 'create') {
      await ruleStore.createRule(data as CreateRuleDto)
    } else {
      await ruleStore.updateRule(currentRecord.value!.ruleId, data as UpdateRuleDto)
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

const showTestModal = () => {
  testText.value = ''
  testResult.value = null
  testModalVisible.value = true
}

const handleTest = async () => {
  if (!testText.value.trim()) {
    message.warning('请输入测试文本')
    return
  }
  
  try {
    testing.value = true
    const result = await ruleStore.testRule({ text: testText.value })
    testResult.value = result
  } catch (error) {
    console.error('测试失败:', error)
  } finally {
    testing.value = false
  }
}

const resetForm = () => {
  formData.ruleName = ''
  formData.keywords = ''
  formData.transactionType = 'EXPENSE'
  formData.categoryId = undefined
  formData.amount = undefined
  formRef.value?.resetFields()
}

// 生命周期
onMounted(async () => {
  await Promise.all([
    ruleStore.fetchRules(),
    categoryStore.fetchAllCategories()
  ])
})
</script>

<style scoped>
.rule-management {
  padding: 24px;
  padding-left: 70px; /* 为左上角的home图标留出空间 */
}

.keywords-text {
  display: inline-block;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.no-amount {
  color: #8c8c8c;
  font-style: italic;
}

.test-result {
  margin-top: 16px;
}

@media (max-width: 768px) {
  .rule-management {
    padding: 16px;
    padding-left: 60px; /* 移动端为home图标留出空间 */
  }
}
</style>