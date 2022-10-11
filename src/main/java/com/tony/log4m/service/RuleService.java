package com.tony.log4m.service;

import com.tony.log4m.base.CrudService;
import com.tony.log4m.pojo.dto.RuleDTO;
import com.tony.log4m.pojo.entity.Rule;

import java.util.Optional;

/**
 * @author Tony
 * @since 2022-09-23 15:25:10
 */
public interface RuleService extends CrudService<Rule, RuleDTO> {

    Optional<Rule> findByKeyword(Integer userId, String text);
}
