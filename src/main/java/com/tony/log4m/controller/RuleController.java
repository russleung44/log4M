package com.tony.log4m.controller;

import com.github.pagehelper.PageInfo;
import com.tony.log4m.base.R;
import com.tony.log4m.pojo.dto.RuleDTO;
import com.tony.log4m.service.RuleService;
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
@Api(tags = {"规则"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/rule")
public class RuleController {

    private final RuleService ruleService;

    @GetMapping("/page/{pageNum}/{pageSize}")
    @ApiOperation("规则列表")
    public PageInfo<RuleDTO> page(@PathVariable Integer pageNum, @PathVariable Integer pageSize) {
        return ruleService.page(pageNum, pageSize);
    }

    @GetMapping("/{id}")
    @ApiOperation("规则详情")
    public RuleDTO get(@PathVariable Serializable id) {
        return ruleService.get(id);
    }

    @PostMapping
    @ApiOperation("新增规则")
    public RuleDTO insert(@Valid @RequestBody RuleDTO ruleDTO) {
        return ruleService.insert(ruleDTO);
    }

    @PutMapping
    @ApiOperation("修改规则")
    public RuleDTO update(@Valid @RequestBody RuleDTO ruleDTO) {
        return ruleService.update(ruleDTO);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除规则")
    public R delete(@PathVariable Serializable id) {
        ruleService.delete(id);
        return R.ok();
    }
}
