package com.solo.toyboard.util;

import jakarta.servlet.http.Cookie;

//cookie 유효기간 및 http에서만 접근 가능하도록 속성 부여
public class CookieUtil {
    public Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setHttpOnly(true);

        return cookie;
    }
}
