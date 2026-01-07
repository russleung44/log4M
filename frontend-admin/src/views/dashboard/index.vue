<template>
  <div class="dashboard">
    <a-typography-title :level="2">log4M 管理后台</a-typography-title>
    <a-typography-paragraph>
      欢迎使用 log4M 个人记账系统管理后台！
    </a-typography-paragraph>
    
    <!-- 统计卡片区域 -->
    <a-row :gutter="[16, 16]" class="stats-row">
      <a-col :xs="24" :sm="12" :lg="6">
        <StatisticCard
          title="昨日支出"
          :value="yesterdayExpense"
          :icon="CalendarOutlined"
          color="#ff4d4f"
          suffix="元"
          :precision="2"
          :loading="statsLoading"
        />
      </a-col>

      <a-col :xs="24" :sm="12" :lg="6">
        <StatisticCard
          title="本月支出"
          :value="monthExpense"
          :icon="CalendarOutlined"
          color="#ff4d4f"
          suffix="元"
          :precision="2"
          :loading="statsLoading"
        />
      </a-col>

      <a-col :xs="24" :sm="12" :lg="6">
        <StatisticCard
          title="上月支出"
          :value="lastMonthExpense"
          :icon="CalendarOutlined"
          color="#ff4d4f"
          suffix="元"
          :precision="2"
          :loading="statsLoading"
        />
      </a-col>

      <a-col :xs="24" :sm="12" :lg="6">
        <StatisticCard
          title="本年支出"
          :value="yearExpense"
          :icon="CalendarOutlined"
          color="#ff4d4f"
          suffix="元"
          :precision="2"
          :loading="statsLoading"
        />
      </a-col>
    </a-row>
    
    <!-- 图表区域 -->
    <a-row :gutter="[16, 16]" class="charts-row">
      <a-col :xs="24" :lg="12">
        <a-card title="最近7天收支趋势" :loading="chartsLoading">
          <EChart
            ref="trendChartRef"
            :option="trendChartOption"
            height="300px"
            :loading="chartsLoading"
            @chart-click="handleTrendClick"
            :key="`trend-chart-${trendChartKey}`"
          />
        </a-card>
      </a-col>

      <a-col :xs="24" :lg="12">
        <a-card :loading="categoryChartLoading">
          <template #title>
            <div class="chart-title">
              <span>支出分类</span>
              <a-date-picker
                v-model:value="selectedMonth"
                picker="month"
                size="small"
                :allowClear="false"
                :disabled="categoryChartLoading"
                @change="handleMonthChange"
                class="month-picker"
              />
            </div>
          </template>
          <a-row :gutter="16">
            <a-col :span="14">
              <EChart
                ref="categoryChartRef"
                :option="categoryChartOption"
                height="300px"
                :loading="categoryChartLoading"
                @chart-click="handleCategoryClick"
                :key="`category-chart-${categoryChartKey}`"
              />
            </a-col>
            <a-col :span="10">
              <div class="category-data-container">
                <!-- 总支出统计 -->
                <div class="category-total-item">
                  <span class="category-total-label">总支出</span>
                  <span class="category-total-amount">¥{{ formatAmount(totalCategoryExpense) }}</span>
                </div>
                <!-- 分类支出数据 -->
                <div
                  v-for="item in categoryData"
                  :key="item.name"
                  class="category-data-item"
                >
                  <span class="category-name">{{ item.name }}</span>
                  <span class="category-amount">¥{{ formatAmount(item.value) }}</span>
                </div>
              </div>
            </a-col>
          </a-row>
        </a-card>
      </a-col>
    </a-row>

    <!-- 年度支出趋势 -->
    <a-row :gutter="[16, 16]" class="charts-row">
      <a-col :xs="24" :lg="24">
        <a-card :loading="yearlyChartLoading">
          <template #title>
            <div class="chart-title">
              <span>年度支出趋势</span>
              <a-date-picker
                v-model:value="selectedYear"
                picker="year"
                size="small"
                :allowClear="false"
                :disabled="yearlyChartLoading"
                @change="handleYearChange"
                class="year-picker"
              />
            </div>
          </template>
          <EChart
            ref="yearlyChartRef"
            :option="yearlyChartOption"
            height="350px"
            :loading="yearlyChartLoading"
            @chart-click="handleYearlyClick"
            :key="`yearly-chart-${yearlyChartKey}`"
          />
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  ArrowUpOutlined,
  ArrowDownOutlined,
  WalletOutlined,
  TransactionOutlined,
  CalendarOutlined
} from '@ant-design/icons-vue'
import dayjs, { Dayjs } from 'dayjs'
import type { EChartsOption } from 'echarts'

