package com.tony.log4m.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tony.log4m.exception.Log4mException;
import com.tony.log4m.mapper.CategoryKeywordMapper;
import com.tony.log4m.models.entity.CategoryKeyword;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * 从数据库读取关键词并匹配自动分类。
 */
@Service
public class CategoryKeywordService extends ServiceImpl<CategoryKeywordMapper, CategoryKeyword> {

    public KeywordAddResult addKeyword(String categoryName, String keyword) {
        String normalizedCategoryName = StrUtil.trim(categoryName);
        String normalizedKeyword = StrUtil.trim(keyword);
        if (StrUtil.isBlank(normalizedCategoryName) || StrUtil.isBlank(normalizedKeyword)) {
            throw new Log4mException("分类名称和关键词不能为空");
        }
        if (normalizedCategoryName.length() > 255 || normalizedKeyword.length() > 255) {
            throw new Log4mException("分类名称和关键词不能超过255个字符");
        }

        Optional<CategoryKeyword> existing = list().stream()
                .filter(mapping -> mapping.getKeyword().equalsIgnoreCase(normalizedKeyword))
                .findFirst();
        if (existing.isPresent()) {
            CategoryKeyword mapping = existing.get();
            KeywordAddStatus status = mapping.getCategoryName().equals(normalizedCategoryName)
                    ? KeywordAddStatus.EXISTS
                    : KeywordAddStatus.CONFLICT;
            return new KeywordAddResult(status, mapping);
        }

        CategoryKeyword mapping = new CategoryKeyword()
                .setCategoryName(normalizedCategoryName)
                .setKeyword(normalizedKeyword)
                .setIgnoreCase(true)
                .setSort(99);
        save(mapping);
        return new KeywordAddResult(KeywordAddStatus.CREATED, mapping);
    }

    public Optional<String> matchCategory(String text) {
        if (StrUtil.isBlank(text)) {
            return Optional.empty();
        }

        List<CategoryKeyword> mappings = lambdaQuery()
                .orderByAsc(CategoryKeyword::getSort)
                .orderByAsc(CategoryKeyword::getCategoryKeywordId)
                .list();
        return matchCategory(text, mappings);
    }

    static Optional<String> matchCategory(String text, List<CategoryKeyword> mappings) {
        if (StrUtil.isBlank(text)) {
            return Optional.empty();
        }

        String lowerText = text.toLowerCase(Locale.ROOT);
        return mappings.stream()
                .filter(mapping -> StrUtil.isNotBlank(mapping.getCategoryName()))
                .filter(mapping -> StrUtil.isNotBlank(mapping.getKeyword()))
                .filter(mapping -> matches(text, lowerText, mapping))
                .map(CategoryKeyword::getCategoryName)
                .findFirst();
    }

    private static boolean matches(String text, String lowerText, CategoryKeyword mapping) {
        if (Boolean.TRUE.equals(mapping.getIgnoreCase())) {
            return lowerText.contains(mapping.getKeyword().toLowerCase(Locale.ROOT));
        }
        return text.contains(mapping.getKeyword());
    }

    public enum KeywordAddStatus {
        CREATED,
        EXISTS,
        CONFLICT
    }

    public record KeywordAddResult(KeywordAddStatus status, CategoryKeyword mapping) {
    }
}
