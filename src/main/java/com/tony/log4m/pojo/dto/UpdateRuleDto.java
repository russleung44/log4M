package com.tony.log4m.pojo.dto;

import com.tony.log4m.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 更新规则DTO
 * 
 * @author Tony
 */
@Data
public class UpdateRuleDto {
    
    @NotBlank(message = "规则名称不能为空")
    private String ruleName;
    
    @NotBlank(message = "关键词不能为空")
    private String keywords;
    
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;
    
    private BigDecimal amount;
    
    @NotNull(message = "交易类型不能为空")
    private TransactionType transactionType;
    
    @PositiveOrZero(message = "优先级必须大于等于0")
    private Integer priority = 0;
    
    private Boolean isActive = true;
}