import EChart from '@/components/Chart/EChart.vue'
import StatisticCard from '@/components/StatisticCard/index.vue'
import { useBillStore, useCategoryStore } from '@/stores'
import { BillApi } from '@/api'
import type { Bill, TrendStatistics, CategoryStatistics, YearlyMonthlyStatistics } from '@/types'
import { TransactionType } from '@/types'

const router = useRouter()
const billStore = useBillStore()
const categoryStore = useCategoryStore()

// 响应式数据
const statsLoading = ref(false)
const chartsLoading = ref(false)
const categoryChartLoading = ref(false) // 专门用于分类图表的加载状态
const yearlyChartLoading = ref(false) // 年度图表加载状态
const trendChartKey = ref(0) // 用于强制重新渲染趋势图表
const categoryChartKey = ref(0) // 用于强制重新渲染分类图表
const yearlyChartKey = ref(0) // 用于强制重新渲染年度图表

// 新的支���统计数据
const yesterdayExpense = ref(0)
const monthExpense = ref(0)
const lastMonthExpense = ref(0)
const yearExpense = ref(0)

const trendData = ref<TrendStatistics[]>([])
const categoryData = ref<{ name: string; value: number }[]>([])
const yearlyData = ref<YearlyMonthlyStatistics[]>([])

// 月份和年份选择器数据
const selectedMonth = ref<Dayjs>(dayjs())
const selectedYear = ref<Dayjs>(dayjs())

// 图表引用
const trendChartRef = ref()
const categoryChartRef = ref()
const yearlyChartRef = ref()

// 计算总分类支出
const totalCategoryExpense = computed(() => {
  return categoryData.value.reduce((total, item) => total + item.value, 0)
})

// 图表配置
const trendChartOption = computed<EChartsOption>(() => {
  // console.log('趋势图表数据更新:', trendData.value) // 调试信息
  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
        label: {
          backgroundColor: '#6a7985'
        }
      }
    },
    legend: {
      data: ['收入', '支出']
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: trendData.value.map(item => dayjs(item.date).format('MM-DD'))
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '收入',
        type: 'line',
        stack: 'Total',
        data: trendData.value.map(item => item.income),
        itemStyle: { color: '#52c41a' }
      },
      {
        name: '支出',
        type: 'line',
        stack: 'Total',
        data: trendData.value.map(item => item.expense),
        itemStyle: { color: '#ff4d4f' }
      }
    ]
  }
})

const categoryChartOption = computed<EChartsOption>(() => {
  // console.log('分类图表数据更新:', categoryData.value) // 调试信息
  return {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '支出分类',
        type: 'pie',
        radius: '50%',
        data: categoryData.value,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
})

const yearlyChartOption = computed<EChartsOption>(() => {
  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
        label: {
          backgroundColor: '#6a7985'
        }
      }
    },
    legend: {
      data: ['收入', '支出']
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: yearlyData.value.map(item => `${item.month}月`)
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '¥{value}'
      }
    },
    series: [
      {
        name: '收入',
        type: 'line',
        data: yearlyData.value.map(item => item.income),
        smooth: true,
        itemStyle: { color: '#52c41a' },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(82, 196, 26, 0.3)' },
              { offset: 1, color: 'rgba(82, 196, 26, 0.05)' }
            ]
          }
        }
      },
      {
        name: '支出',
        type: 'line',
        data: yearlyData.value.map(item => item.expense),
        smooth: true,
        itemStyle: { color: '#ff4d4f' },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(255, 77, 79, 0.3)' },
              { offset: 1, color: 'rgba(255, 77, 79, 0.05)' }
            ]
          }
        }
      }
    ]
  }
})

