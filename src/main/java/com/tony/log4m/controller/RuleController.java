package com.tony.log4m.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.tony.log4m.base.R;
import com.tony.log4m.pojo.dto.RuleDTO;
import com.tony.log4m.pojo.entity.Rule;
import com.tony.log4m.service.RuleService;
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
@Api(tags = {"规则"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/rule")
public class RuleController {

    private final RuleService ruleService;

    @PostMapping("/page")
    @ApiOperation("规则列表")
    public Page<RuleDTO> page(@Valid @RequestBody PageDTO pageDTO) {
        return ruleService.page(pageDTO);
    }

    @GetMapping("/{id}")
    @ApiOperation("规则详情")
    public RuleDTO get(@PathVariable Serializable id) {
        return ruleService.get(id);
    }

    @PostMapping
    @ApiOperation("新增规则")
    public RuleDTO insert(@Valid @RequestBody Rule rule) {
        return ruleService.insert(rule);
    }

    @PutMapping
    @ApiOperation("修改规则")
    public RuleDTO update(@Valid @RequestBody Rule rule) {
        return ruleService.update(rule);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除规则")
    public R delete(@PathVariable Serializable id) {
        ruleService.delete(id);
        return R.ok();
    }
}
