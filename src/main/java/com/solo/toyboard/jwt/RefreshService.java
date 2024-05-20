package com.solo.toyboard.jwt;

import com.solo.toyboard.entity.RefreshEntity;
import com.solo.toyboard.repository.RefreshRepository;
import com.solo.toyboard.util.CookieUtil;
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

    public ResponseEntity<?> reissueToken(HttpServletRequest request, HttpServletResponse response) {

        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies) {
            if(cookie.getName().equals("refresh")) refresh = cookie.getValue();
        }

        if(refresh == null) return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);

        try {
            jwtUtil.isExpired(refresh);
        }
        catch (ExpiredJwtException e) {
            return new ResponseEntity<>("refresh token expire", HttpStatus.BAD_REQUEST);
        }

        String category = jwtUtil.getCategory(refresh);
        if(!category.equals("refresh")) return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);

        boolean isExist = refreshRepository.existsById(refresh);
        if(!isExist) return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        String newAccess = jwtUtil.createJwt("access", username, role, 10 * 60 * 1000L);
        String newRefresh = jwtUtil.createJwt("refresh", username, role, 24 * 60 * 60 * 1000L);

        addRefreshEntity(username, newRefresh);

        response.setHeader("access", newAccess);
        response.addCookie(new CookieUtil().createCookie("refresh", newRefresh));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    public void addRefreshEntity(String username, String refresh) {
        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refresh);

        refreshRepository.save(refreshEntity);
    }
}
