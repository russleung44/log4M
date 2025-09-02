package com.tony.log4m.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tony.log4m.pojo.entity.Rule;
import com.tony.log4m.pojo.vo.ResultVO;
import com.tony.log4m.service.RuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * 规则控制器
 *
 * @author Tony
 * @since 2025-09-02
 */
@RestController
@RequestMapping("/rule")
@RequiredArgsConstructor
public class RuleController {

    private final RuleService ruleService;

    /**
     * 创建规则
     */
    @PostMapping
    public ResultVO<Rule> create(@RequestBody @Valid Rule rule) {
        Rule result = ruleService.create(rule);
        return ResultVO.ok(result);
    }

    /**
     * 根据ID获取规则
     */
    @GetMapping("/{id}")
    public ResultVO<Rule> getById(@PathVariable Long id) {
        Rule result = ruleService.getById(id);
        return ResultVO.ok(result);
    }

    /**
     * 根据ID更新规则
     */
    @PutMapping("/{id}")
    public ResultVO<Boolean> updateById(@PathVariable Long id, @RequestBody @Valid Rule rule) {
        rule.setRuleId(id);
        Boolean result = ruleService.updateByIdWithValidation(rule);
        return ResultVO.ok(result);
    }

    /**
     * 根据ID删除规则
     */
    @DeleteMapping("/{id}")
    public ResultVO<Boolean> deleteById(@PathVariable Long id) {
        Boolean result = ruleService.deleteById(id);
        return ResultVO.ok(result);
    }

    /**
     * 获取所有规则
     */
    @GetMapping("/list")
    public ResultVO<List<Rule>> list() {
        List<Rule> result = ruleService.listAll();
        return ResultVO.ok(result);
    }

    /**
     * 分页获取规则
     */
    @GetMapping("/page/{current}/{size}")
    public ResultVO<Page<Rule>> page(@PathVariable int current, @PathVariable int size) {
        Page<Rule> result = ruleService.pageQuery(current, size);
        return ResultVO.ok(result);
    }

    /**
     * 根据关键词搜索规则
     */
    @GetMapping("/search")
    public ResultVO<List<Rule>> searchByKeyword(@RequestParam String keyword) {
        List<Rule> result = ruleService.searchByKeyword(keyword);
        return ResultVO.ok(result);
    }

    /**
     * 根据关键词查找匹配的规则
     */
    @GetMapping("/find/{keyword}")
    public ResultVO<Rule> findByKeyword(@PathVariable String keyword) {
        Optional<Rule> result = ruleService.findByKeyword(keyword);
        return ResultVO.ok(result.orElse(null));
    }

    /**
     * 获取规则详情
     */
    @GetMapping("/{id}/details")
    public ResultVO<String> getRuleDetails(@PathVariable String id) {
        String result = ruleService.buildRuleDetails(id);
        return ResultVO.ok(result);
    }

    /**
     * 根据分类ID获取规则列表
     */
    @GetMapping("/category/{categoryId}")
    public ResultVO<List<Rule>> getByCategoryId(@PathVariable Long categoryId) {
        List<Rule> result = ruleService.getByCategoryId(categoryId);
        return ResultVO.ok(result);
    }
}