package com.tony.log4m.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tony.log4m.pojo.entity.Account;
import com.tony.log4m.pojo.vo.ResultVO;
import com.tony.log4m.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

/**
 * 账户控制器
 *
 * @author Tony
 * @since 2025-09-02
 */
@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    /**
     * 创建账户
     */
    @PostMapping
    public ResultVO<Account> create(@RequestBody @Valid Account account) {
        Account result = accountService.create(account);
        return ResultVO.ok(result);
    }

    /**
     * 根据ID获取账户
     */
    @GetMapping("/{id}")
    public ResultVO<Account> getById(@PathVariable Long id) {
        Account result = accountService.getById(id);
        return ResultVO.ok(result);
    }

    /**
     * 根据ID更新账户
     */
    @PutMapping("/{id}")
    public ResultVO<Boolean> updateById(@PathVariable Long id, @RequestBody @Valid Account account) {
        account.setAccountId(id);
        Boolean result = accountService.updateByIdWithValidation(account);
        return ResultVO.ok(result);
    }

    /**
     * 根据ID删除账户
     */
    @DeleteMapping("/{id}")
    public ResultVO<Boolean> deleteById(@PathVariable Long id) {
        Boolean result = accountService.deleteById(id);
        return ResultVO.ok(result);
    }

    /**
     * 获取所有账户
     */
    @GetMapping("/list")
    public ResultVO<List<Account>> list() {
        List<Account> result = accountService.listAll();
        return ResultVO.ok(result);
    }

    /**
     * 分页获取账户
     */
    @GetMapping("/page/{current}/{size}")
    public ResultVO<Page<Account>> page(@PathVariable int current, @PathVariable int size) {
        Page<Account> result = accountService.pageQuery(current, size);
        return ResultVO.ok(result);
    }

    /**
     * 根据用户ID获取账户列表
     */
    @GetMapping("/user/{userId}")
    public ResultVO<List<Account>> getByUserId(@PathVariable Long userId) {
        List<Account> result = accountService.getByUserId(userId);
        return ResultVO.ok(result);
    }

    /**
     * 设置默认账户
     */
    @PutMapping("/{id}/default")
    public ResultVO<Boolean> setDefault(@PathVariable Long id) {
        accountService.setDefault(id);
        return ResultVO.ok(true);
    }

    /**
     * 获取默认账户
     */
    @GetMapping("/default")
    public ResultVO<Account> getDefaultAccount() {
        Account result = accountService.getOrCreateDefaultAccount();
        return ResultVO.ok(result);
    }

    /**
     * 获取账户预算
     */
    @GetMapping("/budget")
    public ResultVO<BigDecimal> getBudget() {
        BigDecimal result = accountService.getBudget();
        return ResultVO.ok(result);
    }
}