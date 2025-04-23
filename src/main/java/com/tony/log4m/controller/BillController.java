package com.tony.log4m.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tony.log4m.pojo.entity.Bill;
import com.tony.log4m.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/bill")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    @GetMapping("/list")
    public List<Bill> list() {
        return billService.lambdaQuery().orderByDesc(Bill::getBillDate).last("limit 10").list();
    }

    @GetMapping("/page/{current}/{size}")
    public Page<Bill> page(@PathVariable int current, @PathVariable int size) {
        Page<Bill> page = new Page<>(current, size);
        return billService.lambdaQuery().orderByDesc(Bill::getBillDate).orderByDesc(Bill::getBillId).page(page);
    }

}
