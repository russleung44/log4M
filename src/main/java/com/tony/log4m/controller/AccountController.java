package com.tony.log4m.controller;

import com.github.pagehelper.PageInfo;
import com.tony.log4m.base.PageDTO;
import com.tony.log4m.base.R;
import com.tony.log4m.pojo.dto.AccountDTO;
import com.tony.log4m.pojo.entity.Account;
import com.tony.log4m.pojo.vo.AccountStatVO;
import com.tony.log4m.service.AccountService;
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
@Tag(name = "账户")
@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/page")
    @Operation(summary = "账户列表")
    public PageInfo<Account> page(@RequestBody PageDTO<Account> pageDTO) {
        return accountService.getAccountPage(pageDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "账户详情")
    public AccountDTO get(@PathVariable Serializable id) {
        return accountService.get(id);
    }

    @PostMapping
    @Operation(summary = "新增账户")
    public AccountDTO insert(@Valid @RequestBody AccountDTO accountDTO) {
        return accountService.insert(accountDTO);
    }

    @PutMapping
    @Operation(summary = "修改账户")
    public AccountDTO update(@Valid @RequestBody AccountDTO accountDTO) {
        return accountService.update(accountDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除账户")
    public R delete(@PathVariable Serializable id) {
        accountService.delete(id);
        return R.ok();
    }

    @GetMapping("/stat/{userId}")
    @Operation(summary = "统计账户")
    public AccountStatVO stat(@PathVariable Integer userId) {
        return accountService.getStat(userId);
    }

    @PutMapping("/default/{id}")
    @Operation(summary = "设为默认账户")
    public R setDefault(@PathVariable Serializable id) {
        accountService.setDefault(id);
        return R.ok();
    }
}
