package com.tony.log4m.service;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tony.log4m.mapper.CategoryMapper;
import com.tony.log4m.pojo.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Tony
 * @since 2022-09-23 15:31:38
 */
@Service
@RequiredArgsConstructor
public class CategoryService extends ServiceImpl<CategoryMapper, Category> {

    public Category getOrCreate(String categoryName) {
        return this.lambdaQuery().eq(Category::getCategoryName, categoryName)
                .oneOpt().orElseGet(() -> createCategory(categoryName));
    }

    private Category createCategory(String name) {
        Category category = new Category();
        category.setCategoryName(name);
        category.insert();
        return category;
    }

    public String getCategoryName(Long categoryId) {
        return getOptById(categoryId)
                .map(Category::getCategoryName)
                .orElse("未分类");
    }

    public String buildCategoryDetails(String categoryId) {
        Category category = getOptById(categoryId).orElseThrow();

        String template = """
                分类详情
                名称: {}
                父类: {}
                """;

        return StrUtil.format(
                template,
                category.getCategoryName(),
                getCategoryName(category.getParentCategoryId())
        );
    }
}
