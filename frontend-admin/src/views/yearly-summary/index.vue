<template>
  <div class="yearly-summary">
    <!-- 头部年份选择 -->
    <div class="header">
      <a-typography-title :level="3">{{ selectedYear }} 年度总结</a-typography-title>
      <a-date-picker
        v-model:value="selectedYearDayjs"
        picker="year"
        :allow-clear="false"
        @change="handleYearChange"
        size="large"
      />
    </div>

    <!-- Bot 对话区域 -->
    <div class="chat-container" ref="chatContainer">
      <div
        v-for="(message, index) in messages"
        :key="index"
        :class="['message', message.type]"
      >
        <div class="message-avatar">
          <component :is="message.type === 'bot' ? RobotOutlined : UserOutlined" />
        </div>
        <div class="message-content">
          <div
            v-if="message.type === 'bot' && message.typing"
            class="typing-indicator"
          >
            <span></span>
            <span></span>
            <span></span>
          </div>
          <div v-else class="message-text" v-html="message.content"></div>
        </div>
      </div>
    </div>

    <!-- 重新播放按钮 -->
    <div class="action-bar">
      <a-button
        type="primary"
        :icon="ReloadOutlined"
        @click="replay"
        :loading="loading"
        :disabled="messages.length === 0"
      >
        重新播放
      </a-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, nextTick, onMounted, watch } from 'vue'
