package com.tony.log4m.controller;

import com.tony.log4m.pojo.entity.Bill;
import com.tony.log4m.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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
        return billService.lambdaQuery().last("limit 10").list();
    }

}
