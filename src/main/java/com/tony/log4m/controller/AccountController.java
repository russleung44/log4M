package com.tony.log4m.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tony.log4m.pojo.entity.Account;
import com.tony.log4m.pojo.vo.ResultVO;
import com.tony.log4m.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 账户管理Controller
 * 
 * @author Tony
 */
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AccountController {

    private final AccountService accountService;

    /**
     * 分页查询账户
     */
    @GetMapping
    public ResultVO<Page<Account>> pageQuery(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword
    ) {
        Page<Account> page = new Page<>(current, size);
        
        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<Account>()
                .like(keyword != null && !keyword.trim().isEmpty(), Account::getAccountName, keyword)
                .orderByDesc(Account::getAccountId);
        
        Page<Account> result = accountService.page(page, queryWrapper);
        return ResultVO.success(result);
    }

    /**
     * 获取所有账户
     */
    @GetMapping("/all")
    public ResultVO<List<Account>> getAllAccounts() {
        List<Account> accounts = accountService.list();
        return ResultVO.success(accounts);
    }

    /**
     * 根据ID获取账户
     */
    @GetMapping("/{id}")
    public ResultVO<Account> getById(@PathVariable Long id) {
        Account account = accountService.getById(id);
        if (account == null) {
            return ResultVO.error("账户不存在");
        }
        return ResultVO.success(account);
    }
}