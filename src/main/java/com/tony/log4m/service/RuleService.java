package com.tony.log4m.service;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tony.log4m.mapper.RuleMapper;
import com.tony.log4m.pojo.entity.Rule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Tony
 * @since 2022-09-23 15:31:38
 */
@Service
@RequiredArgsConstructor
public class RuleService extends ServiceImpl<RuleMapper, Rule> {

    private final CategoryService categoryService;

    public Optional<Rule> findByKeyword(String text) {
        return this.lambdaQuery().like(Rule::getKeywords, text).last("limit 1").oneOpt();
    }

    public String buildRuleDetails(String ruleId) {
        Rule rule = getOptById(ruleId).orElseThrow();
        return buildRuleDetails(rule);
    }

    public String buildRuleDetails(Rule rule) {
        String ruleTemp = """
                规则详情
                --------
                名称:     {}
                关键词:   {}
                分类:     {}
                类型:     {}
                金额:     {}
                """;

        return StrUtil.format(
                ruleTemp,
                rule.getRuleName(),
                rule.getKeywords(),
                categoryService.getCategoryName(rule.getCategoryId()),
                rule.getTransactionType().getType(),
                rule.getAmount());
    }
}
