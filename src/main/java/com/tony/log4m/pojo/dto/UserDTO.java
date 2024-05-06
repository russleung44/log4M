package com.tony.log4m.pojo.dto;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author Tony
 * @since 2022-09-23 15:13:50
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class UserDTO {

    private Integer id;

    private String username;

    private String password;

    private String email;

    private Integer status;

}
