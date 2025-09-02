package com.tony.log4m.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tony.log4m.pojo.dto.CreateRuleDto;
import com.tony.log4m.pojo.dto.UpdateRuleDto;
import com.tony.log4m.pojo.entity.Rule;
import com.tony.log4m.pojo.vo.ResultVO;
import com.tony.log4m.service.RuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 规则管理Controller
 * 
 * @author Tony
 */
@RestController
@RequestMapping("/api/rules")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RuleController {

    private final RuleService ruleService;

    /**
     * 分页查询规则
     */
    @GetMapping
    public ResultVO<Page<Rule>> pageQuery(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isActive
    ) {
        Page<Rule> page = new Page<>(current, size);
        
        LambdaQueryWrapper<Rule> queryWrapper = new LambdaQueryWrapper<Rule>()
                .like(keyword != null && !keyword.trim().isEmpty(), Rule::getRuleName, keyword)
                .or()
                .like(keyword != null && !keyword.trim().isEmpty(), Rule::getKeywords, keyword)
                .orderByDesc(Rule::getSort)
                .orderByDesc(Rule::getRuleId);
        
        Page<Rule> result = ruleService.page(page, queryWrapper);
        return ResultVO.success(result);
    }

    /**
     * 根据ID获取规则
     */
    @GetMapping("/{id}")
    public ResultVO<Rule> getById(@PathVariable Long id) {
        Rule rule = ruleService.getById(id);
        if (rule == null) {
            return ResultVO.error("规则不存在");
        }
        return ResultVO.success(rule);
    }

    /**
     * 创建规则
     */
    @PostMapping
    public ResultVO<Rule> create(@Valid @RequestBody CreateRuleDto dto) {
        Rule rule = new Rule();
        rule.setRuleName(dto.getRuleName());
        rule.setKeywords(dto.getKeywords());
        rule.setCategoryId(dto.getCategoryId());
        rule.setAmount(dto.getAmount());
        rule.setTransactionType(dto.getTransactionType());
        
        boolean saved = ruleService.save(rule);
        if (saved) {
            return ResultVO.success(rule);
        } else {
            return ResultVO.error("创建规则失败");
        }
    }

    /**
     * 更新规则
     */
    @PutMapping("/{id}")
    public ResultVO<Boolean> update(@PathVariable Long id, @Valid @RequestBody UpdateRuleDto dto) {
        Rule rule = ruleService.getById(id);
        if (rule == null) {
            return ResultVO.error("规则不存在");
        }
        
        rule.setRuleName(dto.getRuleName());
        rule.setKeywords(dto.getKeywords());
        rule.setCategoryId(dto.getCategoryId());
        rule.setAmount(dto.getAmount());
        rule.setTransactionType(dto.getTransactionType());
        
        boolean updated = ruleService.updateById(rule);
        return ResultVO.success(updated);
    }

    /**
     * 删除规则
     */
    @DeleteMapping("/{id}")
    public ResultVO<Boolean> delete(@PathVariable Long id) {
        boolean deleted = ruleService.removeById(id);
        return ResultVO.success(deleted);
    }

    /**
     * 测试规则匹配
     */
    @PostMapping("/test")
    public ResultVO<Map<String, Object>> testRule(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        if (text == null || text.trim().isEmpty()) {
            return ResultVO.error("测试文本不能为空");
        }
        
        // TODO: 实现规则匹配逻辑
        Map<String, Object> result = new HashMap<>();
        result.put("text", text);
        result.put("matched", false);
        result.put("matchedRule", null);
        result.put("message", "规则匹配功能待实现");
        
        return ResultVO.success(result);
    }

    /**
     * 启用/禁用规则
     */
    @PutMapping("/{id}/toggle")
    public ResultVO<Boolean> toggleRule(@PathVariable Long id) {
        // TODO: 实现规则切换功能
        // Rule rule = ruleService.getById(id);
        // if (rule == null) {
        //     return ResultVO.error("规则不存在");
        // }
        // 
        // rule.setIsActive(!rule.getIsActive());
        // boolean updated = ruleService.updateById(rule);
        return ResultVO.success(true);
    }
}