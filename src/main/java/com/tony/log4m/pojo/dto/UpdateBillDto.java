package com.tony.log4m.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tony.log4m.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 更新账单DTO
 * 
 * @author Tony
 */
@Data
public class UpdateBillDto {
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "账单日期不能为空")
    private LocalDate billDate;
    
    @NotNull(message = "金额不能为空")
    @Positive(message = "金额必须大于0")
    private BigDecimal amount;
    
    @NotBlank(message = "备注不能为空")
    private String note;
    
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;
    
    @NotNull(message = "账户ID不能为空")
    private Long accountId;
    
    private Long tagId;
    
    @NotNull(message = "交易类型不能为空")
    private TransactionType transactionType;
}