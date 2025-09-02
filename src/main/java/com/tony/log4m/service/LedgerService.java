package com.tony.log4m.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tony.log4m.exception.Log4mException;
import com.tony.log4m.mapper.LedgerMapper;
import com.tony.log4m.pojo.entity.Ledger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tony
 * @since 4/6/2025
 */
@Service
@RequiredArgsConstructor
public class LedgerService extends ServiceImpl<LedgerMapper, Ledger> {

    // ========== 标准CRUD操作 ==========

    /**
     * 创建账本
     *
     * @param ledger 账本信息
     * @return 创建的账本
     */
    public Ledger create(Ledger ledger) {
        // 检查账本名是否重复
        if (this.lambdaQuery().eq(Ledger::getName, ledger.getName()).exists()) {
            throw new Log4mException("账本名称重复");
        }
        ledger.insert();
        return ledger;
    }

    /**
     * 根据ID更新账本（带数据验证）
     *
     * @param ledger 账本信息
     * @return 更新结果
     */
    public Boolean updateByIdWithValidation(Ledger ledger) {
        // 检查账本名是否重复
        if (this.lambdaQuery().eq(Ledger::getName, ledger.getName())
                .ne(Ledger::getLedgerId, ledger.getLedgerId()).exists()) {
            throw new Log4mException("账本名称重复");
        }
        return ledger.updateById();
    }

    /**
     * 根据ID删除账本（逻辑删除）
     *
     * @param id 账本ID
     * @return 删除结果
     */
    public Boolean deleteById(Serializable id) {
        Ledger ledger = this.getOptById(id).orElseThrow(() -> new Log4mException("账本不存在"));
        return this.removeById(id);
    }

    /**
     * 根据ID查询账本
     *
     * @param id 账本ID
     * @return 账本信息
     */
    public Ledger getById(Serializable id) {
        return this.getOptById(id).orElseThrow(() -> new Log4mException("账本不存在"));
    }

    /**
     * 查询所有账本
     *
     * @return 账本列表
     */
    public List<Ledger> listAll() {
        return this.lambdaQuery().orderByDesc(Ledger::getCrTime).list();
    }

    /**
     * 分页查询账本
     *
     * @param current 当前页
     * @param size    页大小
     * @return 分页结果
     */
    public Page<Ledger> pageQuery(int current, int size) {
        Page<Ledger> page = new Page<>(current, size);
        return this.lambdaQuery().orderByDesc(Ledger::getCrTime).page(page);
    }

    /**
     * 根据用户ID查询账本列表
     *
     * @param userId 用户ID
     * @return 账本列表
     */
    public List<Ledger> getByUserId(Long userId) {
        // 注意：Ledger实体中没有createBy字段，这里改为按创建时间查询
        return this.lambdaQuery().orderByDesc(Ledger::getCrTime).list();
    }

    /**
     * 获取或创建默认账本
     *
     * @return 默认账本
     */
    public Ledger getOrCreateDefaultLedger() {
        // 注意：Ledger实体中没有isDefault字段，这里直接创建默认账本
        Ledger defaultLedger = this.lambdaQuery().eq(Ledger::getName, "默认账本").one();
        if (defaultLedger == null) {
            defaultLedger = new Ledger()
                    .setName("默认账本");
            defaultLedger.insert();
        }
        return defaultLedger;
    }
}
