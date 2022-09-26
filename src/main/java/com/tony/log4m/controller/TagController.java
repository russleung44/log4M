package com.tony.log4m.controller;

import com.github.pagehelper.PageInfo;
import com.tony.log4m.base.R;
import com.tony.log4m.pojo.dto.TagDTO;
import com.tony.log4m.service.TagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.Serializable;


/**
 * @author Tony
 * @since 2022-09-26 12:06:50
 */
@Api(tags = {"标签"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/tag")
public class TagController {

    private final TagService tagService;

    @GetMapping("/page/{pageNum}/{pageSize}")
    @ApiOperation("标签列表")
    public PageInfo<TagDTO> page(@PathVariable Integer pageNum, @PathVariable Integer pageSize) {
        return tagService.page(pageNum, pageSize);
    }

    @GetMapping("/{id}")
    @ApiOperation("标签详情")
    public TagDTO get(@PathVariable Serializable id) {
        return tagService.get(id);
    }

    @PostMapping
    @ApiOperation("新增标签")
    public TagDTO insert(@Valid @RequestBody TagDTO tagDTO) {
        return tagService.insert(tagDTO);
    }

    @PutMapping
    @ApiOperation("修改标签")
    public TagDTO update(@Valid @RequestBody TagDTO tagDTO) {
        return tagService.update(tagDTO);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除标签")
    public R delete(@PathVariable Serializable id) {
        tagService.delete(id);
        return R.ok();
    }
}