// 方法
const navigateTo = (path: string) => {
  // 现在可以正常跳转到已实现的页面
  if (path === '/bills' || path === '/categories' || path === '/rules') {
    router.push(path)
  } else if (path.includes('/create')) {
    message.info(`即将跳转到 ${path}，该功能正在开发中...`)
  } else {
    message.info(`即将跳转到 ${path}，该功能正在开发中...`)
  }
}

const handleTrendClick = (params: any) => {
  console.log('趋势图点击：', params)
  message.info(`点击了 ${params.name} 的数据`)
}

const handleCategoryClick = (params: any) => {
  console.log('分类图点击：', params)
  message.info(`点击了分类：${params.name}`)
}

const handleYearlyClick = (params: any) => {
  console.log('年度图点击：', params)
  message.info(`点击了 ${params.name} 的数据`)
}

const formatDate = (date: string) => {
  return dayjs(date).format('YYYY-MM-DD')
}

const formatAmount = (amount: number) => {
  return amount.toFixed(2);
};

// 月份改变处理函数
const handleMonthChange = async () => {
  console.log('月份切换到:', selectedMonth.value.format('YYYY-MM')) // 调试信息
  await loadCategoryData()
  // 只增加分类图表的key
  categoryChartKey.value++
  console.log('月份切换后更新categoryChartKey:', categoryChartKey.value) // 调试信息

  // 手动触发分类图表更新
  await nextTick()
  if (categoryChartRef.value) {
    console.log('手动更新分类图表')
    categoryChartRef.value.updateChart()
  }
}

// 年份改变处理函数
const handleYearChange = async () => {
  console.log('年份切换到:', selectedYear.value.format('YYYY')) // 调试信息
  await loadYearlyData()
  // 增加年度图表的key
  yearlyChartKey.value++
  console.log('年份切换后更新yearlyChartKey:', yearlyChartKey.value) // 调试信息

  // 手动触发年度图表更新
  await nextTick()
  if (yearlyChartRef.value) {
    console.log('手动更新年度图表')
    yearlyChartRef.value.updateChart()
  }
}

// 更新加载统计的方法名称
const loadExpenseStats = async () => {
  try {
    statsLoading.value = true

    // 获取需要的日期格式
    const yesterday = dayjs().subtract(1, 'day').format('YYYY-MM-DD')
    const currentMonth = dayjs().format('YYYY-MM')
    const lastMonth = dayjs().subtract(1, 'month').format('YYYY-MM')
    const currentYear = dayjs().format('YYYY')

    // 并行获取所有统计数据
    const [yesterdayResponse, monthResponse, lastMonthResponse, yearResponse] = await Promise.all([
      BillApi.getDailyStatistics(yesterday),
      BillApi.getMonthlyStatistics(currentMonth),
      BillApi.getMonthlyStatistics(lastMonth),
      BillApi.getYearlyExpenseStatistics(currentYear)
    ])

    // 处理昨日支出
    if (yesterdayResponse.code === 200 || yesterdayResponse.code === 0) {
      yesterdayExpense.value = yesterdayResponse.data.expense || 0
    } else {
      message.error(yesterdayResponse.message || '获取昨日支出数据失败')
    }

    // 处理本月支出
    if (monthResponse.code === 200 || monthResponse.code === 0) {
      monthExpense.value = monthResponse.data.expense || 0
    } else {
      message.error(monthResponse.message || '获取本月支出数据失败')
    }

    // 处理上月支出
    if (lastMonthResponse.code === 200 || lastMonthResponse.code === 0) {
      lastMonthExpense.value = lastMonthResponse.data.expense || 0
    } else {
      message.error(lastMonthResponse.message || '获取上月支出数据失败')
    }

    // 处理本年支出
    if (yearResponse.code === 200 || yearResponse.code === 0) {
      yearExpense.value = yearResponse.data.expense || 0
    } else {
      message.error(yearResponse.message || '获取本年支出数据失败')
    }
  } catch (error) {
    console.error('加载支出统计数据失败：', error)
    message.error('加载支出统计数据失败')
  } finally {
    statsLoading.value = false
  }
}

