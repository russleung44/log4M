package com.tony.log4m.bots.commands;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.tony.log4m.bots.enums.Command;
import com.tony.log4m.models.entity.Category;
import com.tony.log4m.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author Tony
 * @since 4/12/2025
 */
@Component
@RequiredArgsConstructor
public class CategoryCommand implements CommandStrategy {

    private final CategoryService categoryService;

    @Override
    public SendMessage execute(Command command, String param, Long chatId) {
        switch (command) {
            case CATEGORIES -> {
                return getCategories(chatId);
            }

            case CATEGORY_ADD -> {
                return addCategory(param.split("-"), chatId);
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
}
