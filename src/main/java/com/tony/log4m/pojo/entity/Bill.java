package com.tony.log4m.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.tony.log4m.base.BaseEntity;
import com.tony.log4m.enums.TransactionType;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    private Long accountId;

    private String title;

    private LocalDateTime billDate;

    private LocalDate billDay;

    private String billMonth;

    private TransactionType transactionType;

    private BigDecimal amount;

    private Long categoryId;

    private String categoryName;

    private Long subCategoryId;

    private String subCategoryName;

    private Long tagId;

    private String tagName;

    private Long ledgerId;

    private String ledgerName;

    /**
     * 备注
     */
    private String note;

    /**
     * 货币
     */
    private String currency;
}
