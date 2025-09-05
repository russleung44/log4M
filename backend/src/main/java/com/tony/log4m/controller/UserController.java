package com.tony.log4m.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tony.log4m.models.entity.User;
import com.tony.log4m.models.vo.ResultVO;
import com.tony.log4m.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理Controller
 * 
 * @author Tony
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    /**
     * 分页查询用户
     */
    @GetMapping
    public ResultVO<Page<User>> pageQuery(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword
    ) {
        Page<User> page = new Page<>(current, size);
        
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
                .like(keyword != null && !keyword.trim().isEmpty(), User::getUsername, keyword)
                .or()
                .like(keyword != null && !keyword.trim().isEmpty(), User::getEmail, keyword)
                .orderByDesc(User::getUserId);
        
        Page<User> result = userService.page(page, queryWrapper);
        return ResultVO.success(result);
    }

    /**
     * 获取所有用户
     */
    @GetMapping("/all")
    public ResultVO<List<User>> getAllUsers() {
        List<User> users = userService.list();
        return ResultVO.success(users);
    }

    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    public ResultVO<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return ResultVO.error("用户不存在");
        }
        return ResultVO.success(user);
    }
}