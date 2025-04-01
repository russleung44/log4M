package com.tony.log4m.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


/**
 * @author TonyLeung
 * @since 2022/9/23
 */
@Data
public class LoginDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
