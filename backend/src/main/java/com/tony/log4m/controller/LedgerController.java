package com.tony.log4m.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tony.log4m.models.entity.Ledger;
import com.tony.log4m.models.vo.ResultVO;
import com.tony.log4m.service.LedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 账本控制器
 *
 * @author Tony
 * @since 2025-09-02
 */
@RestController
@RequestMapping("/ledger")
@RequiredArgsConstructor
public class LedgerController {

    private final LedgerService ledgerService;

    /**
     * 创建账本
     */
    @PostMapping
    public ResultVO<Ledger> create(@RequestBody @Valid Ledger ledger) {
        Ledger result = ledgerService.create(ledger);
        return ResultVO.ok(result);
    }

    /**
     * 根据ID获取账本
     */
    @GetMapping("/{id}")
    public ResultVO<Ledger> getById(@PathVariable Long id) {
        Ledger result = ledgerService.getById(id);
        return ResultVO.ok(result);
    }

    /**
     * 根据ID更新账本
     */
    @PutMapping("/{id}")
    public ResultVO<Boolean> updateById(@PathVariable Long id, @RequestBody @Valid Ledger ledger) {
        ledger.setLedgerId(id);
        Boolean result = ledgerService.updateByIdWithValidation(ledger);
        return ResultVO.ok(result);
    }

    /**
     * 根据ID删除账本
     */
    @DeleteMapping("/{id}")
    public ResultVO<Boolean> deleteById(@PathVariable Long id) {
        Boolean result = ledgerService.deleteById(id);
        return ResultVO.ok(result);
    }

    /**
     * 获取所有账本
     */
    @GetMapping("/list")
    public ResultVO<List<Ledger>> list() {
        List<Ledger> result = ledgerService.listAll();
        return ResultVO.ok(result);
    }

    /**
     * 分页获取账本
     */
    @GetMapping("/page/{current}/{size}")
    public ResultVO<Page<Ledger>> page(@PathVariable int current, @PathVariable int size) {
        Page<Ledger> result = ledgerService.pageQuery(current, size);
        return ResultVO.ok(result);
    }

    /**
     * 根据用户ID获取账本列表
     */
    @GetMapping("/user/{userId}")
    public ResultVO<List<Ledger>> getByUserId(@PathVariable Long userId) {
        List<Ledger> result = ledgerService.getByUserId(userId);
        return ResultVO.ok(result);
    }

    /**
     * 获取默认账本
     */
    @GetMapping("/default")
    public ResultVO<Ledger> getDefaultLedger() {
        Ledger result = ledgerService.getOrCreateDefaultLedger();
        return ResultVO.ok(result);
    }
}