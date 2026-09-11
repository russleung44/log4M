package com.tony.log4m.models.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tony.log4m.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 自动分类关键词。
 */
@Data
@Accessors(chain = true)
@TableName("category_keyword")
@EqualsAndHashCode(callSuper = true)
public class CategoryKeyword extends BaseEntity<CategoryKeyword> {

    @TableId
    private Long categoryKeywordId;

    private String categoryName;

    private String keyword;

    private Boolean ignoreCase;

    private Integer sort;
}
