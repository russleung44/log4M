package com.tony.log4m.controller;

import com.github.pagehelper.PageInfo;
import com.tony.log4m.base.R;
import com.tony.log4m.pojo.dto.UserDTO;
import com.tony.log4m.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.Serializable;


/**
 * @author Tony
 * @since 2022-09-26 12:06:50
 */
@Api(tags = {"用户"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/page/{pageNum}/{pageSize}")
    @ApiOperation("用户列表")
    public PageInfo<UserDTO> page(@PathVariable Integer pageNum, @PathVariable Integer pageSize) {
        return userService.page(pageNum, pageSize);
    }

    @GetMapping("/{id}")
    @ApiOperation("用户详情")
    public UserDTO get(@PathVariable Serializable id) {
        return userService.get(id);
    }

    @PostMapping
    @ApiOperation("新增用户")
    public UserDTO insert(@Valid @RequestBody UserDTO userDTO) {
        return userService.insert(userDTO);
    }

    @PutMapping
    @ApiOperation("修改用户")
    public UserDTO update(@Valid @RequestBody UserDTO userDTO) {
        return userService.update(userDTO);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除用户")
    public R delete(@PathVariable Serializable id) {
        userService.delete(id);
        return R.ok();
    }
}
