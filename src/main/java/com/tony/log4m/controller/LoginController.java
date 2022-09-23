package com.tony.log4m.controller;

import com.tony.log4m.pojo.dto.LoginDTO;
import com.tony.log4m.pojo.entity.User;
import com.tony.log4m.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author TonyLeung
 * @date 2022/9/23
 */
@Api(tags = {"登录"})
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;


    @ApiOperation("注册")
    @PostMapping("/register")
    public void register(@Valid @RequestBody User user) {
        userService.register(user);
    }

    @ApiOperation("登录")
    @PostMapping("/login")
    public void login(LoginDTO loginDTO) {
        userService.login(loginDTO);
    }

}
