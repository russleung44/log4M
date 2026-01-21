package com.tony.log4m.models.dto;

import com.tony.log4m.enums.CategoryType;
import jakarta.validation.constraints.NotBlank;
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

    private CategoryType categoryType;

    private String icon;

    private String color;

    private String description;
}