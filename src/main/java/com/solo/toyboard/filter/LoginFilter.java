package com.solo.toyboard.filter;

import com.solo.toyboard.service.RefreshService;
import com.solo.toyboard.repository.RefreshRepository;
import com.solo.toyboard.util.CookieUtil;
import com.solo.toyboard.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

//default로 loginForm방식을 지원하지만 JWT를 사용하기 위해 커스텀
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, RefreshRepository refreshRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        //요청에서 username password 추출
        String username = obtainUsername(request);
        String password = obtainPassword(request);

        //로그인 검증을 위한 토큰생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password, null);

        //토큰을 검증을 위해 manager로 전송
        return authenticationManager.authenticate(authenticationToken);
    }

    //로그인 성공시 JWT발급으로 이동
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        String username = authResult.getName();
        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        Iterator<? extends  GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority authority = iterator.next();
        String role = authority.getAuthority();

        String access = jwtUtil.createJwt("access", username, role, 10 * 60 * 1000L);
        String refresh = jwtUtil.createJwt("refresh", username, role, 24 * 60 * 60 * 1000L);

        new RefreshService(jwtUtil, refreshRepository).addRefreshEntity(username, refresh);

        response.addHeader("access", access);
        response.addCookie(new CookieUtil().createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());
    }

    //로그인 실패시 실행
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

        response.setStatus(401);
    }
}
