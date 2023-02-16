package com.tony.log4m;

import com.tony.log4m.enums.TransactionType;
import com.tony.log4m.pojo.entity.Account;
import com.tony.log4m.pojo.entity.Category;
import com.tony.log4m.pojo.entity.Rule;
import com.tony.log4m.pojo.entity.Tag;
import com.tony.log4m.pojo.entity.User;
import com.tony.log4m.service.impl.AccountServiceImpl;
import com.tony.log4m.service.impl.CategoryServiceImpl;
import com.tony.log4m.service.impl.RuleServiceImpl;
import com.tony.log4m.service.impl.TagServiceImpl;
import com.tony.log4m.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

/**
 * @author TonyLeung
 * @date 2022/10/10
 */
@SpringBootTest
class InitTest {
    @Autowired
    private TagServiceImpl tagService;
    @Autowired
    private RuleServiceImpl ruleService;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private AccountServiceImpl accountService;
    @Autowired
    private CategoryServiceImpl categoryService;


    @Test
    void init() {
        User user = User.builder()
                .username("tony")
                .email("russleung44@gmail.com")
                .status(1)
                .build();
        user.setId(10001).insert();

        Account account = Account.builder()
                .accountName("零花钱")
                .userId(10001)
                .balance(BigDecimal.valueOf(1500))
                .consumeLimit(BigDecimal.valueOf(2000))
                .build();
        account.insert();

        Tag tag = Tag.builder()
                .userId(10001)
                .name("吃的")
                .build();
        tag.insert();

        Tag tag2 = Tag.builder()
                .userId(10001)
                .name("喝的")
                .build();
        tag2.insert();

        Category category = Category.builder()
                .userId(10001)
                .name("饮食")
                .build();
        category.insert();

        Category category2 = Category.builder()
                .userId(10001)
                .name("红包")
                .build();
        category2.insert();

        Rule rule = Rule.builder()
                .name("咖啡")
                .amount(BigDecimal.valueOf(14.5))
                .userId(10001)
                .keywords("咖啡,生椰")
                .tagId(tag2.getId())
                .categoryId(category.getId())
                .transactionType(TransactionType.CONSUME.getType())
                .build();
        rule.insert();

        Rule rule2 = Rule.builder()
                .name("红包")
                .amount(BigDecimal.valueOf(20))
                .userId(10001)
                .keywords("红包")
                .categoryId(category2.getId())
                .transactionType(TransactionType.CONSUME.getType())
                .build();
        rule2.insert();

        Rule rule3 = Rule.builder()
                .name("濑粉")
                .amount(BigDecimal.valueOf(20))
                .userId(10001)
                .keywords("濑粉")
                .tagId(tag.getId())
                .categoryId(category.getId())
                .transactionType(TransactionType.CONSUME.getType())
                .build();
        rule3.insert();
    }
}
