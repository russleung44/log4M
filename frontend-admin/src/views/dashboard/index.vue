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
          title="今日收入"
          :value="todayIncome"
          :icon="ArrowUpOutlined"
          color="#52c41a"
          suffix="元"
          :precision="2"
          :trend="incomeTrend"
          trend-label="较昨日"
          :loading="statsLoading"
        />
      </a-col>
      
      <a-col :xs="24" :sm="12" :lg="6">
        <StatisticCard
          title="今日支出"
          :value="todayExpense"
          :icon="ArrowDownOutlined"
          color="#ff4d4f"
          suffix="元"
          :precision="2"
          :trend="expenseTrend"
          trend-label="较昨日"
          :loading="statsLoading"
        />
      </a-col>
      
      <a-col :xs="24" :sm="12" :lg="6">
        <StatisticCard
          title="今日净收入"
          :value="todayBalance"
          :icon="WalletOutlined"
          :color="todayBalance >= 0 ? '#52c41a' : '#ff4d4f'"
          suffix="元"
          :precision="2"
          :loading="statsLoading"
        />
      </a-col>
      
      <a-col :xs="24" :sm="12" :lg="6">
        <StatisticCard
          title="今日交易"
          :value="todayCount"
          :icon="TransactionOutlined"
          color="#1890ff"
          suffix="笔"
          :loading="statsLoading"
        />
      </a-col>
    </a-row>
    
    <!-- 图表区域 -->
    <a-row :gutter="[16, 16]" class="charts-row">
      <a-col :xs="24" :lg="12">
        <a-card title="最近7天收支趋势" :loading="chartsLoading">
          <EChart
            :option="trendChartOption"
            height="300px"
            :loading="chartsLoading"
            @chart-click="handleTrendClick"
          />
        </a-card>
      </a-col>
      
      <a-col :xs="24" :lg="12">
        <a-card title="本月支出分类" :loading="chartsLoading">
          <EChart
            :option="categoryChartOption"
            height="300px"
            :loading="chartsLoading"
            @chart-click="handleCategoryClick"
          />
        </a-card>
      </a-col>
    </a-row>
    
    <!-- 最近账单 -->
    <a-row :gutter="[16, 16]" class="recent-bills-row">
      <a-col :span="24">
        <a-card title="最近账单" :loading="billsLoading">
          <template #extra>
            <a-button type="link" @click="navigateTo('/bills')">
              查看全部
            </a-button>
          </template>
          
          <a-list
            :data-source="recentBills"
            :loading="billsLoading"
          >
            <template #renderItem="{ item }">
              <a-list-item>
                <a-list-item-meta>
                  <template #title>
                    <div class="bill-title">
                      <span class="bill-description">{{ item.note || '无备注' }}</span>
                      <a-tag :color="item.transactionType === 'INCOME' ? 'green' : 'red'">
                        {{ item.transactionType === 'INCOME' ? '收入' : '支出' }}
                      </a-tag>
                    </div>
                  </template>
                  <template #description>
                    <div class="bill-meta">
                      <span>{{ item.categoryName || '未分类' }}</span>
                      <span>{{ formatDate(item.billDate) }}</span>
                    </div>
                  </template>
                </a-list-item-meta>
                <div class="bill-amount">
                  <span 
                    :class="[
                      'amount-text',
                      item.transactionType === 'INCOME' ? 'income' : 'expense'
                    ]"
                  >
                    {{ item.transactionType === 'INCOME' ? '+' : '-' }}¥{{ item.amount }}
                  </span>
                </div>
              </a-list-item>
            </template>
          </a-list>
        </a-card>
      </a-col>
    </a-row>
    
    <!-- 快捷操作 -->
    <a-card title="快捷操作" class="quick-actions">
      <a-row :gutter="[16, 16]">
        <a-col :xs="12" :sm="6">
          <a-button type="primary" block size="large" @click="navigateTo('/bills/create')">
            <PlusOutlined />
            添加账单
          </a-button>
        </a-col>
        <a-col :xs="12" :sm="6">
          <a-button block size="large" @click="navigateTo('/bills')">
            <UnorderedListOutlined />
            账单管理
          </a-button>
        </a-col>
        <a-col :xs="12" :sm="6">
          <a-button block size="large" @click="navigateTo('/categories')">
            <AppstoreOutlined />
            分类管理
          </a-button>
        </a-col>
        <a-col :xs="12" :sm="6">
          <a-button block size="large" @click="navigateTo('/rules')">
            <SettingOutlined />
            规则管理
          </a-button>
        </a-col>
      </a-row>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  ArrowUpOutlined,
  ArrowDownOutlined,
  WalletOutlined,
  TransactionOutlined,
  PlusOutlined,
  UnorderedListOutlined,
  AppstoreOutlined,
  SettingOutlined
} from '@ant-design/icons-vue'
import dayjs from 'dayjs'
import type { EChartsOption } from 'echarts'

