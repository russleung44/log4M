package com.tony.log4m.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author TonyLeung
 * @since 2023/6/12
 */
@Getter
@AllArgsConstructor
public enum CategoryType {

    EXPENSE("支出分类", "book"),

    INCOME("收入分类", "book"),

    BUSINESS("商家管理"),

    PROJECT("项目管理"),

    MEMBER("成员管理"),

    BILL("账单模板");

    private final String desc;
    private final String tag;

    CategoryType(String desc) {
        this.desc = desc;
        this.tag = "";
    }

    @Override
    public String toString() {
        return this.name() + ":" + this.desc;
    }
}
