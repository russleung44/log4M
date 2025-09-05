package com.tony.log4m.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tony.log4m.models.entity.Tag;
import com.tony.log4m.models.vo.ResultVO;
import com.tony.log4m.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 标签控制器
 *
 * @author Tony
 * @since 2025-09-02
 */
@RestController
@RequestMapping("/tag")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    /**
     * 创建标签
     */
    @PostMapping
    public ResultVO<Tag> create(@RequestBody @Valid Tag tag) {
        Tag result = tagService.create(tag);
        return ResultVO.ok(result);
    }

    /**
     * 根据ID获取标签
     */
    @GetMapping("/{id}")
    public ResultVO<Tag> getById(@PathVariable Long id) {
        Tag result = tagService.getById(id);
        return ResultVO.ok(result);
    }

    /**
     * 根据ID更新标签
     */
    @PutMapping("/{id}")
    public ResultVO<Boolean> updateById(@PathVariable Long id, @RequestBody @Valid Tag tag) {
        tag.setTagId(id);
        Boolean result = tagService.updateByIdWithValidation(tag);
        return ResultVO.ok(result);
    }

    /**
     * 根据ID删除标签
     */
    @DeleteMapping("/{id}")
    public ResultVO<Boolean> deleteById(@PathVariable Long id) {
        Boolean result = tagService.deleteById(id);
        return ResultVO.ok(result);
    }

    /**
     * 获取所有标签
     */
    @GetMapping("/list")
    public ResultVO<List<Tag>> list() {
        List<Tag> result = tagService.listAll();
        return ResultVO.ok(result);
    }

    /**
     * 分页获取标签
     */
    @GetMapping("/page/{current}/{size}")
    public ResultVO<Page<Tag>> page(@PathVariable int current, @PathVariable int size) {
        Page<Tag> result = tagService.pageQuery(current, size);
        return ResultVO.ok(result);
    }

    /**
     * 按标签名称搜索
     */
    @GetMapping("/search")
    public ResultVO<List<Tag>> searchByName(@RequestParam String tagName) {
        List<Tag> result = tagService.searchByName(tagName);
        return ResultVO.ok(result);
    }

    /**
     * 获取或创建标签
     */
    @PostMapping("/get-or-create")
    public ResultVO<Tag> getOrCreate(@RequestParam String tagName) {
        Tag result = tagService.getOrCreate(tagName);
        return ResultVO.ok(result);
    }
}