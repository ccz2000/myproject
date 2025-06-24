package com.ccz.department.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author : Chenchunze
 * @description : 文件上传配置类
 * @createDate : 2025/6/16
 */
@Data
@Component
@ConfigurationProperties(prefix = "file")
public class FileConfig {
    
    /**
     * 服务器端口
     */
    @Value("${server.port:8080}")
    private String serverPort;
    
    /**
     * 上传文件访问路径前缀
     */
    private String uploadPath = "/uploads";
    
    /**
     * 头像上传路径
     */
    private String avatarPath = "/avatars";
    
    /**
     * 获取完整的头像访问URL（动态获取服务器地址）
     */
    public String getAvatarUrl(String filename, HttpServletRequest request) {
        String serverUrl = getServerUrl(request);
        return serverUrl + uploadPath + avatarPath + "/" + filename;
    }
    
    /**
     * 获取完整的头像访问URL（使用相对路径）
     */
    public String getAvatarUrl(String filename) {
        return uploadPath + avatarPath + "/" + filename;
    }
    
    /**
     * 动态获取服务器URL
     */
    private String getServerUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();
        
        StringBuilder url = new StringBuilder();
        url.append(scheme).append("://").append(serverName);
        
        if ((scheme.equals("http") && serverPort != 80) || 
            (scheme.equals("https") && serverPort != 443)) {
            url.append(":").append(serverPort);
        }
        
        url.append(contextPath);
        return url.toString();
    }
} 