package com.solo.toyboard.config;

import com.solo.toyboard.filter.CustomLogoutFilter;
import com.solo.toyboard.filter.JWTFilter;
import com.solo.toyboard.util.JWTUtil;
import com.solo.toyboard.filter.LoginFilter;
import com.solo.toyboard.repository.RefreshRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${front-end.url}")
    private String frontEndUrl;

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil, RefreshRepository refreshRepository){

        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    //authenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{

        return authenticationConfiguration.getAuthenticationManager();
    }

    //비밀번호 암호화를 위한 메서드 생성
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();

    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        //cors 설정
        http
                .cors((cors) -> cors
                        .configurationSource(request -> corsConfiguration(request))
                );

        //csrf disable
        http
                .csrf((auth) -> auth.disable());

        //uri별 접근권한 설정
        http
                .authorizeHttpRequests((auth) -> auth
                        .anyRequest().authenticated()
                );

        //커스텀 필터를 등록
        http
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);

        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshRepository), UsernamePasswordAuthenticationFilter.class);

        //로그아웃 커스텀 필터
        http
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);


        //JWT방식으로 로그인 하기 위한 다른 로그인 과정 disable
        //로그인 폼 disable
        http
                .formLogin((auth) -> auth.disable());

        //http basic disable
        http
                .httpBasic((auth) -> auth.disable());

        //동시 로그인 허용
        http
                .sessionManagement((auth) -> auth
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(true)
                );

        //세션 설정 - JWT를 사용하기 위해 세션을 STATELESS하게 관리하기 위함
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

    //Cors 설정을 위한 메서드
    private CorsConfiguration corsConfiguration(HttpServletRequest request) {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Collections.singletonList(frontEndUrl));
        configuration.setAllowedMethods(Collections.singletonList("*"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setMaxAge(3600L);

        configuration.setExposedHeaders(Collections.singletonList("Authorization"));

        return configuration;
    }
}
