package com.tony.log4m.bots.commands;

import com.pengrad.telegrambot.request.SendMessage;
import com.tony.log4m.bots.enums.Command;
import com.tony.log4m.models.entity.CategoryKeyword;
import com.tony.log4m.service.CategoryKeywordService;
import com.tony.log4m.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryCommandTest {

    private static final Long CHAT_ID = 1L;

    @Mock
    private CategoryService categoryService;

    @Mock
    private CategoryKeywordService categoryKeywordService;

    private CategoryCommand categoryCommand;

    @BeforeEach
    void setUp() {
        categoryCommand = new CategoryCommand(categoryService, categoryKeywordService);
    }

    @Test
    void testAddCategoryKeyword_WhenCreated() {
        CategoryKeyword mapping = new CategoryKeyword().setCategoryName("交通").setKeyword("滴滴");
        when(categoryKeywordService.addKeyword("交通", "滴滴"))
                .thenReturn(new CategoryKeywordService.KeywordAddResult(
                        CategoryKeywordService.KeywordAddStatus.CREATED, mapping));

        SendMessage message = categoryCommand.execute(
                Command.CATEGORY_KEYWORD_ADD, "交通-滴滴", CHAT_ID);

        assertTrue(textOf(message).contains("分类关键词添加成功"));
        verify(categoryService).getOrCreate("交通");
    }

    @Test
    void testAddCategoryKeyword_WhenParamIsInvalid() {
        SendMessage message = categoryCommand.execute(
                Command.CATEGORY_KEYWORD_ADD, "交通", CHAT_ID);

        assertTrue(textOf(message).contains("/category_keyword_add/分类名-关键词"));
    }

    @Test
    void testAddCategoryKeyword_WhenKeywordConflicts() {
        CategoryKeyword mapping = new CategoryKeyword().setCategoryName("出行").setKeyword("滴滴");
        when(categoryKeywordService.addKeyword("交通", "滴滴"))
                .thenReturn(new CategoryKeywordService.KeywordAddResult(
                        CategoryKeywordService.KeywordAddStatus.CONFLICT, mapping));

        SendMessage message = categoryCommand.execute(
                Command.CATEGORY_KEYWORD_ADD, "交通-滴滴", CHAT_ID);

        assertTrue(textOf(message).contains("关键词已属于其他分类：出行"));
    }

    private String textOf(SendMessage message) {
        return String.valueOf(message.getParameters().get("text"));
    }
}