const loadChartData = async () => {
  try {
    chartsLoading.value = true
    
    // 获取最近7天趋势数据
    const trendResponse = await BillApi.getTrendStatistics(7)
    console.log('趋势数据响应:', trendResponse) // 调试信息
    
    // 获取最近7天的分类统计（支出）- 与趋势图保持一致的时间范围
    const endDate = dayjs().format('YYYY-MM-DD')
    const startDate = dayjs().subtract(6, 'day').format('YYYY-MM-DD')
    console.log('分类统计时间范围:', startDate, '到', endDate) // 调试信息
    const categoryResponse = await BillApi.getCategoryStatistics(startDate, endDate, TransactionType.EXPENSE)
    console.log('分类数据响应:', categoryResponse) // 调试信息
    
    if (trendResponse.code === 200 || trendResponse.code === 0) {
      // 处理趋势数据 - 确保数据格式正确，兼容大小写字段名
      console.log('原始趋势数据:', trendResponse.data) // 调试信息
      const trendResult = (trendResponse.data || []).map((item: any) => ({
        date: item.date || item.DATE || item.billDate || item.BILLDATE || '',
        income: parseFloat(item.income || item.INCOME || 0),
        expense: parseFloat(item.expense || item.EXPENSE || 0)
      }))
      console.log('处理后的趋势数据:', trendResult) // 调试信息
      
      // 确保有7天的数据，缺失的日期用0填充
      const trendMap = new Map<string, { income: number; expense: number }>()
      trendResult.forEach(item => {
        // 格式化日期为 YYYY-MM-DD 格式
        const formattedDate = dayjs(item.date).format('YYYY-MM-DD')
        trendMap.set(formattedDate, { income: item.income, expense: item.expense })
      })
      
      // 生成完整的7天数据
      trendData.value = Array.from({ length: 7 }, (_, i) => {
        const date = dayjs().subtract(6 - i, 'day').format('YYYY-MM-DD')
        const data = trendMap.get(date) || { income: 0, expense: 0 }
        return {
          date,
          income: data.income,
          expense: data.expense
        }
      })
      console.log('最终趋势数据:', trendData.value) // 调试信息
      
      // 数据更新后，确保图表重新渲染
      await nextTick()
    } else {
      message.error(trendResponse.message || '获取趋势数据失败')
    }
    
    if (categoryResponse.code === 200 || categoryResponse.code === 0) {
      // 处理分类数据 - 确保数据格式正确，兼容大小写字段名
      console.log('原始分类数据:', categoryResponse.data) // 调试信息
      const categoryResult = (categoryResponse.data || []).map((item: any) => ({
        categoryName: item.categoryName || item.CATEGORYNAME || item.name || '未分类',
        amount: parseFloat(item.amount || item.AMOUNT || 0),
        count: parseInt(item.count || item.COUNT || 0)
      }))
      console.log('处理后的分类数据:', categoryResult) // 调试信息
      
      categoryData.value = categoryResult
        .filter(item => item.amount > 0) // 只显示有金额的分类
        .map(item => ({
          name: item.categoryName,
          value: item.amount
        }))
      console.log('最终分类数据:', categoryData.value) // 调试信息
      
      // 数据更新后，确保图表重新渲染
      await nextTick()
    } else {
      message.error(categoryResponse.message || '获取分类数据失败')
    }
    
    // 只增加趋势图表的key，分类图表的key由handleMonthChange函数控制
    trendChartKey.value++
    console.log('更新trendChartKey:', trendChartKey.value) // 调试信息
    
    // 手动触发趋势图表更新
    await nextTick()
    if (trendChartRef.value) {
      console.log('手动更新趋势图表')
      trendChartRef.value.updateChart()
    }
  } catch (error) {
    console.error('加载图表数据失败：', error)
    message.error('加载图表数据失败')
  } finally {
    chartsLoading.value = false
  }
}

