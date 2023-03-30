package com.tony.log4m.service.impl;

import com.tony.log4m.pojo.entity.Rule;
import com.tony.log4m.service.RuleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author TonyLeung
 * @since 2022/10/10
 */
@SpringBootTest
class RuleServiceTest {

    @Autowired
    private RuleService ruleService;


    @Test
    void add() {
        Rule rule = Rule.builder()
                .build();
    }

}