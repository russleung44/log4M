package com.tony.log4m.service.impl;


import com.tony.log4m.base.CrudServiceImpl;
import com.tony.log4m.convert.AccountConvert;
import com.tony.log4m.dao.AccountDao;
import com.tony.log4m.pojo.dto.AccountDTO;
import com.tony.log4m.pojo.entity.Account;
import com.tony.log4m.service.AccountService;
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
public class AccountServiceImpl extends CrudServiceImpl<AccountDao, Account, AccountDTO, AccountConvert> implements AccountService {

    @Override
    public AccountDTO insert(Account account) {
        return super.insert(account);
    }

    @Override
    public AccountDTO update(Account account) {
        Optional.ofNullable(this.getById(account.getId())).orElseThrow();
        return super.update(account);
    }

    @Override
    public AccountDTO get(Serializable id) {
        return super.get(id);
    }

    @Override
    public void delete(Serializable id) {
        super.delete(id);
    }

}
