package com.tony.log4m.pojo.dto;

import com.tony.log4m.enums.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新分类DTO
 * 
 * @author Tony
 */
@Data
public class UpdateCategoryDto {
    
    @NotBlank(message = "分类名称不能为空")
    private String categoryName;
    
    private Long parentCategoryId;
    
    @NotNull(message = "分类类型不能为空")
    private CategoryType categoryType;
    
    private String icon;
    
    private String color;
    
    private String description;
}