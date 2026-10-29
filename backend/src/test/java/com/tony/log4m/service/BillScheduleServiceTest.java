package com.tony.log4m.service;

import com.tony.log4m.models.entity.BillSchedule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * BillScheduleService 单元测试
 *
 * @author Tony
 * @since 10/29/2026
 */
@DisplayName("定时记账触发判断测试")
class BillScheduleServiceTest {

    private final BillScheduleService service = new BillScheduleService();

    /**
     * 2026-10-26 周一，2026-10-31 周六，2026-11-01 周日
     */
    private final BillSchedule workdayNine = schedule("12345", "09:00", true);

    @Test
    @DisplayName("工作日到达触发时间 - 触发")
    void testIsDue_Match() {
        assertTrue(service.isDue(workdayNine, LocalDate.of(2026, 10, 26), LocalTime.of(9, 0)));
    }

    @Test
    @DisplayName("周末不在工作日计划内 - 不触发")
    void testIsDue_WeekendSkipped() {
        assertFalse(service.isDue(workdayNine, LocalDate.of(2026, 10, 31), LocalTime.of(9, 0)));
        assertFalse(service.isDue(workdayNine, LocalDate.of(2026, 11, 1), LocalTime.of(9, 0)));
    }

    @Test
    @DisplayName("分钟不同 - 不触发")
    void testIsDue_WrongMinute() {
        assertFalse(service.isDue(workdayNine, LocalDate.of(2026, 10, 26), LocalTime.of(9, 1)));
        assertFalse(service.isDue(workdayNine, LocalDate.of(2026, 10, 26), LocalTime.of(8, 59)));
    }

    @Test
    @DisplayName("已停用 - 不触发")
    void testIsDue_Disabled() {
        assertFalse(service.isDue(schedule("12345", "09:00", false), LocalDate.of(2026, 10, 26), LocalTime.of(9, 0)));
    }

    @Test
    @DisplayName("周末计划(67)在周日触发")
    void testIsDue_WeekendSchedule() {
        assertTrue(service.isDue(schedule("67", "10:30", true), LocalDate.of(2026, 11, 1), LocalTime.of(10, 30)));
        assertFalse(service.isDue(schedule("67", "10:30", true), LocalDate.of(2026, 10, 26), LocalTime.of(10, 30)));
    }

    private BillSchedule schedule(String weekDays, String execTime, boolean enabled) {
        return BillSchedule.builder()
                .enabled(enabled)
                .weekDays(weekDays)
                .execTime(LocalTime.parse(execTime))
                .build();
    }
}
