package com.solo.toyboard.controller;

import com.solo.toyboard.dto.JoinDTO;
import com.solo.toyboard.service.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AccountController {

    private AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/join")
    public String joinP() {
        return "join";
    }

    @GetMapping("login")
    public String loginP() {
        return "login";
    }

    @PostMapping("/joinProc")
    public String joinProcess(JoinDTO joinDTO) {

        System.out.println(joinDTO.getUsername());
        if(accountService.joinProcess(joinDTO)) return "redirect:/login";
        return "redirect:/join";

    }
}
