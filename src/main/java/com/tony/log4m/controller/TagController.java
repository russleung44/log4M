package com.tony.log4m.controller;

import com.github.pagehelper.PageInfo;
import com.tony.log4m.base.R;
import com.tony.log4m.pojo.dto.TagDTO;
import com.tony.log4m.service.TagService;
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
@Tag(name = "标签")
@RestController
@RequiredArgsConstructor
@RequestMapping("/tag")
public class TagController {

    private final TagService tagService;

    @GetMapping("/page/{pageNum}/{pageSize}")
    @Operation(summary = "标签列表")
    public PageInfo<TagDTO> page(@PathVariable Integer pageNum, @PathVariable Integer pageSize) {
        return tagService.page(pageNum, pageSize);
    }

    @GetMapping("/{id}")
    @Operation(summary = "标签详情")
    public TagDTO get(@PathVariable Serializable id) {
        return tagService.get(id);
    }

    @PostMapping
    @Operation(summary = "新增标签")
    public TagDTO insert(@Valid @RequestBody TagDTO tagDTO) {
        return tagService.insert(tagDTO);
    }

    @PutMapping
    @Operation(summary = "修改标签")
    public TagDTO update(@Valid @RequestBody TagDTO tagDTO) {
        return tagService.update(tagDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除标签")
    public R delete(@PathVariable Serializable id) {
        tagService.delete(id);
        return R.ok();
    }
}
