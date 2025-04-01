package com.tony.log4m.service;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tony.log4m.mapper.AccountMapper;
import com.tony.log4m.pojo.entity.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        if (this.query().eq("account_name", account.getAccountName()).eq("user_id", account.getUserId()).exists()) {
            throw new RuntimeException("账户名重复");
        }
    }

    public void update(Account account) {
        // 检查账户名是否重复
        if (this.query().eq("account_name", account.getAccountName()).eq("user_id", account.getUserId()).ne("id", account.getId()).exists()) {
            throw new RuntimeException("账户名重复");
        }
        Optional.ofNullable(this.getById(account.getId())).orElseThrow();
        account.updateById();
    }


    public void setDefault(Serializable id) {
        Optional.ofNullable(this.getById(id)).orElseThrow().setDefault(true).updateById();
    }

}
