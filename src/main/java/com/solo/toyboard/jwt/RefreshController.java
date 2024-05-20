package com.solo.toyboard.jwt;

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

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        return refreshService.reissueToken(request, response);
    }

}
