package com.tony.log4m.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tony.log4m.base.CrudServiceImpl;
import com.tony.log4m.base.PageDTO;
import com.tony.log4m.convert.AccountConvert;
import com.tony.log4m.dao.AccountDao;
import com.tony.log4m.pojo.dto.AccountDTO;
import com.tony.log4m.pojo.entity.Account;
import com.tony.log4m.pojo.vo.AccountStatVO;
import com.tony.log4m.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
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

    @Override
    public AccountStatVO getStat(Integer userId) {
        List<Account> accountList = this.query().eq("user_id", userId).list();

        int totalAccount = accountList.size();
        long activeAccount = accountList.stream().filter(v -> v.getStatus() == 1).count();
        long frozenAccount = accountList.stream().filter(v -> v.getStatus() == 0).count();
        BigDecimal totalBalance = accountList.stream().map(Account::getBalance).reduce(BigDecimal.ZERO, BigDecimal::add);

        return AccountStatVO.builder()
                .totalBalance(totalBalance)
                .totalAccount(totalAccount)
                .activeAccount((int) activeAccount)
                .frozenAccount((int) frozenAccount)
                .build();
    }

    @Override
    public void setDefault(Serializable id) {
        Account account = Optional.ofNullable(this.getById(id)).orElseThrow();
        account.setDefault(true).updateById();
    }

    @Override
    public PageInfo<Account> getAccountPage(PageDTO<Account> pageDTO) {
        Account param = pageDTO.getParam();
        return PageHelper.startPage(pageDTO).doSelectPageInfo(() -> {
            if (param != null) {
                Integer userId = param.getUserId();
                Integer status = param.getStatus();
                String accountName = param.getAccountName();
                this.query()
                        .eq(userId != null, "user_id", userId)
                        .eq(status != null, "status", status)
                        .like(accountName != null, "account_name", accountName)
                        .list();
            } else {
                this.list();
            }
        });
    }
}
