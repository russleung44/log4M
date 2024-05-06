package com.tony.log4m.controller;

import com.tony.log4m.base.PageDTO;
import com.tony.log4m.base.R;
import com.tony.log4m.pojo.dto.AccountDTO;
import com.tony.log4m.pojo.entity.Account;
import com.tony.log4m.pojo.vo.AccountStatVO;
import com.tony.log4m.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.Serializable;


/**
 * @author Tony
 * @since 2022-09-26 12:06:50
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;


    @GetMapping("/{id}")
    public Account get(@PathVariable Serializable id) {
        return accountService.getById(id);
    }

    @PostMapping
    public void insert(@Valid @RequestBody Account account) {
         accountService.insert(account);
    }

    @PutMapping
    public void update(@Valid @RequestBody Account account) {
        accountService.update(account);
    }

    @DeleteMapping("/{id}")
    public R delete(@PathVariable Serializable id) {
        accountService.removeById(id);
        return R.ok();
    }


    @PutMapping("/default/{id}")
    public R setDefault(@PathVariable Serializable id) {
        accountService.setDefault(id);
        return R.ok();
    }
}
