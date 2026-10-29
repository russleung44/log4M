package com.tony.log4m.models.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tony.log4m.base.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * 定时记账
 *
 * @author Tony
 * @since 10/29/2026
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName(value = "bill_schedule", autoResultMap = true)
@EqualsAndHashCode(callSuper = true)
public class BillSchedule extends BaseEntity<BillSchedule> {

    @TableId
    private Long scheduleId;

    /**
     * Telegram chat id，触发时发送通知
     */
    private Long chatId;

    private String note;

    private Long categoryId;

    private String categoryName;

    private BigDecimal amount;

    /**
     * 触发时间（时:分）
     */
    private LocalTime execTime;

    /**
     * 周几触发，1=周一…7=周日，如 12345 表示工作日
     */
    private String weekDays;

    private Boolean enabled;
}