import EChart from '@/components/Chart/EChart.vue'
import StatisticCard from '@/components/StatisticCard/index.vue'
import { useBillStore, useCategoryStore } from '@/stores'
import type { Bill } from '@/types'

const router = useRouter()
const billStore = useBillStore()
const categoryStore = useCategoryStore()

// 响应式数据
const statsLoading = ref(false)
const chartsLoading = ref(false)
const billsLoading = ref(false)

const todayIncome = ref(0)
const todayExpense = ref(0)
const todayCount = ref(0)
const incomeTrend = ref(0)
const expenseTrend = ref(0)

const recentBills = ref<Bill[]>([])
const trendData = ref<{ date: string; income: number; expense: number }[]>([])
const categoryData = ref<{ name: string; value: number }[]>([])

// 计算属性
const todayBalance = computed(() => todayIncome.value - todayExpense.value)

// 图表配置
const trendChartOption = computed<EChartsOption>(() => ({
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
}))

const categoryChartOption = computed<EChartsOption>(() => ({
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
}))

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

const formatDate = (date: string) => {
  return dayjs(date).format('YYYY-MM-DD')
}

const loadTodayStats = async () => {
  try {
    statsLoading.value = true
    const today = dayjs().format('YYYY-MM-DD')
    
    // 模拟数据，实际应该调用API
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    todayIncome.value = 1500.50
    todayExpense.value = 280.30
    todayCount.value = 8
    incomeTrend.value = 12.5
    expenseTrend.value = -5.2
  } catch (error) {
    console.error('加载今日统计失败：', error)
    message.error('加载统计数据失败')
  } finally {
    statsLoading.value = false
  }
}

const loadChartData = async () => {
  try {
    chartsLoading.value = true
    
    // 模拟数据，实际应该调用API
    await new Promise(resolve => setTimeout(resolve, 1200))
    
    // 生成最近7天的模拟数据
    trendData.value = Array.from({ length: 7 }, (_, i) => {
      const date = dayjs().subtract(6 - i, 'day').format('YYYY-MM-DD')
      return {
        date,
        income: Math.random() * 2000 + 500,
        expense: Math.random() * 1000 + 200
      }
    })
    
    // 模拟分类数据
    categoryData.value = [
      { name: '餐饮', value: 850 },
      { name: '交通', value: 320 },
      { name: '购物', value: 680 },
      { name: '娱乐', value: 420 },
      { name: '其他', value: 180 }
    ]
  } catch (error) {
    console.error('加载图表数据失败：', error)
    message.error('加载图表数据失败')
  } finally {
    chartsLoading.value = false
  }
}

const loadRecentBills = async () => {
  try {
    billsLoading.value = true
    
    // 模拟数据，实际应该调用billStore.fetchBills()
    await new Promise(resolve => setTimeout(resolve, 800))
    
    recentBills.value = [
      {
        billId: 1,
        billDate: '2024-01-20',
        amount: 25.5,
        note: '午餐',
        categoryId: 1,
        categoryName: '餐饮',
        accountId: 1,
        transactionType: 'EXPENSE',
        crTime: '2024-01-20 12:30:00',
        upTime: '2024-01-20 12:30:00'
      },
      {
        billId: 2,
        billDate: '2024-01-20',
        amount: 3000,
        note: '工资',
        categoryId: 2,
        categoryName: '工资',
        accountId: 1,
        transactionType: 'INCOME',
        crTime: '2024-01-20 09:00:00',
        upTime: '2024-01-20 09:00:00'
      },
      {
        billId: 3,
        billDate: '2024-01-19',
        amount: 15.8,
        note: '地铁',
        categoryId: 3,
        categoryName: '交通',
        accountId: 1,
        transactionType: 'EXPENSE',
        crTime: '2024-01-19 18:45:00',
        upTime: '2024-01-19 18:45:00'
      }
    ] as Bill[]
  } catch (error) {
    console.error('加载最近账单失败：', error)
    message.error('加载最近账单失败')
  } finally {
    billsLoading.value = false
  }
}

onMounted(async () => {
  // 并发加载数据
  await Promise.all([
    loadTodayStats(),
    loadChartData(),
    loadRecentBills()
  ])
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

.recent-bills-row {
  margin-bottom: 24px;
}

.bill-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.bill-description {
  font-weight: 500;
}

.bill-meta {
  display: flex;
  gap: 16px;
  color: rgba(0, 0, 0, 0.45);
  font-size: 12px;
}

.bill-amount {
  text-align: right;
}

.amount-text {
  font-size: 16px;
  font-weight: 600;
}

.amount-text.income {
  color: #52c41a;
}

.amount-text.expense {
  color: #ff4d4f;
}

.quick-actions {
  margin-top: 24px;
}

@media (max-width: 768px) {
  .dashboard {
    padding: 16px;
  }
  
  .bill-meta {
    flex-direction: column;
    gap: 4px;
  }
}
</style>