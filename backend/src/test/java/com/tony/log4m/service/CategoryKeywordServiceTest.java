package com.tony.log4m.service;

import com.tony.log4m.models.entity.CategoryKeyword;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoryKeywordServiceTest {

    @Test
    void testMatchCategory_WhenKeywordMatches() {
        List<CategoryKeyword> mappings = List.of(
                keyword("饮", "瑞幸", false),
                keyword("水果", "苹果", false)
        );

        assertEquals("水果", CategoryKeywordService.matchCategory("午餐后买苹果", mappings).orElseThrow());
    }

    @Test
    void testMatchCategory_WhenKeywordIgnoresCase() {
        List<CategoryKeyword> mappings = List.of(keyword("饮", "luckin", true));

        assertEquals("饮", CategoryKeywordService.matchCategory("LUCKIN 18", mappings).orElseThrow());
    }

    @Test
    void testMatchCategory_WhenCaseSensitiveKeywordDoesNotMatch() {
        List<CategoryKeyword> mappings = List.of(keyword("饮", "CoCo", false));

        assertTrue(CategoryKeywordService.matchCategory("COCO 12", mappings).isEmpty());
    }

    @Test
    void testMatchCategory_WhenInputIsBlank() {
        assertTrue(CategoryKeywordService.matchCategory(" ", List.of()).isEmpty());
    }

    private CategoryKeyword keyword(String categoryName, String value, boolean ignoreCase) {
        return new CategoryKeyword()
                .setCategoryName(categoryName)
                .setKeyword(value)
                .setIgnoreCase(ignoreCase);
    }
}
