package com.tony.log4m.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.tony.log4m.base.R;
import com.tony.log4m.pojo.dto.UserDTO;
import com.tony.log4m.pojo.entity.User;
import com.tony.log4m.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.Serializable;


/**
 * @author Tony
 * @since 2022-09-23 15:46:49
 */
@Api(tags = {"用户"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/page")
    @ApiOperation("用户列表")
    public Page<UserDTO> page(@Valid @RequestBody PageDTO pageDTO) {
        return userService.page(pageDTO);
    }

    @GetMapping("/{id}")
    @ApiOperation("用户详情")
    public UserDTO get(@PathVariable Serializable id) {
        return userService.get(id);
    }


    @PutMapping
    @ApiOperation("修改用户")
    public UserDTO update(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除用户")
    public R delete(@PathVariable Serializable id) {
        userService.delete(id);
        return R.ok();
    }
}
