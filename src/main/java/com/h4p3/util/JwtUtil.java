package com.h4p3.util;

import com.h4p3.constant.MemberConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;

import javax.crypto.SecretKey;
import java.util.Map;

public class JwtUtil {

    private static final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    public static String getToken(HttpServletRequest request) {
        String token = request.getHeader(MemberConstants.REQUEST_HEAD_AUTH);
        if (StringUtils.isNotEmpty(token) && token.startsWith(MemberConstants.BEARER)) {
            token = token.replace(MemberConstants.BEARER, "");
        }
        return token;
    }

    public static String createToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .signWith(key)
                .compact();
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
