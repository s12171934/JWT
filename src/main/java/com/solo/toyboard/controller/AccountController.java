package com.solo.toyboard.controller;

import com.solo.toyboard.dto.JoinDTO;
import com.solo.toyboard.service.AccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {

    private AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/join")
    public String joinP() {
        return "join";
    }

    @PostMapping("/join")
    public String joinProcess(JoinDTO joinDTO) {

        System.out.println(joinDTO.getUsername());
        if(accountService.joinProcess(joinDTO)) return "ok";
        return "fail";

    }
}
