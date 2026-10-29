package com.tony.log4m.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tony.log4m.mapper.BillScheduleMapper;
import com.tony.log4m.models.entity.BillSchedule;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * @author Tony
 * @since 10/29/2026
 */
@Service
public class BillScheduleService extends ServiceImpl<BillScheduleMapper, BillSchedule> {

    /**
     * 判断定时计划是否在指定时刻触发
     */
    public boolean isDue(BillSchedule schedule, LocalDate date, LocalTime now) {
        return Boolean.TRUE.equals(schedule.getEnabled())
                && schedule.getExecTime().equals(now)
                && schedule.getWeekDays().contains(String.valueOf(date.getDayOfWeek().getValue()));
    }
}
