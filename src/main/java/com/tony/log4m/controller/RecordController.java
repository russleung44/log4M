package com.tony.log4m.controller;

import com.tony.log4m.base.R;
import com.tony.log4m.pojo.dto.RecordUpdateDTO;
import com.tony.log4m.pojo.entity.Bill;
import com.tony.log4m.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.Serializable;


/**
 * @author Tony
 * @since 2022-09-26 12:06:50
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/record")
public class RecordController {

    private final BillService billService;


    @GetMapping("/{id}")
    public Bill get(@PathVariable Serializable id) {
        return billService.getById(id);
    }

    @PostMapping
    public void save(@Valid @RequestBody Bill bill) {
         billService.save(bill);
    }


    @PutMapping
    public void update(@Valid @RequestBody RecordUpdateDTO recordDTO) {
         billService.update(recordDTO);
    }

    @DeleteMapping("/{id}")
    public R delete(@PathVariable Serializable id) {
        billService.removeById(id);
        return R.ok();
    }
}
