package com.ccz.department.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author : Chenchunze
 * @description : JWT配置类
 * @createDate : 2025/6/10 15:30
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    
    /**
     * JWT密钥
     */
    private String secret;
    
    /**
     * token过期时间（秒）
     */
    private Long expiration;
    
    /**
     * 刷新token过期时间（秒）
     */
    private Long refreshExpiration;
    
    /**
     * 请求头名称
     */
    private String header = "Authorization";
    
    /**
     * token前缀
     */
    private String tokenPrefix = "Bearer ";
} 