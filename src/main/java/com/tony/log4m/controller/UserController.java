package com.tony.log4m.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tony.log4m.pojo.entity.User;
import com.tony.log4m.pojo.vo.ResultVO;
import com.tony.log4m.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 用户控制器
 *
 * @author Tony
 * @since 2025-09-02
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 创建用户
     */
    @PostMapping
    public ResultVO<User> create(@RequestBody @Valid User user) {
        User result = userService.create(user);
        return ResultVO.ok(result);
    }

    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    public ResultVO<User> getById(@PathVariable Long id) {
        User result = userService.getById(id);
        return ResultVO.ok(result);
    }

    /**
     * 根据ID更新用户
     */
    @PutMapping("/{id}")
    public ResultVO<Boolean> updateById(@PathVariable Long id, @RequestBody @Valid User user) {
        user.setUserId(id);
        Boolean result = userService.updateByIdWithValidation(user);
        return ResultVO.ok(result);
    }

    /**
     * 根据ID删除用户
     */
    @DeleteMapping("/{id}")
    public ResultVO<Boolean> deleteById(@PathVariable Long id) {
        Boolean result = userService.deleteById(id);
        return ResultVO.ok(result);
    }

    /**
     * 获取所有用户
     */
    @GetMapping("/list")
    public ResultVO<List<User>> list() {
        List<User> result = userService.listAll();
        return ResultVO.ok(result);
    }

    /**
     * 分页获取用户
     */
    @GetMapping("/page/{current}/{size}")
    public ResultVO<Page<User>> page(@PathVariable int current, @PathVariable int size) {
        Page<User> result = userService.pageQuery(current, size);
        return ResultVO.ok(result);
    }

    /**
     * 根据Telegram用户ID查询用户
     */
    @GetMapping("/telegram/{tgUserId}")
    public ResultVO<User> getByTgUserId(@PathVariable Long tgUserId) {
        User result = userService.getByTgUserId(tgUserId);
        return ResultVO.ok(result);
    }

    /**
     * 保存或更新Telegram用户信息
     */
    @PostMapping("/telegram")
    public ResultVO<Boolean> saveTgUser(@RequestParam Long tgUserId, @RequestParam String username) {
        userService.saveTgUser(tgUserId, username);
        return ResultVO.ok(true);
    }
}