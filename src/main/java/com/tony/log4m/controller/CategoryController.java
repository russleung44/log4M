package com.tony.log4m.controller;

import com.github.pagehelper.PageInfo;
import com.tony.log4m.base.R;
import com.tony.log4m.pojo.dto.CategoryDTO;
import com.tony.log4m.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.Serializable;


/**
 * @author Tony
 * @since 2022-09-26 12:06:50
 */
@Tag(name = "记录分类")
@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/page/{pageNum}/{pageSize}")
    @Operation(summary = "分类列表")
    public PageInfo<CategoryDTO> page(@PathVariable Integer pageNum, @PathVariable Integer pageSize) {
        return categoryService.page(pageNum, pageSize);
    }

    @GetMapping("/{id}")
    @Operation(summary = "分类详情")
    public CategoryDTO get(@PathVariable Serializable id) {
        return categoryService.get(id);
    }

    @PostMapping
    @Operation(summary = "新增分类")
    public CategoryDTO insert(@Valid @RequestBody CategoryDTO categoryDTO) {
        return categoryService.insert(categoryDTO);
    }

    @PutMapping
    @Operation(summary = "修改分类")
    public CategoryDTO update(@Valid @RequestBody CategoryDTO categoryDTO) {
        return categoryService.update(categoryDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除分类")
    public R delete(@PathVariable Serializable id) {
        categoryService.delete(id);
        return R.ok();
    }
}
