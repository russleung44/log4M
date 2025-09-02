<template>
  <div class="form-container">
    <a-form
      ref="formRef"
      :model="form"
      :rules="rules"
      :layout="layout"
      :label-col="labelCol"
      :wrapper-col="wrapperCol"
      @finish="handleSubmit"
      @finishFailed="handleSubmitFailed"
    >
      <slot :form="form" />
      
      <a-form-item v-if="showButtons" :wrapper-col="buttonWrapperCol">
        <a-space>
          <a-button 
            type="primary" 
            html-type="submit" 
            :loading="loading"
            :disabled="disabled"
          >
            {{ submitText }}
          </a-button>
          <a-button @click="handleCancel" :disabled="loading">
            {{ cancelText }}
          </a-button>
          <a-button v-if="showReset" @click="handleReset" :disabled="loading">
            {{ resetText }}
          </a-button>
        </a-space>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import type { FormInstance, FormProps } from 'ant-design-vue'

interface Props {
  form: Record<string, any>
  rules?: Record<string, any>
  layout?: FormProps['layout']
  labelCol?: FormProps['labelCol']
  wrapperCol?: FormProps['wrapperCol']
  loading?: boolean
  disabled?: boolean
  showButtons?: boolean
  showReset?: boolean
  submitText?: string
  cancelText?: string
  resetText?: string
}

const props = withDefaults(defineProps<Props>(), {
  layout: 'horizontal',
  labelCol: () => ({ span: 6 }),
  wrapperCol: () => ({ span: 18 }),
  loading: false,
  disabled: false,
  showButtons: true,
  showReset: false,
  submitText: '提交',
  cancelText: '取消',
  resetText: '重置'
})

const emit = defineEmits<{
  submit: [values: any]
  cancel: []
  reset: []
}>()

const formRef = ref<FormInstance>()

const buttonWrapperCol = computed(() => {
  if (props.layout === 'vertical') {
    return { span: 24 }
  }
  return {
    span: props.wrapperCol?.span || 18,
    offset: props.labelCol?.span || 6
  }
})

const handleSubmit = (values: any) => {
  emit('submit', values)
}

const handleSubmitFailed = (errorInfo: any) => {
  console.log('Form validation failed:', errorInfo)
}

const handleCancel = () => {
  emit('cancel')
}

const handleReset = () => {
  formRef.value?.resetFields()
  emit('reset')
}

const validate = () => {
  return formRef.value?.validate()
}

const resetFields = () => {
  formRef.value?.resetFields()
}

const clearValidate = () => {
  formRef.value?.clearValidate()
}

defineExpose({
  validate,
  resetFields,
  clearValidate,
  formRef
})
</script>

<style scoped>
.form-container {
  background: #fff;
  padding: 24px;
  border-radius: 6px;
}
</style>