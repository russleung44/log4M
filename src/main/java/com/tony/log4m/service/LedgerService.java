package com.tony.log4m.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tony.log4m.mapper.LedgerMapper;
import com.tony.log4m.pojo.entity.Ledger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Tony
 * @since 4/6/2025
 */
@Service
@RequiredArgsConstructor
public class LedgerService extends ServiceImpl<LedgerMapper, Ledger> {
}
