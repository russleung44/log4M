package com.tony.log4m.models.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 定时记账保存DTO（创建/更新共用）
 *
 * @author Tony
 * @since 10/29/2026
 */
@Data
public class SaveBillScheduleDto {

    @NotBlank(message = "备注不能为空")
    private String note;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    @Digits(integer = 8, fraction = 2, message = "金额超出范围")
    private BigDecimal amount;

    @NotBlank(message = "触发时间不能为空")
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d(:[0-5]\\d)?$", message = "触发时间格式应为 HH:mm")
    private String execTime;

    @NotBlank(message = "周几不能为空")
    @Pattern(regexp = "[1-7]{1,7}", message = "周几应为1~7的数字")
    private String weekDays;

    private Boolean enabled = true;
}
