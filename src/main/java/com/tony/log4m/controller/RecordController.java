package com.tony.log4m.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.tony.log4m.base.R;
import com.tony.log4m.pojo.dto.RecordDTO;
import com.tony.log4m.pojo.entity.Record;
import com.tony.log4m.service.RecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.Serializable;


/**
 * @author Tony
 * @since 2022-09-23 15:46:48
 */
@Api(tags = {"记录"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/record")
public class RecordController {

    private final RecordService recordService;

    @PostMapping("/page")
    @ApiOperation("记录列表")
    public Page<RecordDTO> page(@Valid @RequestBody PageDTO pageDTO) {
        return recordService.page(pageDTO);
    }

    @GetMapping("/{id}")
    @ApiOperation("记录详情")
    public RecordDTO get(@PathVariable Serializable id) {
        return recordService.get(id);
    }

    @PostMapping
    @ApiOperation("新增记录")
    public RecordDTO insert(@Valid @RequestBody Record record) {
        return recordService.insert(record);
    }

    @PutMapping
    @ApiOperation("修改记录")
    public RecordDTO update(@Valid @RequestBody Record record) {
        return recordService.update(record);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除记录")
    public R delete(@PathVariable Serializable id) {
        recordService.delete(id);
        return R.ok();
    }
}
