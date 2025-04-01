package com.tony.log4m.controller;

import com.tony.log4m.pojo.entity.Category;
import com.tony.log4m.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public void insert(@Valid @RequestBody Category category) {
        categoryService.insert(category);
    }

    @PutMapping("/{id}")
    public void update(@Valid @RequestBody Category category) {
        categoryService.update(category);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Serializable id) {
        categoryService.removeById(id);
        return ResponseEntity.ok().build();
    }
}