import { message } from 'ant-design-vue'
import { RobotOutlined, UserOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import dayjs, { Dayjs } from 'dayjs'
import { BillApi } from '@/api'
import type { YearlyMonthlyStatistics, CategoryStatistics } from '@/types'
import { TransactionType } from '@/types'

interface ChatMessage {
  type: 'bot' | 'user'
  content: string
  typing?: boolean
}

const selectedYearDayjs = ref<Dayjs>(dayjs())
const selectedYear = computed(() => selectedYearDayjs.value.format('YYYY'))
const chatContainer = ref<HTMLElement>()
const messages = ref<ChatMessage[]>([])
const loading = ref(false)
let typingTimer: number | null = null

// 年度数据
const yearlyData = ref<{
  totalIncome: number
  totalExpense: number
  monthlyData: YearlyMonthlyStatistics[]
  categoryData: CategoryStatistics[]
  maxExpenseMonth: { month: string; amount: string }
  avgMonthlyExpense: string
}>({
  totalIncome: 0,
  totalExpense: 0,
  monthlyData: [],
  categoryData: [],
  maxExpenseMonth: { month: '', amount: '0' },
  avgMonthlyExpense: '0'
})

// 滚动到底部
const scrollToBottom = async () => {
  await nextTick()
  if (chatContainer.value) {
    chatContainer.value.scrollTop = chatContainer.value.scrollHeight
  }
}

// 模拟打字效果
const typeMessage = async (content: string, delay: number = 50): Promise<void> => {
  return new Promise((resolve) => {
    const messageId = messages.value.length
    messages.value.push({
      type: 'bot',
      content: '',
      typing: true
    })

    scrollToBottom()

    let index = 0
    const chars = content.split('')

    typingTimer = window.setInterval(() => {
      if (index < chars.length) {
        messages.value[messageId].content += chars[index]
        index++
        scrollToBottom()
      } else {
        if (typingTimer) {
          clearInterval(typingTimer)
          typingTimer = null
        }
        messages.value[messageId].typing = false
        resolve()
      }
    }, delay)
  })
}

// 添加消息（无打字效果）
const addMessage = (content: string, type: 'bot' | 'user' = 'bot') => {
  messages.value.push({ type, content })
  scrollToBottom()
}

// 格式化金额
const formatAmount = (amount: number): string => {
  if (amount >= 10000) {
    return (amount / 10000).toFixed(2) + '万'
  }
  return amount.toFixed(2)
}

// 获取月份名称
const getMonthName = (month: number): string => {
  const names = ['一月', '二月', '三月', '四月', '五月', '六月',
                 '七月', '八月', '九月', '十月', '十一月', '十二月']
  return names[month - 1] || `${month}月`
}

// 获取随机问候语
const getGreeting = (): string => {
  const hour = new Date().getHours()
  let timeGreeting = '你好'
  if (hour < 6) timeGreeting = '深夜好'
  else if (hour < 9) timeGreeting = '早上好'
  else if (hour < 12) timeGreeting = '上午好'
  else if (hour < 14) timeGreeting = '中午好'
  else if (hour < 18) timeGreeting = '下午好'
  else if (hour < 22) timeGreeting = '晚上好'
  else timeGreeting = '夜深了'

  const greetings = [
    `${timeGreeting}！我是你的记账小助手 🤖`,
    `哈喽！${timeGreeting}！让我来帮你回顾一下 ${selectedYear.value} 年的记账情况吧 📊`,
    `${timeGreeting}！来看看你 ${selectedYear.value} 年的财务表现如何 💰`
  ]
  return greetings[Math.floor(Math.random() * greetings.length)]
}

// 获取年度评价
const getYearlyEvaluation = (): string => {
  const { totalIncome, totalExpense } = yearlyData.value
  const balance = totalIncome - totalExpense

  if (balance > 0) {
    const savings = (balance / totalIncome * 100).toFixed(1)
    return `太棒了！${selectedYear.value} 年你实现了<strong style="color: #52c41a">盈余 ¥${formatAmount(balance)}</strong>，储蓄率达到 <strong>${savings}%</strong>！继续保持这个良好的理财习惯 💪`
  } else if (balance < 0) {
    return `${selectedYear.value} 年支出超过了收入 <strong style="color: #ff4d4f">¥${formatAmount(Math.abs(balance))}</strong>。建议在新的一年里合理规划支出，控制不必要的消费 📝`
  } else {
    return `${selectedYear.value} 年收支平衡，每一分钱都用在了刀刃上！可以考虑增加一些储蓄计划 🎯`
  }
}

// 加载年度数据
const loadYearlyData = async () => {
  try {
    loading.value = true

    const year = selectedYear.value

    // 并行获取年度月度数据和分类数据
    const [monthlyResponse, categoryResponse] = await Promise.all([
      BillApi.getYearlyMonthlyStatistics(year),
      BillApi.getCategoryStatistics(`${year}-01-01`, `${year}-12-31`, TransactionType.EXPENSE)
    ])

    if (monthlyResponse.code === 200 || monthlyResponse.code === 0) {
      const data = (monthlyResponse.data || []).map((item: any) => ({
        month: item.bill_month || item.BILL_MONTH || '',
        income: parseFloat(item.income || item.INCOME || 0),
        expense: parseFloat(item.expense || item.EXPENSE || 0)
      }))

      // 创建月份映射
      const monthlyMap = new Map<number, { income: number; expense: number }>()
      data.forEach(item => {
        const monthNum = parseInt(item.month.substring(4, 6))
        monthlyMap.set(monthNum, { income: item.income, expense: item.expense })
      })

      // 确保包含12个月的数据
      const monthlyData = Array.from({ length: 12 }, (_, i) => {
        const monthNum = i + 1
        const data = monthlyMap.get(monthNum) || { income: 0, expense: 0 }
        return {
          month: monthNum,
          income: data.income,
          expense: data.expense
        }
      })

      const totalIncome = monthlyData.reduce((sum, m) => sum + m.income, 0)
      const totalExpense = monthlyData.reduce((sum, m) => sum + m.expense, 0)

      // 找出支出最多的月份
      const maxMonth = monthlyData.reduce((max, m) =>
        m.expense > (monthlyData.find(md => md.month === max.month)?.expense || 0) ? m : max
      , monthlyData[0] || { month: 1, expense: 0 })

      const avgMonthlyExpense = totalExpense / 12

      yearlyData.value = {
        totalIncome,
        totalExpense,
        monthlyData,
        categoryData: [],
        maxExpenseMonth: {
          month: getMonthName(maxMonth.month),
          amount: formatAmount(maxMonth.expense)
        },
        avgMonthlyExpense: formatAmount(avgMonthlyExpense)
      }
    }

    if (categoryResponse.code === 200 || categoryResponse.code === 0) {
      const categoryData = (categoryResponse.data || [])
        .map((item: any) => ({
          categoryName: item.categoryName || item.CATEGORYNAME || item.name || '未分类',
          amount: parseFloat(item.amount || item.AMOUNT || 0),
          count: parseInt(item.count || item.COUNT || 0)
        }))
        .filter(item => item.amount > 0)
        .sort((a, b) => b.amount - a.amount)

      yearlyData.value.categoryData = categoryData
    }
  } catch (error) {
    console.error('加载年度数据失败：', error)
    message.error('加载年度数据失败')
  } finally {
    loading.value = false
  }
}

// 播放年度总结
const playSummary = async () => {
  messages.value = []

  // 延迟开始
  await new Promise(resolve => setTimeout(resolve, 500))

  // 问候
  await typeMessage(getGreeting(), 30)
  await new Promise(resolve => setTimeout(resolve, 800))

  // 年度总览
  await typeMessage(
    `📅 <strong>${selectedYear.value} 年度账单总览</strong><br><br>` +
    `💰 总收入：<span style="color: #52c41a; font-size: 18px; font-weight: bold;">¥${formatAmount(yearlyData.value.totalIncome)}</span><br>` +
    `💸 总支出：<span style="color: #ff4d4f; font-size: 18px; font-weight: bold;">¥${formatAmount(yearlyData.value.totalExpense)}</span><br>` +
    `📊 月均支出：¥${yearlyData.value.avgMonthlyExpense}`,
    20
  )
  await new Promise(resolve => setTimeout(resolve, 1000))

  // 年度评价
  await typeMessage(getYearlyEvaluation(), 30)
  await new Promise(resolve => setTimeout(resolve, 1000))

  // 支出最多的月份
  if (yearlyData.value.totalExpense > 0) {
    await typeMessage(
      `🔝 支出最多的月份是 <strong>${yearlyData.value.maxExpenseMonth.month}</strong>，` +
      `共支出 ¥${yearlyData.value.maxExpenseMonth.amount}`,
      30
    )
    await new Promise(resolve => setTimeout(resolve, 800))
  }

  // 月度趋势摘要
  const activeMonths = yearlyData.value.monthlyData.filter(m => m.expense > 0).length
  await typeMessage(
    `📈 这一年你共有 <strong>${activeMonths}</strong> 个月有记账记录，` +
    `坚持记录是良好理财习惯的开始！`,
    30
  )
  await new Promise(resolve => setTimeout(resolve, 800))

  // 分类排行
  if (yearlyData.value.categoryData.length > 0) {
    const top3 = yearlyData.value.categoryData.slice(0, 3)
    let categoryText = '🏆 支出分类 TOP3<br><br>'
    top3.forEach((cat, index) => {
      const medals = ['🥇', '🥈', '🥉']
      const percent = ((cat.amount / yearlyData.value.totalExpense) * 100).toFixed(1)
      categoryText += `${medals[index]} <strong>${cat.categoryName}</strong>：¥${formatAmount(cat.amount)} (${cat.count}笔, ${percent}%)<br>`
    })
    await typeMessage(categoryText, 20)
    await new Promise(resolve => setTimeout(resolve, 1000))
  }

  // 结语
  const endings = [
    `以上就是 ${selectedYear.value} 年的记账总结啦！希望新的一年你能继续保持良好的记账习惯，实现财务自由的目标！🎉`,
    `感谢你这一年的坚持记录！记账让我们更了解自己的消费习惯，让每一分钱都花得明明白白！✨`,
    `${selectedYear.value} 年的记账回顾就到这里。新的一年，让我们一起制定更合理的财务计划吧！🚀`
  ]
  await typeMessage(endings[Math.floor(Math.random() * endings.length)], 30)
}

// 重新播放
const replay = async () => {
  await loadYearlyData()
  await playSummary()
}

// 年份改变
const handleYearChange = async () => {
  if (messages.value.length > 0) {
    await replay()
  }
}

// 监听年份变化
watch(selectedYear, async () => {
  if (messages.value.length > 0) {
    await replay()
  }
})

onMounted(async () => {
  await loadYearlyData()
  await playSummary()
})
</script>

<style scoped>
.yearly-summary {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 64px);
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  z-index: 10;
}

