package com.tony.log4m.controller;

import com.github.pagehelper.PageInfo;
import com.tony.log4m.pojo.dto.UserDTO;
import com.tony.log4m.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.Serializable;


/**
 * @author Tony
 * @since 2022-09-26 12:06:50
 */
@Tag(name = "用户")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/page/{pageNum}/{pageSize}")
    @Operation(summary = "用户列表")
    public PageInfo<UserDTO> page(@PathVariable Integer pageNum, @PathVariable Integer pageSize) {
        return userService.page(pageNum, pageSize);
    }

    @GetMapping("/{id}")
    @Operation(summary = "用户详情")
    public UserDTO get(@PathVariable Serializable id) {
        return userService.get(id);
    }

    @PostMapping
    @Operation(summary = "新增用户")
    public UserDTO insert(@Valid @RequestBody UserDTO userDTO) {
        return userService.insert(userDTO);
    }

    @PutMapping
    @Operation(summary = "修改用户")
    public UserDTO update(@Valid @RequestBody UserDTO userDTO) {
        return userService.update(userDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    public void delete(@PathVariable Serializable id) {
        userService.delete(id);
    }
}
