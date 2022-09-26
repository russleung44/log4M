package com.tony.log4m.controller;

import com.github.pagehelper.PageInfo;
import com.tony.log4m.base.R;
import com.tony.log4m.pojo.dto.AccountDTO;
import com.tony.log4m.service.AccountService;
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
@Api(tags = {"账户"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/page/{pageNum}/{pageSize}")
    @ApiOperation("账户列表")
    public PageInfo<AccountDTO> page(@PathVariable Integer pageNum, @PathVariable Integer pageSize) {
        return accountService.page(pageNum, pageSize);
    }

    @GetMapping("/{id}")
    @ApiOperation("账户详情")
    public AccountDTO get(@PathVariable Serializable id) {
        return accountService.get(id);
    }

    @PostMapping
    @ApiOperation("新增账户")
    public AccountDTO insert(@Valid @RequestBody AccountDTO accountDTO) {
        return accountService.insert(accountDTO);
    }

    @PutMapping
    @ApiOperation("修改账户")
    public AccountDTO update(@Valid @RequestBody AccountDTO accountDTO) {
        return accountService.update(accountDTO);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除账户")
    public R delete(@PathVariable Serializable id) {
        accountService.delete(id);
        return R.ok();
    }
}
