package com.tony.log4m.service;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tony.log4m.mapper.AccountMapper;
import com.tony.log4m.pojo.entity.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

/**
 * @author Tony
 * @since 2022-09-23 15:31:38
 */
@Service
@RequiredArgsConstructor
public class AccountService extends ServiceImpl<AccountMapper, Account> {


    public void update(Account account) {
        // 检查账户名是否重复
        if (this.lambdaQuery().eq(Account::getAccountName, account.getAccountName()).ne(Account::getAccountId, account.getAccountId()).exists()) {
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
        this.lambdaUpdate().ne(Account::getAccountId, id).eq(Account::getIsDefault, true).set(Account::getIsDefault, false).update();
    }


    /**
     * 获取默认账户
     *
     * @return 默认账户
     */
    public Account getOrCreateDefaultAccount() {
        Account defaultAccount = this.lambdaQuery().eq(Account::getIsDefault, true).one();
        if (defaultAccount == null) {
            defaultAccount = new Account()
                    .setIsDefault(true)
                    .setAccountName("默认账户");
            defaultAccount.insert();
        }

        return defaultAccount;
    }
}
