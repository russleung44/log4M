package com.tony.log4m.controller;

import com.tony.log4m.pojo.entity.Account;
import com.tony.log4m.service.AccountService;
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
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;


    @GetMapping("/{id}")
    public Account get(@PathVariable Serializable id) {
        return accountService.getById(id);
    }

    @PostMapping
    public void insert(@Valid @RequestBody Account account) {
        accountService.insert(account);
    }

    @PutMapping("/id")
    public void update(@Valid @RequestBody Account account) {
        accountService.update(account);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Serializable id) {
        accountService.removeById(id);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{id}/default")
    public ResponseEntity<?> setDefault(@PathVariable Serializable id) {
        accountService.setDefault(id);
        return ResponseEntity.ok().build();
    }
}
