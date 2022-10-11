package com.tony.log4m.service.impl;

import com.tony.log4m.pojo.entity.Rule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author TonyLeung
 * @date 2022/10/10
 */
@SpringBootTest
class RuleServiceImplTest {

    @Autowired
    private RuleServiceImpl ruleService;


    @Test
    void add() {
        Rule rule = Rule.builder()
                .build();
    }

}