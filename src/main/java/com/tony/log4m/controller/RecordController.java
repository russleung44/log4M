package com.tony.log4m.controller;

import com.github.pagehelper.PageInfo;
import com.tony.log4m.base.R;
import com.tony.log4m.pojo.dto.RecordDTO;
import com.tony.log4m.pojo.dto.RecordUpdateDTO;
import com.tony.log4m.pojo.entity.Record;
import com.tony.log4m.service.RecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.Serializable;


/**
 * @author Tony
 * @since 2022-09-26 12:06:50
 */
@Tag(name = "记录")
@RestController
@RequiredArgsConstructor
@RequestMapping("/record")
public class RecordController {

    private final RecordService recordService;

    @GetMapping("/page/{pageNum}/{pageSize}")
    @Operation(summary = "记录列表")
    public PageInfo<RecordDTO> page(@PathVariable Integer pageNum, @PathVariable Integer pageSize) {
        return recordService.page(pageNum, pageSize);
    }

    @PostMapping("/search/{pageNum}/{pageSize}")
    @Operation(summary = "记录列表")
    public PageInfo<Record> search(@Valid @RequestBody RecordDTO recordDTO, @PathVariable Integer pageNum, @PathVariable Integer pageSize) {
        return recordService.search(recordDTO, pageNum, pageSize);
    }


    @GetMapping("/{id}")
    @Operation(summary = "记录详情")
    public RecordDTO get(@PathVariable Serializable id) {
        return recordService.get(id);
    }

    @PostMapping
    @Operation(summary = "新增记录")
    public RecordDTO save(@Valid @RequestBody RecordDTO recordDTO) {
        return recordService.insert(recordDTO);
    }


    @PutMapping
    @Operation(summary = "修改记录")
    public RecordDTO update(@Valid @RequestBody RecordUpdateDTO recordDTO) {
        return recordService.update(recordDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除记录")
    public R delete(@PathVariable Serializable id) {
        recordService.delete(id);
        return R.ok();
    }
}
