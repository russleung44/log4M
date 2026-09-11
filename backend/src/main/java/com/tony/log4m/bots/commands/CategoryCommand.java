package com.tony.log4m.bots.commands;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.tony.log4m.bots.enums.Command;
import com.tony.log4m.models.entity.Category;
import com.tony.log4m.service.CategoryKeywordService;
import com.tony.log4m.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Tony
 * @since 4/12/2025
 */
@Component
@RequiredArgsConstructor
public class CategoryCommand implements CommandStrategy {

    private final CategoryService categoryService;
    private final CategoryKeywordService categoryKeywordService;

    @Override
    @Transactional
    public SendMessage execute(Command command, String param, Long chatId) {
        switch (command) {
            case CATEGORIES -> {
                return getCategories(chatId);
            }

            case CATEGORY_ADD -> {
                return addCategory(param.split("-", 2), chatId);
            }

            case CATEGORY_KEYWORD_ADD -> {
                return addCategoryKeyword(param, chatId);
            }

            case CATEGORY_DEFAULT -> {
                Category category = categoryService.getOrCreate(param);
                category.setIsDefault(true).insertOrUpdate();
                return new SendMessage(chatId, "设置成功");
            }

            default -> {
                return new SendMessage(chatId, "未识别的命令");
            }
        }
    }

    /**
     * 获取分类列表消息（带 inline 按钮）
     */
    private SendMessage getCategories(Long chatId) {
        SendMessage message = new SendMessage(chatId, "分类列表");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        categoryService.lambdaQuery().list().forEach(category -> {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(category.getCategoryName());
            button.setCallbackData("category::" + category.getCategoryId());
            inlineKeyboardMarkup.addRow(button);
        });
        message.replyMarkup(inlineKeyboardMarkup);
        return message;
    }


    /**
     * 添加分类
     */
    public SendMessage addCategory(String[] params, Long chatId) {
        String categoryName = params[0];

        Category category = new Category().setCategoryName(categoryName);
        if (params.length > 1) {
            String parentName = params[1];
            Category parentCategory = categoryService.lambdaQuery().eq(Category::getCategoryName, parentName).one();
            if (parentCategory == null) {
                parentCategory = new Category();
                parentCategory.setCategoryName(parentName);
                parentCategory.insert();

                category.setParentCategoryId(parentCategory.getCategoryId());
            }
        }

        category.insert();
        String replyText = "分类添加成功";
        return new SendMessage(chatId, replyText);
    }

    /**
     * 添加用于自动分类的关键词，格式：分类名-关键词。
     */
    public SendMessage addCategoryKeyword(String param, Long chatId) {
        if (param == null || !param.contains("-")) {
            return new SendMessage(chatId, "请输入：/category_keyword_add/分类名-关键词\n例如：/category_keyword_add/交通-滴滴");
        }

        String[] params = param.split("-", 2);
        String categoryName = params[0].trim();
        String keyword = params[1].trim();
        if (categoryName.isEmpty() || keyword.isEmpty()) {
            return new SendMessage(chatId, "分类名称和关键词不能为空\n格式：/category_keyword_add/分类名-关键词");
        }
        if (categoryName.length() > 255 || keyword.length() > 255) {
            return new SendMessage(chatId, "分类名称和关键词不能超过255个字符");
        }

        CategoryKeywordService.KeywordAddResult result = categoryKeywordService.addKeyword(categoryName, keyword);
        return switch (result.status()) {
            case CREATED -> {
                categoryService.getOrCreate(categoryName);
                yield new SendMessage(chatId, "✅ 分类关键词添加成功\n分类：" + categoryName + "\n关键词：" + keyword);
            }
            case EXISTS -> new SendMessage(chatId, "ℹ️ 该分类关键词已存在\n分类：" + categoryName + "\n关键词：" + keyword);
            case CONFLICT -> new SendMessage(chatId, "❌ 关键词已属于其他分类："
                    + result.mapping().getCategoryName());
        };
    }
}
