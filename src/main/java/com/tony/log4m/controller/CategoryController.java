package com.tony.log4m.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.tony.log4m.base.R;
import com.tony.log4m.pojo.dto.CategoryDTO;
import com.tony.log4m.pojo.entity.Category;
import com.tony.log4m.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.Serializable;


/**
 * @author Tony
 * @since 2022-09-23 15:46:48
 */
@Api(tags = {"分类"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/page")
    @ApiOperation("分类列表")
    public Page<CategoryDTO> page(@Valid @RequestBody PageDTO pageDTO) {
        return categoryService.page(pageDTO);
    }

    @GetMapping("/{id}")
    @ApiOperation("分类详情")
    public CategoryDTO get(@PathVariable Serializable id) {
        return categoryService.get(id);
    }

    @PostMapping
    @ApiOperation("新增分类")
    public CategoryDTO insert(@Valid @RequestBody Category category) {
        return categoryService.insert(category);
    }

    @PutMapping
    @ApiOperation("修改分类")
    public CategoryDTO update(@Valid @RequestBody Category category) {
        return categoryService.update(category);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除分类")
    public R delete(@PathVariable Serializable id) {
        categoryService.delete(id);
        return R.ok();
    }
}
