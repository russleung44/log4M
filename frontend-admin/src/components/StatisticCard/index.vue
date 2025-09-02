<template>
  <a-card :loading="loading" class="statistic-card">
    <template #title>
      <div class="card-title">
        <a-avatar :style="{ backgroundColor: color }" :size="32">
          <template #icon>
            <component :is="icon" />
          </template>
        </a-avatar>
        <span class="title-text">{{ title }}</span>
      </div>
    </template>
    
    <div class="statistic-content">
      <div class="main-value">
        <a-statistic
          :value="value"
          :precision="precision"
          :suffix="suffix"
          :prefix="prefix"
          :value-style="valueStyle"
        />
      </div>
      
      <div v-if="trend !== undefined" class="trend-info">
        <span :class="['trend-indicator', trendClass]">
          <arrow-up-outlined v-if="trend > 0" />
          <arrow-down-outlined v-if="trend < 0" />
          <minus-outlined v-if="trend === 0" />
          {{ Math.abs(trend) }}%
        </span>
        <span class="trend-label">{{ trendLabel }}</span>
      </div>
      
      <div v-if="extra" class="extra-info">
        <slot name="extra">
          <span class="extra-text">{{ extra }}</span>
        </slot>
      </div>
    </div>
  </a-card>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ArrowUpOutlined, ArrowDownOutlined, MinusOutlined } from '@ant-design/icons-vue'

interface Props {
  title: string
  value: number | string
  icon?: any
  color?: string
  precision?: number
  suffix?: string
  prefix?: string
  trend?: number
  trendLabel?: string
  extra?: string
  loading?: boolean
  valueStyle?: Record<string, any>
}

const props = withDefaults(defineProps<Props>(), {
  color: '#1890ff',
  precision: 0,
  loading: false,
  trendLabel: '较上期',
  valueStyle: () => ({})
})

const trendClass = computed(() => {
  if (props.trend === undefined) return ''
  if (props.trend > 0) return 'trend-up'
  if (props.trend < 0) return 'trend-down'
  return 'trend-stable'
})
</script>

<style scoped>
.statistic-card {
  height: 100%;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.title-text {
  font-size: 16px;
  font-weight: 500;
  color: rgba(0, 0, 0, 0.85);
}

.statistic-content {
  padding: 8px 0;
}

.main-value {
  margin-bottom: 12px;
}

.trend-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.trend-indicator {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  font-weight: 500;
}

.trend-up {
  color: #52c41a;
}

.trend-down {
  color: #ff4d4f;
}

.trend-stable {
  color: #8c8c8c;
}

.trend-label {
  font-size: 12px;
  color: rgba(0, 0, 0, 0.45);
}

.extra-info {
  margin-top: 8px;
}

.extra-text {
  font-size: 12px;
  color: rgba(0, 0, 0, 0.45);
}
</style>