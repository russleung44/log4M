package com.tony.log4m.service;

import com.github.pagehelper.PageInfo;
import com.tony.log4m.base.CrudService;
import com.tony.log4m.base.PageDTO;
import com.tony.log4m.pojo.dto.AccountDTO;
import com.tony.log4m.pojo.entity.Account;
import com.tony.log4m.pojo.vo.AccountStatVO;

import java.io.Serializable;

/**
 * @author Tony
 * @since 2022-09-23 15:25:10
 */
public interface AccountService extends CrudService<Account, AccountDTO> {

    AccountStatVO getStat(Integer userId);

    void setDefault(Serializable id);

    PageInfo<Account> getAccountPage(PageDTO<Account> pageDTO);
}
