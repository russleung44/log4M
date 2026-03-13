package com.tony.log4m.configs;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分类关键词映射配置
 *
 * @author Tony
 * @since 3/13/2026
 */
@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "category-keywords")
public class CategoryKeywordProperties {

    /**
     * 分类配置 (英文key -> 配置)
     */
    private Map<String, CategoryConfig> categories = new HashMap<>();

    @Data
    public static class CategoryConfig {
        /**
         * 分类名称（中文）
         */
        private String name;
        /**
         * 关键词列表
         */
        private List<String> keywords;
    }

    @PostConstruct
    public void init() {
        log.info("=== CategoryKeywordProperties loaded, categories: {} ===", categories);
    }

    /**
     * 根据文本匹配分类名称
     *
     * @param text 输入文本
     * @return 匹配的分类名称，未匹配返回null
     */
    public String matchCategory(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        for (CategoryConfig config : categories.values()) {
            List<String> keywords = config.getKeywords();
            if (keywords != null) {
                for (String keyword : keywords) {
                    if (text.contains(keyword)) {
                        return config.getName();
                    }
                }
            }
        }
        return null;
    }

    /**
     * 根据文本匹配分类名称（忽略大小写）
     *
     * @param text           输入文本
     * @param ignoreCaseList 需要忽略大小写的关键词列表
     * @return 匹配的分类名称，未匹配返回null
     */
    public String matchCategoryIgnoreCase(String text, List<String> ignoreCaseList) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        String lowerText = text.toLowerCase();
        for (CategoryConfig config : categories.values()) {
            List<String> keywords = config.getKeywords();
            if (keywords != null) {
                for (String keyword : keywords) {
                    if (ignoreCaseList != null && ignoreCaseList.contains(keyword)) {
                        if (lowerText.contains(keyword.toLowerCase())) {
                            return config.getName();
                        }
                    } else {
                        if (text.contains(keyword)) {
                            return config.getName();
                        }
                    }
                }
            }
        }
        return null;
    }
}