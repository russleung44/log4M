package com.tony.log4m.controller;

import com.tony.log4m.base.R;
import com.tony.log4m.pojo.entity.Category;
import com.tony.log4m.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.Serializable;


/**
 * @author Tony
 * @since 2022-09-26 12:06:50
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;


    @GetMapping("/{id}")
    public Category get(@PathVariable Serializable id) {
        return categoryService.getById(id);
    }

    @PostMapping
    public void insert(@Valid @RequestBody Category categoryDTO) {
        categoryService.insert(categoryDTO);
    }

    @PutMapping
    public void update(@Valid @RequestBody Category categoryDTO) {
        categoryService.update(categoryDTO);
    }

    @DeleteMapping("/{id}")
    public R delete(@PathVariable Serializable id) {
        categoryService.removeById(id);
        return R.ok();
    }
}
