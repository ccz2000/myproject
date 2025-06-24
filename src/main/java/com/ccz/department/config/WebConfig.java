package com.ccz.department.config;

import com.ccz.department.interceptor.PermissionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.io.File;

/**
 * @author : Chenchunze
 * @description : Web配置类，解决跨域问题
 * @createDate : 2025/6/5 20:00
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private PermissionInterceptor permissionInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 使用默认的静态资源配置，Spring Boot会自动处理classpath:/static/下的文件
        System.out.println("使用默认静态资源配置: classpath:/static/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/**") // 拦截所有请求
                .excludePathPatterns(
                        "/auth/login",           // 登录接口
                        "/auth/register",        // 注册接口
                        "/auth/verify-email",    // 邮箱验证接口
                        "/auth/resend-verification", // 重发验证邮件
                        "/auth/check-username/**",   // 检查用户名
                        "/auth/check-email/**",      // 检查邮箱
                        "/auth/refresh-token",       // 刷新token
                        "/auth/captcha",         // 验证码接口
                        "/auth/forgot-password", // 忘记密码接口
                        "/auth/reset-password",  // 重置密码接口
                        "/static/**",            // 静态资源
                        "/avatars/**",           // 头像资源
                        "/error",                // 错误页面
                        "/favicon.ico"           // 网站图标
                );
    }

} 