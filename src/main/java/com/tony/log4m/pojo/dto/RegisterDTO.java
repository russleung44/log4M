package com.tony.log4m.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;


/**
 * @author TonyLeung
 * @since 2022/9/23
 */
@Data
public class RegisterDTO {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "邮箱不能为空")
    private String email;
}
