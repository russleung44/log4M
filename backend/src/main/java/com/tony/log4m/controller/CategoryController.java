package com.tony.log4m.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tony.log4m.models.dto.CreateCategoryDto;
import com.tony.log4m.models.dto.UpdateCategoryDto;
import com.tony.log4m.models.entity.Category;
import com.tony.log4m.models.vo.ResultVO;
import com.tony.log4m.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理Controller
 * 
 * @author Tony
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 获取分页分类列表
     */
    @GetMapping
    public ResultVO<Page<Category>> pageQuery(
            @RequestParam(name = "current", defaultValue = "1") int current,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "keyword", required = false) String keyword) {
        Page<Category> page = new Page<>(current, size);

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<Category>()
                .like(keyword != null && !keyword.trim().isEmpty(), Category::getCategoryName, keyword)
                .orderByAsc(Category::getParentCategoryId)
                .orderByAsc(Category::getCategoryId);

        Page<Category> result = categoryService.page(page, queryWrapper);
        return ResultVO.success(result);
    }

    /**
     * 获取分类树
     */
    @GetMapping("/tree")
    public ResultVO<List<Category>> getCategoryTree() {
        List<Category> categories = categoryService.list();
        // TODO: 构建树形结构，这里暂时返回所有分类
        return ResultVO.success(categories);
    }

    /**
     * 获取所有分类
     */
    @GetMapping("/all")
    public ResultVO<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.list();
        return ResultVO.success(categories);
    }

    /**
     * 根据ID获取分类
     */
    @GetMapping("/{id}")
    public ResultVO<Category> getById(@PathVariable Long id) {
        Category category = categoryService.getById(id);
        if (category == null) {
            return ResultVO.error("分类不存在");
        }
        return ResultVO.success(category);
    }

    /**
     * 创建分类
     */
    @PostMapping
    public ResultVO<Category> create(@Valid @RequestBody CreateCategoryDto dto) {
        Category category = new Category();
        category.setCategoryName(dto.getCategoryName());
        category.setParentCategoryId(dto.getParentCategoryId());

        boolean saved = categoryService.save(category);
        if (saved) {
            return ResultVO.success(category);
        } else {
            return ResultVO.error("创建分类失败");
        }
    }

    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    public ResultVO<Boolean> update(@PathVariable Long id, @Valid @RequestBody UpdateCategoryDto dto) {
        Category category = categoryService.getById(id);
        if (category == null) {
            return ResultVO.error("分类不存在");
        }

        category.setCategoryName(dto.getCategoryName());
        category.setParentCategoryId(dto.getParentCategoryId());

        boolean updated = categoryService.updateById(category);
        return ResultVO.success(updated);
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public ResultVO<Boolean> delete(@PathVariable Long id) {
        // TODO: 检查是否有账单使用该分类
        boolean deleted = categoryService.removeById(id);
        return ResultVO.success(deleted);
    }
}