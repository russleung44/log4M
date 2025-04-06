package com.tony.log4m.service;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tony.log4m.mapper.AccountMapper;
import com.tony.log4m.pojo.entity.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Optional;

/**
 * @author Tony
 * @since 2022-09-23 15:31:38
 */
@Service
@RequiredArgsConstructor
public class AccountService extends ServiceImpl<AccountMapper, Account> {

    public void insert(Account account) {
        // 检查账户名是否重复
        if (this.lambdaQuery().eq(Account::getName, account.getName()).eq(Account::getUserId, account.getUserId()).exists()) {
            throw new RuntimeException("账户名重复");
        }
    }

    public void update(Account account) {
        // 检查账户名是否重复
        if (this.lambdaQuery().eq(Account::getName, account.getName()).eq(Account::getUserId, account.getUserId()).ne(Account::getId, account.getId()).exists()) {
            throw new RuntimeException("账户名重复");
        }
        account.updateById();
    }


    /**
     * 设置默认账户
     *
     * @param id 账户ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void setDefault(Serializable id) {
        this.getOptById(id).orElseThrow().setIsDefault(true).updateById();
        this.lambdaUpdate().ne(Account::getId, id).eq(Account::getIsDefault, true).set(Account::getIsDefault, false).update();
    }


    /**
     * 获取默认账户
     *
     * @param userId 用户ID
     * @return 默认账户
     */
    public Account getDefaultAccount(Long userId) {
        Account defaultAccount = this.lambdaQuery().eq(Account::getIsDefault, true).one();
        if (defaultAccount == null) {
            defaultAccount = new Account()
                    .setIsDefault(true)
                    .setUserId(userId)
                    .setName("默认账户");
            defaultAccount.insert();
        }

        return defaultAccount;
    }
}
