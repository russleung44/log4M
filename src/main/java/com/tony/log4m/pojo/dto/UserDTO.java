package com.tony.log4m.pojo.dto;


import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(name = "")
    private Integer id;

    private String username;

    private String password;

    private String email;

    @Schema(name = "0: inactive, 1: active")
    private Integer status;

}
