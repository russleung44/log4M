package com.tony.log4m.service;


import com.tony.log4m.base.CrudServiceImpl;
import com.tony.log4m.convert.RuleConvert;
import com.tony.log4m.dao.RuleDao;
import com.tony.log4m.pojo.dto.RuleDTO;
import com.tony.log4m.pojo.entity.Rule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Optional;

/**
 * @author Tony
 * @since 2022-09-23 15:31:38
 */
@Service
@RequiredArgsConstructor
public class RuleService extends CrudServiceImpl<RuleDao, Rule, RuleDTO, RuleConvert> {

    @Override
    public RuleDTO insert(Rule rule) {
        super.checkName(rule.getName(), null);
        return super.insert(rule);
    }

    @Override
    public RuleDTO update(Rule rule) {
        Optional.ofNullable(this.getById(rule.getId())).orElseThrow();
        super.checkName(rule.getName(), rule.getId());
        return super.update(rule);
    }

    @Override
    public RuleDTO get(Serializable id) {
        return super.get(id);
    }

    @Override
    public void delete(Serializable id) {
        super.delete(id);
    }

    public Optional<Rule> findByKeyword(Integer userId, String text) {
        return this.query().eq("user_id", userId).like("keywords", text).last("limit 1").oneOpt();
    }
}
