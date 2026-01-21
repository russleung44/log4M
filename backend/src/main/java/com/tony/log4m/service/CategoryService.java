package com.tony.log4m.service;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tony.log4m.exception.Log4mException;
import com.tony.log4m.mapper.CategoryMapper;
import com.tony.log4m.models.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tony
 * @since 2022-09-23 15:31:38
 */
@Service
@RequiredArgsConstructor
public class CategoryService extends ServiceImpl<CategoryMapper, Category> {

    private final BillService billService;

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

    public Category getDefaultCategory() {
        return this.lambdaQuery().eq(Category::getIsDefault, true).one();
    }

    // ========== 标准CRUD操作 ==========

    /**
     * 创建分类
     *
     * @param category 分类信息
     * @return 创建的分类
     */
    public Category create(Category category) {
        // 检查分类名是否重复
        if (this.lambdaQuery().eq(Category::getCategoryName, category.getCategoryName())
                .eq(Category::getParentCategoryId, category.getParentCategoryId()).exists()) {
            throw new Log4mException("同级分类名称重复");
        }
        category.insert();
        return category;
    }

    /**
     * 根据ID更新分类（带数据验证）
     *
     * @param category 分类信息
     * @return 更新结果
     */
    public Boolean updateByIdWithValidation(Category category) {
        // 检查分类名是否重复
        if (this.lambdaQuery().eq(Category::getCategoryName, category.getCategoryName())
                .eq(Category::getParentCategoryId, category.getParentCategoryId())
                .ne(Category::getCategoryId, category.getCategoryId()).exists()) {
            throw new Log4mException("同级分类名称重复");
        }
        return category.updateById();
    }

    /**
     * 根据ID删除分类（逻辑删除）
     *
     * @param id 分类ID
     * @return 删除结果
     */
    public Boolean deleteById(Serializable id) {
        Category category = this.getOptById(id).orElseThrow(() -> new Log4mException("分类不存在"));
        if (Boolean.TRUE.equals(category.getIsDefault())) {
            throw new Log4mException("默认分类不能删除");
        }
        // 检查是否有子分类
        if (this.lambdaQuery().eq(Category::getParentCategoryId, id).exists()) {
            throw new Log4mException("存在子分类，不能删除");
        }
        // 检查是否有关联的账单
        if (billService.lambdaQuery().eq(com.tony.log4m.models.entity.Bill::getCategoryId, id).exists()) {
            throw new Log4mException("该分类已关联账单，不能删除");
        }
        return this.removeById(id);
    }

    /**
     * 根据ID查询分类
     *
     * @param id 分类ID
     * @return 分类信息
     */
    public Category getById(Serializable id) {
        return this.getOptById(id).orElseThrow(() -> new Log4mException("分类不存在"));
    }

    /**
     * 查询所有分类
     *
     * @return 分类列表
     */
    public List<Category> listAll() {
        return this.lambdaQuery().orderByAsc(Category::getSort).orderByDesc(Category::getCrTime).list();
    }

    /**
     * 分页查询分类
     *
     * @param current 当前页
     * @param size    页大小
     * @return 分页结果
     */
    public Page<Category> pageQuery(int current, int size) {
        Page<Category> page = new Page<>(current, size);
        return this.lambdaQuery().orderByAsc(Category::getSort).orderByDesc(Category::getCrTime).page(page);
    }

    /**
     * 根据父分类ID查询子分类列表
     *
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    public List<Category> getByParentId(Long parentId) {
        return this.lambdaQuery().eq(Category::getParentCategoryId, parentId)
                .orderByAsc(Category::getSort).orderByDesc(Category::getCrTime).list();
    }

    /**
     * 获取所有根分类
     *
     * @return 根分类列表
     */
    public List<Category> getRootCategories() {
        return this.lambdaQuery().isNull(Category::getParentCategoryId)
                .orderByAsc(Category::getSort).orderByDesc(Category::getCrTime).list();
    }
}
