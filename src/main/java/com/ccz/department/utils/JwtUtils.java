package com.ccz.department.utils;

import com.ccz.department.config.JwtConfig;
import com.ccz.department.entity.Users;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : Chenchunze
 * @description : JWT工具类
 * @createDate : 2025/6/10 15:35
 */
@Slf4j
@Component
public class JwtUtils {

    @Autowired
    private JwtConfig jwtConfig;

    /**
     * 获取JWT密钥
     */
    private SecretKey getSecretKey() {
        byte[] keyBytes = jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8);
        
        // 确保密钥长度至少为64字节（512位）以满足HS512要求
        if (keyBytes.length < 64) {
            log.warn("JWT密钥长度不足，当前{}字节，HS512需要至少64字节", keyBytes.length);
            // 扩展密钥到64字节
            byte[] expandedKey = new byte[64];
            System.arraycopy(keyBytes, 0, expandedKey, 0, Math.min(keyBytes.length, 64));
            // 如果原密钥不够64字节，用重复的方式填充
            for (int i = keyBytes.length; i < 64; i++) {
                expandedKey[i] = keyBytes[i % keyBytes.length];
            }
            keyBytes = expandedKey;
        }
        
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成访问token
     */
    public String generateAccessToken(Users user) {
        return generateToken(user, jwtConfig.getExpiration() * 1000);
    }

    /**
     * 生成刷新token
     */
    public String generateRefreshToken(Users user) {
        return generateToken(user, jwtConfig.getRefreshExpiration() * 1000);
    }

    /**
     * 生成token
     */
    private String generateToken(Users user, long expirationTime) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());
        claims.put("email", user.getEmail());
        claims.put("status", user.getStatus());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSecretKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 从token中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? Long.valueOf(claims.get("userId").toString()) : null;
    }

    /**
     * 从token中获取用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? claims.getSubject() : null;
    }

    /**
     * 从token中获取邮箱
     */
    public String getEmailFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? claims.get("email").toString() : null;
    }

    /**
     * 从token中获取过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? claims.getExpiration() : null;
    }

    /**
     * 验证token是否有效
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims != null && !isTokenExpired(token);
        } catch (Exception e) {
            log.error("Token验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 检查token是否过期
     */
    public boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration != null && expiration.before(new Date());
    }

    /**
     * 检查token是否即将过期（30分钟内）
     */
    public boolean isTokenNearExpiry(String token) {
        Date expiration = getExpirationDateFromToken(token);
        if (expiration == null) {
            return false;
        }
        // 如果token在30分钟内过期，认为即将过期
        long timeToExpiry = expiration.getTime() - new Date().getTime();
        return timeToExpiry < 30 * 60 * 1000; // 30分钟 = 30 * 60 * 1000毫秒
    }

    /**
     * 从HTTP请求头中提取token
     */
    public String extractTokenFromHeader(String authHeader) {
        if (StringUtils.hasText(authHeader) && authHeader.startsWith(jwtConfig.getTokenPrefix())) {
            return authHeader.substring(jwtConfig.getTokenPrefix().length());
        }
        return null;
    }

    /**
     * 刷新token（如果即将过期）
     */
    public String refreshTokenIfNeeded(String token, Users user) {
        if (isTokenNearExpiry(token)) {
            log.info("Token即将过期，自动刷新: userId={}", user.getId());
            return generateAccessToken(user);
        }
        return null; // 不需要刷新
    }

    /**
     * 从token中获取Claims
     */
    private Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("Token已过期: {}", e.getMessage());
            return null;
        } catch (UnsupportedJwtException e) {
            log.error("不支持的JWT token: {}", e.getMessage());
            return null;
        } catch (MalformedJwtException e) {
            log.error("无效的JWT token: {}", e.getMessage());
            return null;
        } catch (SecurityException e) {
            log.error("JWT token签名验证失败: {}", e.getMessage());
            return null;
        } catch (IllegalArgumentException e) {
            log.error("JWT token为空: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("解析JWT token失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取token剩余有效时间（毫秒）
     */
    public long getTokenRemainingTime(String token) {
        Date expiration = getExpirationDateFromToken(token);
        if (expiration == null) {
            return 0;
        }
        long remaining = expiration.getTime() - new Date().getTime();
        return Math.max(0, remaining);
    }

    /**
     * 获取token剩余有效时间（秒）
     */
    public long getTokenRemainingTimeInSeconds(String token) {
        return getTokenRemainingTime(token) / 1000;
    }
} 