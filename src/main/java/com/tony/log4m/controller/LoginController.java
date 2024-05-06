package com.tony.log4m.controller;

import com.tony.log4m.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author TonyLeung
 * @since 2022/9/23
 */
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;


}
