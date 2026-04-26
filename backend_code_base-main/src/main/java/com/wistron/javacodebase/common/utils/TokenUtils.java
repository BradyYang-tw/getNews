package com.wistron.javacodebase.common.utils;

import org.springframework.security.oauth2.jwt.Jwt;

public class TokenUtils {
    private TokenUtils() {
    }

    public static String extractUserNameFromJwt(Jwt jwtToken) {
        if (jwtToken == null) {
            throw new IllegalArgumentException("JWT token cannot be null");
        }

        String userName = jwtToken.getClaimAsString("name");
        if (userName == null) {
            throw new IllegalArgumentException("JWT token does not contain 'name' claim");
        }

        // 名稱會是 `Name/WHQ/Wistron` 之類的格式，這邊只取 Name 的部分
        if (userName.contains("/")) {
            userName = userName.substring(0, userName.indexOf("/"));
        }

        return userName;
    }
}
