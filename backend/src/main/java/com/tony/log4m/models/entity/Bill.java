package com.tony.log4m.models.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tony.log4m.base.BaseEntity;
import com.tony.log4m.enums.TransactionType;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 记录
 *
 * @author Tony
 * @since 2022-09-23 16:00:42
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName(value = "bill", autoResultMap = true)
@EqualsAndHashCode(callSuper = true)
public class Bill extends BaseEntity<Bill> {

    @TableId
    private Long billId;

    private Long accountId;

    @JSONField(format = "yyyy-MM-dd")
    private LocalDate billDate;

    private String billMonth;

    private TransactionType transactionType;

    private BigDecimal amount;

    private Long categoryId;

    private String categoryName;

    private Long parentCategoryId;

    private String parentCategoryName;

    private Long tagId;

    private String tagName;

    private Long ledgerId;

    private String ledgerName;

    /**
     * 备注
     */
    private String note;

    /**
     * 备注按钮单独字段
     */
    private String remark;

    /**
     * 货币
     */
    private String currency;
}