// 加载指定月份的分类数据
const loadCategoryData = async () => {
  try {
    categoryChartLoading.value = true
    
    // 获取选择月份的第一天和最后一天
    const startDate = selectedMonth.value.startOf('month').format('YYYY-MM-DD')
    const endDate = selectedMonth.value.endOf('month').format('YYYY-MM-DD')
    
    console.log('月份切换时间范围:', startDate, '到', endDate) // 调试信息
    
    // 获取分类统计（支出）
    const categoryResponse = await BillApi.getCategoryStatistics(startDate, endDate, TransactionType.EXPENSE)
    console.log('月份切换-分类数据响应:', categoryResponse) // 调试信息
    
    if (categoryResponse.code === 200 || categoryResponse.code === 0) {
      // 处理分类数据 - 确保数据格式正确，兼容大小写字段名
      console.log('月份切换-原始分类数据:', categoryResponse.data) // 调试信息
      const categoryResult = (categoryResponse.data || []).map((item: any) => ({
        categoryName: item.categoryName || item.CATEGORYNAME || item.name || '未分类',
        amount: parseFloat(item.amount || item.AMOUNT || 0),
        count: parseInt(item.count || item.COUNT || 0)
      }))
      console.log('月份切换-处理后的分类数据:', categoryResult) // 调试信息
      
      // 更新分类数据
      categoryData.value = categoryResult
        .filter(item => item.amount > 0) // 只显示有金额的分类
        .map(item => ({
          name: item.categoryName,
          value: item.amount
        }))
      console.log('月份切换-最终分类数据:', categoryData.value) // 调试信息
      
      // 数据更新后，确保图表重新渲染
      await nextTick()
    } else {
      message.error(categoryResponse.message || '获取分类数据失败')
    }
  } catch (error) {
    console.error('加载分类数据失败：', error)
    message.error('加载分类数据失败')
  } finally {
    categoryChartLoading.value = false
  }
}

// 加载年度月度统计数据
const loadYearlyData = async () => {
  try {
    yearlyChartLoading.value = true

    const year = selectedYear.value.format('YYYY')
    console.log('加载年度数据，年份:', year)

    const yearlyResponse = await BillApi.getYearlyMonthlyStatistics(year)
    console.log('年度数据响应:', yearlyResponse)

    if (yearlyResponse.code === 200 || yearlyResponse.code === 0) {
      // 处理年度数据 - bill_month 格式为 "202601"
      const result = (yearlyResponse.data || []).map((item: any) => ({
        month: item.bill_month || item.BILL_MONTH || '',
        income: parseFloat(item.income || item.INCOME || 0),
        expense: parseFloat(item.expense || item.EXPENSE || 0)
      }))
      console.log('处理后的年度数据:', result)

      // 创建月份映射 - 从 "202601" 提取月份 1
      const monthlyMap = new Map<number, { income: number; expense: number }>()
      result.forEach(item => {
        const monthNum = parseInt(item.month.substring(4, 6))
        monthlyMap.set(monthNum, { income: item.income, expense: item.expense })
      })

      // 确保包含12个月的数据，缺失的月份用0填充
      yearlyData.value = Array.from({ length: 12 }, (_, i) => {
        const monthNum = i + 1
        const data = monthlyMap.get(monthNum) || { income: 0, expense: 0 }
        return {
          month: monthNum,
          income: data.income,
          expense: data.expense
        }
      })
      console.log('最终年度数据:', yearlyData.value)

      await nextTick()
    } else {
      message.error(yearlyResponse.message || '获取年度数据失败')
    }
  } catch (error) {
    console.error('加载年度数据失败：', error)
    message.error('加载年度数据失败')
  } finally {
    yearlyChartLoading.value = false
  }
}

onMounted(async () => {
  console.log('开始加载仪表板数据...') // 调试信息
  // 并发加载数据
  await Promise.all([
    loadExpenseStats(),
    loadChartData(),
    loadYearlyData()
  ])
  console.log('仪表板数据加载完成') // 调试信息
})
</script>

