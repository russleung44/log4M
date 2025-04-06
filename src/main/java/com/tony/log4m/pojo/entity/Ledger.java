package com.tony.log4m.pojo.entity;

import com.tony.log4m.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author Tony
 * @since 4/6/2025
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class Ledger extends BaseEntity<Ledger> {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 账本名称
     */
    private String name;

}
