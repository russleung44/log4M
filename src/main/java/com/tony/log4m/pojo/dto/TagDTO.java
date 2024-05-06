package com.tony.log4m.pojo.dto;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author Tony
 * @since 2022-09-23 15:13:49
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class TagDTO {

    private Integer id;

    private Integer userId;

    private String tag;

    private Integer sort;

}
