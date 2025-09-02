package com.tony.log4m.service;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tony.log4m.exception.Log4mException;
import com.tony.log4m.mapper.RuleMapper;
import com.tony.log4m.models.entity.Rule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
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

    // ========== 标准CRUD操作 ==========

    /**
     * 创建规则
     *
     * @param rule 规则信息
     * @return 创建的规则
     */
    public Rule create(Rule rule) {
        // 检查规则名是否重复
        if (this.lambdaQuery().eq(Rule::getRuleName, rule.getRuleName()).exists()) {
            throw new Log4mException("规则名称重复");
        }
        rule.insert();
        return rule;
    }

    /**
     * 根据ID更新规则（带数据验证）
     *
     * @param rule 规则信息
     * @return 更新结果
     */
    public Boolean updateByIdWithValidation(Rule rule) {
        // 检查规则名是否重复
        if (this.lambdaQuery().eq(Rule::getRuleName, rule.getRuleName())
                .ne(Rule::getRuleId, rule.getRuleId()).exists()) {
            throw new Log4mException("规则名称重复");
        }
        return rule.updateById();
    }

    /**
     * 根据ID删除规则（逻辑删除）
     *
     * @param id 规则ID
     * @return 删除结果
     */
    public Boolean deleteById(Serializable id) {
        Rule rule = this.getOptById(id).orElseThrow(() -> new Log4mException("规则不存在"));
        return this.removeById(id);
    }

    /**
     * 根据ID查询规则
     *
     * @param id 规则ID
     * @return 规则信息
     */
    public Rule getById(Serializable id) {
        return this.getOptById(id).orElseThrow(() -> new Log4mException("规则不存在"));
    }

    /**
     * 查询所有规则
     *
     * @return 规则列表
     */
    public List<Rule> listAll() {
        return this.lambdaQuery().orderByDesc(Rule::getCrTime).list();
    }

    /**
     * 分页查询规则
     *
     * @param current 当前页
     * @param size    页大小
     * @return 分页结果
     */
    public Page<Rule> pageQuery(int current, int size) {
        Page<Rule> page = new Page<>(current, size);
        return this.lambdaQuery().orderByDesc(Rule::getCrTime).page(page);
    }

    /**
     * 根据分类ID查询规则列表
     *
     * @param categoryId 分类ID
     * @return 规则列表
     */
    public List<Rule> getByCategoryId(Long categoryId) {
        return this.lambdaQuery().eq(Rule::getCategoryId, categoryId)
                .orderByDesc(Rule::getCrTime).list();
    }

    /**
     * 根据关键词搜索规则
     *
     * @param keyword 关键词
     * @return 规则列表
     */
    public List<Rule> searchByKeyword(String keyword) {
        return this.lambdaQuery().like(Rule::getKeywords, keyword)
                .orderByDesc(Rule::getCrTime).list();
    }
}
