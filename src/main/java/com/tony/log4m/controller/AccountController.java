package com.tony.log4m.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.tony.log4m.base.R;
import com.tony.log4m.pojo.dto.AccountDTO;
import com.tony.log4m.pojo.entity.Account;
import com.tony.log4m.service.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.Serializable;


/**
 * @author Tony
 * @since 2022-09-23 15:46:48
 */
@Api(tags = {"账户"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/page")
    @ApiOperation("账户列表")
    public Page<AccountDTO> page(@Valid @RequestBody PageDTO pageDTO) {
        return accountService.page(pageDTO);
    }

    @GetMapping("/{id}")
    @ApiOperation("账户详情")
    public AccountDTO get(@PathVariable Serializable id) {
        return accountService.get(id);
    }

    @PostMapping
    @ApiOperation("新增账户")
    public AccountDTO insert(@Valid @RequestBody Account account) {
        return accountService.insert(account);
    }

    @PutMapping
    @ApiOperation("修改账户")
    public AccountDTO update(@Valid @RequestBody Account account) {
        return accountService.update(account);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除账户")
    public R delete(@PathVariable Serializable id) {
        accountService.delete(id);
        return R.ok();
    }
}
