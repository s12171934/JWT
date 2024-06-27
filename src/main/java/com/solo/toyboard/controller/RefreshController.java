package com.solo.toyboard.controller;

import com.solo.toyboard.service.RefreshService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RefreshController {

    private final RefreshService refreshService;

    public RefreshController(RefreshService refreshService){
        this.refreshService = refreshService;
    }

    //refresh token을 통해서 access token을 재발급
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        return refreshService.reissueToken(request, response);
    }

}
