package com.tony.log4m.pojo.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author TonyLeung
 * @since 2022/10/12
 */
@Data
public class RecordUpdateDTO {
    private Integer recordId;
    private Integer categoryId;
    private Integer accountId;
    private Integer tagId;
    private String date;
    private String title;
    private BigDecimal amount;
}
