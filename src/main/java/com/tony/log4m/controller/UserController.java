package com.tony.log4m.controller;

import com.tony.log4m.pojo.entity.User;
import com.tony.log4m.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;


/**
 * @author Tony
 * @since 2022-09-26 12:06:50
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;


    @GetMapping("/{id}")
    public User get(@PathVariable Serializable id) {
        return userService.getById(id);
    }

    @PostMapping
    public void insert(@Valid @RequestBody User user) {
        userService.save(user);
    }

    @PutMapping
    public void update(@Valid @RequestBody User user) {
        userService.updateById(user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Serializable id) {
        userService.removeById(id);
    }
}