.header :deep(.ant-typography) {
  margin: 0;
}

.chat-container {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message {
  display: flex;
  gap: 12px;
  max-width: 80%;
  animation: fadeIn 0.3s ease-in;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message.bot {
  align-self: flex-start;
}

.message.user {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.message.bot .message-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.message.user .message-avatar {
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
  color: #d35400;
}

.message-content {
  background: white;
  padding: 12px 16px;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.message.bot .message-content {
  border-bottom-left-radius: 4px;
}

.message.user .message-content {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-bottom-right-radius: 4px;
}

.message-text {
  line-height: 1.6;
  word-break: break-word;
}

.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 4px 0;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #b0b0b0;
  animation: typing 1.4s infinite;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
    opacity: 0.7;
  }
  30% {
    transform: translateY(-10px);
    opacity: 1;
  }
}

.action-bar {
  padding: 16px 24px;
  background: white;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.08);
  display: flex;
  justify-content: center;
  gap: 12px;
}

/* 滚动条样式 */
.chat-container::-webkit-scrollbar {
  width: 6px;
}

.chat-container::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 3px;
}

.chat-container::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.2);
  border-radius: 3px;
}

.chat-container::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 0, 0, 0.3);
}

/* 响应式 */
@media (max-width: 768px) {
  .header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }

  .header :deep(.ant-picker) {
    width: 100%;
  }

  .message {
    max-width: 90%;
  }
}
</style>
