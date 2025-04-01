package com.tony.log4m.service;


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

    public void insert(Rule rule) {
        //todo checkName
        super.save(rule);
    }

    public void update(Rule rule) {
        Optional.ofNullable(this.getById(rule.getId())).orElseThrow();
        // todo checkName
        rule.updateById();
    }


    public Optional<Rule> findByKeyword(String text) {
        return this.query().like("keywords", text).last("limit 1").oneOpt();
    }
}
