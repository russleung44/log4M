package com.tony.log4m.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tony.log4m.pojo.entity.Category;
import com.tony.log4m.pojo.vo.ResultVO;
import com.tony.log4m.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 分类控制器
 *
 * @author Tony
 * @since 2025-09-02
 */
@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 创建分类
     */
    @PostMapping
    public ResultVO<Category> create(@RequestBody @Valid Category category) {
        Category result = categoryService.create(category);
        return ResultVO.ok(result);
    }

    /**
     * 根据ID获取分类
     */
    @GetMapping("/{id}")
    public ResultVO<Category> getById(@PathVariable Long id) {
        Category result = categoryService.getById(id);
        return ResultVO.ok(result);
    }

    /**
     * 根据ID更新分类
     */
    @PutMapping("/{id}")
    public ResultVO<Boolean> updateById(@PathVariable Long id, @RequestBody @Valid Category category) {
        category.setCategoryId(id);
        Boolean result = categoryService.updateByIdWithValidation(category);
        return ResultVO.ok(result);
    }

    /**
     * 根据ID删除分类
     */
    @DeleteMapping("/{id}")
    public ResultVO<Boolean> deleteById(@PathVariable Long id) {
        Boolean result = categoryService.deleteById(id);
        return ResultVO.ok(result);
    }

    /**
     * 获取所有分类
     */
    @GetMapping("/list")
    public ResultVO<List<Category>> list() {
        List<Category> result = categoryService.listAll();
        return ResultVO.ok(result);
    }

    /**
     * 分页获取分类
     */
    @GetMapping("/page/{current}/{size}")
    public ResultVO<Page<Category>> page(@PathVariable int current, @PathVariable int size) {
        Page<Category> result = categoryService.pageQuery(current, size);
        return ResultVO.ok(result);
    }

    /**
     * 获取根分类列表
     */
    @GetMapping("/roots")
    public ResultVO<List<Category>> getRootCategories() {
        List<Category> result = categoryService.getRootCategories();
        return ResultVO.ok(result);
    }

    /**
     * 获取指定分类的子分类
     */
    @GetMapping("/{parentId}/children")
    public ResultVO<List<Category>> getByParentId(@PathVariable Long parentId) {
        List<Category> result = categoryService.getByParentId(parentId);
        return ResultVO.ok(result);
    }

    /**
     * 获取分类详情
     */
    @GetMapping("/{id}/details")
    public ResultVO<String> getCategoryDetails(@PathVariable String id) {
        String result = categoryService.buildCategoryDetails(id);
        return ResultVO.ok(result);
    }

    /**
     * 创建或获取分类
     */
    @PostMapping("/get-or-create")
    public ResultVO<Category> getOrCreate(@RequestParam String categoryName) {
        Category result = categoryService.getOrCreate(categoryName);
        return ResultVO.ok(result);
    }

    /**
     * 获取默认分类
     */
    @GetMapping("/default")
    public ResultVO<Category> getDefaultCategory() {
        Category result = categoryService.getDefaultCategory();
        return ResultVO.ok(result);
    }
}