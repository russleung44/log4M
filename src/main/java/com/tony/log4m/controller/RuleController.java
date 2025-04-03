package com.tony.log4m.controller;

import com.tony.log4m.pojo.entity.Rule;
import com.tony.log4m.service.RuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


    @GetMapping("/{ruleId}")
    public Rule get(@PathVariable Serializable ruleId) {
        return ruleService.getById(ruleId);
    }

    @PostMapping
    public void insert(@Valid @RequestBody Rule rule) {
        ruleService.save(rule);
    }

    @PutMapping("/{ruleId}")
    public void update(@PathVariable Serializable ruleId, @Valid @RequestBody Rule rule) {
        ruleService.update(ruleId, rule);
    }

    @DeleteMapping("/{ruleId}")
    public ResponseEntity<?> delete(@PathVariable Serializable ruleId) {
        ruleService.removeById(ruleId);
        return ResponseEntity.ok().build();
    }
}