<style scoped>
.dashboard {
  padding: 24px;
  background: #f0f2f5;
  min-height: 100vh;
}

.stats-row {
  margin-bottom: 24px;
}

.charts-row {
  margin-bottom: 24px;
}

.chart-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.month-picker {
  width: 100px;
}

.year-picker {
  width: 100px;
}

.recent-bills-row {
  margin-bottom: 24px;
}

.recent-bills-card {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: none;
}

.bills-list {
  max-height: 400px;
  overflow-y: auto;
}

.bill-item {
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
  transition: all 0.3s ease;
  position: relative;
  text-align: left;
  display: block;
}

.bill-item:hover {
  background-color: #fafafa;
  border-radius: 4px;
  padding: 16px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.bill-item:last-child {
  border-bottom: none;
}

.bill-avatar {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
  font-weight: bold;
}

.bill-avatar.income {
  background: linear-gradient(135deg, #52c41a, #389e0d);
}

.bill-avatar.expense {
  background: linear-gradient(135deg, #ff4d4f, #f5222d);
}

.bill-content {
  display: flex;
  width: 100%;
  text-align: left;
}

.bill-main-info {
  display: flex;
  flex-direction: column;
  flex: 1;
  text-align: left;
  align-items: flex-start;
}

.bill-description {
  font-weight: 500;
  font-size: 16px;
  color: #262626;
  margin-bottom: 8px;
  text-align: left;
  width: 100%;
}

.bill-details-row {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 4px;
  text-align: left;
  width: 100%;
  justify-content: flex-start;
}

.bill-amount {
  font-size: 18px;
  font-weight: 600;
  white-space: nowrap;
  text-align: left;
}

.bill-amount.income {
  color: #52c41a;
}

.bill-amount.expense {
  color: #ff4d4f;
}

.bill-type-tag {
  font-size: 12px;
  border-radius: 4px;
}

.bill-meta-info {
  display: flex;
  gap: 8px;
  align-items: center;
  text-align: left;
  width: 100%;
  justify-content: flex-start;
}

.bill-category-small,
.bill-date-small {
  font-size: 12px;
  color: #8c8c8c;
  text-align: left;
}

.separator {
  color: #d9d9d9;
  font-size: 12px;
}

/* 分类数据容器样式 */
.category-data-container {
  height: 300px;
  overflow-y: auto;
  padding: 10px 0;
}

/* 总支出统计样式 */
.category-total-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  font-size: 13px;
  font-weight: 600;
  border-bottom: 2px solid #f0f0f0;
  margin-bottom: 8px;
}

.category-total-label {
  color: #262626;
}

.category-total-amount {
  color: #ff4d4f;
}

/* 分类支出数据样式 */
.category-data-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 0;
  font-size: 12px;
  border-bottom: 1px dashed #f0f0f0;
}

.category-data-item:last-child {
  border-bottom: none;
}

.category-name {
  color: #595959;
  max-width: 70%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.category-amount {
  color: #ff4d4f;
  font-weight: 500;
  flex-shrink: 0;
}

@media (max-width: 768px) {
  .dashboard {
    padding: 16px;
  }
  
  .bill-amount {
    font-size: 16px;
  }
  
  .bill-item:hover {
    padding: 16px 0;
    box-shadow: none;
  }
  
  .bill-content,
  .bill-main-info,
  .bill-description,
  .bill-details-row,
  .bill-meta-info,
  .bill-category-small,
  .bill-date-small {
    text-align: left;
    align-items: flex-start;
  }
  
  .chart-title {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .month-picker {
    width: 100%;
  }
  
  /* 移动端分类数据容器样式 */
  .category-data-container {
    height: auto;
    max-height: 150px;
  }
  
  .category-total-item {
    padding: 6px 0;
    font-size: 12px;
  }
  
  .category-data-item {
    padding: 4px 0;
  }
  
  .category-name,
  .category-amount {
    font-size: 11px;
  }
}
</style>