import { defineStore } from 'pinia'
import { ref } from 'vue'
import { message } from 'ant-design-vue'
import { BillScheduleApi } from '@/api'
import type { BillSchedule, SaveBillScheduleDto } from '@/types'

export const useScheduleStore = defineStore('schedule', () => {
  // State
  const schedules = ref<BillSchedule[]>([])
  const loading = ref(false)

  // Actions
  const fetchSchedules = async () => {
    try {
      loading.value = true
      const response = await BillScheduleApi.list()

      if (response.code === 200 || response.code === 0) {
        schedules.value = response.data
      } else {
        message.error(response.message || '获取定时记账列表失败')
      }
    } catch (error) {
      console.error('Failed to fetch schedules:', error)
      message.error('获取定时记账列表失败')
    } finally {
      loading.value = false
    }
  }

  const createSchedule = async (data: SaveBillScheduleDto) => {
    try {
      loading.value = true
      const response = await BillScheduleApi.create(data)

      if (response.code === 200 || response.code === 0) {
        message.success('创建定时记账成功')
        await fetchSchedules()
        return response.data
      } else {
        message.error(response.message || '创建定时记账失败')
        return null
      }
    } catch (error) {
      console.error('Failed to create schedule:', error)
      message.error('创建定时记账失败')
      return null
    } finally {
      loading.value = false
    }
  }

  const updateSchedule = async (id: number, data: SaveBillScheduleDto) => {
    try {
      loading.value = true
      const response = await BillScheduleApi.update(id, data)

      if (response.code === 200 || response.code === 0) {
        message.success('更新定时记账成功')
        await fetchSchedules()
        return true
      } else {
        message.error(response.message || '更新定时记账失败')
        return false
      }
    } catch (error) {
      console.error('Failed to update schedule:', error)
      message.error('更新定时记账失败')
      return false
    } finally {
      loading.value = false
    }
  }

  const deleteSchedule = async (id: number) => {
    try {
      loading.value = true
      const response = await BillScheduleApi.delete(id)

      if (response.code === 200 || response.code === 0) {
        message.success('删除定时记账成功')
        await fetchSchedules()
        return true
      } else {
        message.error(response.message || '删除定时记账失败')
        return false
      }
    } catch (error) {
      console.error('Failed to delete schedule:', error)
      message.error('删除定时记账失败')
      return false
    } finally {
      loading.value = false
    }
  }

  /** 启用/停用（整条记录原样提交，仅翻转 enabled） */
  const toggleSchedule = async (record: BillSchedule) => {
    return updateSchedule(record.scheduleId, {
      note: record.note,
      categoryId: record.categoryId,
      amount: record.amount,
      execTime: record.execTime,
      weekDays: record.weekDays,
      enabled: !record.enabled
    })
  }

  return {
    // State
    schedules,
    loading,

    // Actions
    fetchSchedules,
    createSchedule,
    updateSchedule,
    deleteSchedule,
    toggleSchedule
  }
})
