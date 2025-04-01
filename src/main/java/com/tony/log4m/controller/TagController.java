package com.tony.log4m.controller;

import com.tony.log4m.pojo.entity.Tag;
import com.tony.log4m.service.TagService;
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
@RequestMapping("/tag")
public class TagController {

    private final TagService tagService;


    @GetMapping("/{id}")
    public Tag get(@PathVariable Serializable id) {
        return tagService.getById(id);
    }

    @PostMapping
    public void insert(@Valid @RequestBody Tag tag) {
        tagService.save(tag);
    }

    @PutMapping
    public void update(@Valid @RequestBody Tag tag) {
        tagService.updateById(tag);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Serializable id) {
        tagService.removeById(id);
        return ResponseEntity.ok().build();
    }
}
