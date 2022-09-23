package com.tony.log4m.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.tony.log4m.base.R;
import com.tony.log4m.pojo.dto.TagDTO;
import com.tony.log4m.pojo.entity.Tag;
import com.tony.log4m.service.TagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.Serializable;


/**
 * @author Tony
 * @since 2022-09-23 15:46:49
 */
@Api(tags = {"标签"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/tag")
public class TagController {

    private final TagService tagService;

    @PostMapping("/page")
    @ApiOperation("标签列表")
    public Page<TagDTO> page(@Valid @RequestBody PageDTO pageDTO) {
        return tagService.page(pageDTO);
    }

    @GetMapping("/{id}")
    @ApiOperation("标签详情")
    public TagDTO get(@PathVariable Serializable id) {
        return tagService.get(id);
    }

    @PostMapping
    @ApiOperation("新增标签")
    public TagDTO insert(@Valid @RequestBody Tag tag) {
        return tagService.insert(tag);
    }

    @PutMapping
    @ApiOperation("修改标签")
    public TagDTO update(@Valid @RequestBody Tag tag) {
        return tagService.update(tag);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除标签")
    public R delete(@PathVariable Serializable id) {
        tagService.delete(id);
        return R.ok();
    }
}
