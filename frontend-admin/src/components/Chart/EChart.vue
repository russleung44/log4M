<template>
  <div ref="chartRef" :style="{ width: '100%', height }" class="chart-container"></div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import * as echarts from 'echarts'

interface Props {
  option: echarts.EChartsOption
  height?: string
  loading?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  height: '400px',
  loading: false
})

const emit = defineEmits<{
  chartClick: [params: echarts.ECElementEvent]
}>()

const chartRef = ref<HTMLDivElement>()
let chartInstance: echarts.ECharts | null = null

const initChart = () => {
  if (!chartRef.value) return
  
  chartInstance = echarts.init(chartRef.value)
  chartInstance.setOption(props.option)
  
  // 添加点击事件监听
  chartInstance.on('click', (params) => {
    emit('chartClick', params)
  })
}

const updateChart = () => {
  if (chartInstance) {
    chartInstance.setOption(props.option, true)
  }
}

const resizeChart = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}

const showLoading = () => {
  if (chartInstance) {
    chartInstance.showLoading('default', {
      text: '加载中...',
      color: '#1890ff',
      textColor: '#000',
      maskColor: 'rgba(255, 255, 255, 0.8)',
      zlevel: 0
    })
  }
}

const hideLoading = () => {
  if (chartInstance) {
    chartInstance.hideLoading()
  }
}

watch(
  () => props.option,
  () => {
    updateChart()
  },
  { deep: true }
)

watch(
  () => props.loading,
  (loading) => {
    if (loading) {
      showLoading()
    } else {
      hideLoading()
    }
  }
)

onMounted(async () => {
  await nextTick()
  initChart()
  
  if (props.loading) {
    showLoading()
  }
  
  // 监听窗口大小变化
  window.addEventListener('resize', resizeChart)
})

onUnmounted(() => {
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
  window.removeEventListener('resize', resizeChart)
})

// 暴露方法给父组件
defineExpose({
  getChart: () => chartInstance,
  resize: resizeChart,
  showLoading,
  hideLoading
})
</script>

<style scoped>
.chart-container {
  position: relative;
}
</style>