package com.solo.toyboard.util;

import jakarta.servlet.http.Cookie;

public class CookieUtil {
    public Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setHttpOnly(true);

        return cookie;
    }
}
