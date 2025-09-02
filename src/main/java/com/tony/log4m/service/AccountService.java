package com.tony.log4m.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tony.log4m.exception.Log4mException;
import com.tony.log4m.mapper.AccountMapper;
import com.tony.log4m.pojo.entity.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

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
            throw new Log4mException("账户名重复");
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

    public BigDecimal getBudget() {
        return this.getOrCreateDefaultAccount().getBudget();
    }

    // ========== 标准CRUD操作 ==========

    /**
     * 创建账户
     *
     * @param account 账户信息
     * @return 创建的账户
     */
    public Account create(Account account) {
        // 检查账户名是否重复
        if (this.lambdaQuery().eq(Account::getAccountName, account.getAccountName()).exists()) {
            throw new Log4mException("账户名重复");
        }
        account.insert();
        return account;
    }

    /**
     * 根据ID更新账户（带数据验证）
     *
     * @param account 账户信息
     * @return 更新结果
     */
    public Boolean updateByIdWithValidation(Account account) {
        // 检查账户名是否重复
        if (this.lambdaQuery().eq(Account::getAccountName, account.getAccountName())
                .ne(Account::getAccountId, account.getAccountId()).exists()) {
            throw new Log4mException("账户名重复");
        }
        return account.updateById();
    }

    /**
     * 根据ID删除账户（逻辑删除）
     *
     * @param id 账户ID
     * @return 删除结果
     */
    public Boolean deleteById(Serializable id) {
        Account account = this.getOptById(id).orElseThrow(() -> new Log4mException("账户不存在"));
        if (Boolean.TRUE.equals(account.getIsDefault())) {
            throw new Log4mException("默认账户不能删除");
        }
        return this.removeById(id);
    }

    /**
     * 根据ID查询账户
     *
     * @param id 账户ID
     * @return 账户信息
     */
    public Account getById(Serializable id) {
        return this.getOptById(id).orElseThrow(() -> new Log4mException("账户不存在"));
    }

    /**
     * 查询所有账户
     *
     * @return 账户列表
     */
    public List<Account> listAll() {
        return this.lambdaQuery().orderByAsc(Account::getSort).orderByDesc(Account::getCrTime).list();
    }

    /**
     * 分页查询账户
     *
     * @param current 当前页
     * @param size    页大小
     * @return 分页结果
     */
    public Page<Account> pageQuery(int current, int size) {
        Page<Account> page = new Page<>(current, size);
        return this.lambdaQuery().orderByAsc(Account::getSort).orderByDesc(Account::getCrTime).page(page);
    }

    /**
     * 根据用户ID查询账户列表
     *
     * @param userId 用户ID
     * @return 账户列表
     */
    public List<Account> getByUserId(Long userId) {
        // 注意：Account实体中没有createBy字段，这里改为按排序和创建时间查询
        return this.lambdaQuery().orderByAsc(Account::getSort).orderByDesc(Account::getCrTime).list();
    }
}
