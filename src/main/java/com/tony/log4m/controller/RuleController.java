package com.tony.log4m.controller;

import com.tony.log4m.base.R;
import com.tony.log4m.pojo.entity.Rule;
import com.tony.log4m.service.RuleService;
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
@RequestMapping("/rule")
public class RuleController {

    private final RuleService ruleService;


    @GetMapping("/{id}")
    public Rule get(@PathVariable Serializable id) {
        return ruleService.getById(id);
    }

    @PostMapping
    public void insert(@Valid @RequestBody Rule rule) {
        ruleService.insert(rule);
    }

    @PutMapping
    public void update(@Valid @RequestBody Rule rule) {
        ruleService.update(rule);
    }

    @DeleteMapping("/{id}")
    public R delete(@PathVariable Serializable id) {
        ruleService.removeById(id);
        return R.ok();
    }
}
