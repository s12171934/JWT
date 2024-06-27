package com.solo.toyboard.service;

import com.solo.toyboard.entity.RefreshEntity;
import com.solo.toyboard.repository.RefreshRepository;
import com.solo.toyboard.util.CookieUtil;
import com.solo.toyboard.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class RefreshService {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public RefreshService(JWTUtil jwtUtil, RefreshRepository refreshRepository){
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    //Refresh 토큰 재발급
    public ResponseEntity<?> reissueToken(HttpServletRequest request, HttpServletResponse response) {

        //Request 쿠키값에 refresh token있는지 확인
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies) {
            if(cookie.getName().equals("refresh")) refresh = cookie.getValue();
        }

        if(refresh == null) return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);

        //refresh token 유효기간 확인
        try {
            jwtUtil.isExpired(refresh);
        }
        catch (ExpiredJwtException e) {
            return new ResponseEntity<>("refresh token expire", HttpStatus.BAD_REQUEST);
        }

        //refresh token 유효성 확인
        String category = jwtUtil.getCategory(refresh);
        if(!category.equals("refresh")) return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);

        //redis에 존재하는 refresh token인지 확인
        boolean isExist = refreshRepository.existsById(refresh);
        if(!isExist) return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        //새로운 Access token과 Refresh token 발급
        String newAccess = jwtUtil.createJwt("access", username, role, 10 * 60 * 1000L);
        String newRefresh = jwtUtil.createJwt("refresh", username, role, 24 * 60 * 60 * 1000L);

        addRefreshEntity(username, newRefresh);

        response.setHeader("access", newAccess);
        response.addCookie(new CookieUtil().createCookie("refresh", newRefresh));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    //redis에 refresh token 저장
    public void addRefreshEntity(String username, String refresh) {
        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refresh);

        refreshRepository.save(refreshEntity);
    }
}
