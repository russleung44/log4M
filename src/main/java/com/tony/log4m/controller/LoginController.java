package com.tony.log4m.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.tony.log4m.base.R;
import com.tony.log4m.pojo.dto.LoginDTO;
import com.tony.log4m.pojo.entity.User;
import com.tony.log4m.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author TonyLeung
 * @since 2022/9/23
 */
@Tag(name = "登录")
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;


    @Operation(summary = "注册")
    @PostMapping("/register")
    public void register(@Valid @RequestBody User user) {
        userService.register(user);
    }

    @Operation(summary = "登录")
    @PostMapping("/login")
    public R<SaTokenInfo> login(@Valid @RequestBody LoginDTO loginDTO) {
        return R.ok(userService.login(loginDTO));
    }

}
