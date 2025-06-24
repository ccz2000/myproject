package com.ccz.department.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author : Chenchunze
 * @description : 用户登录数据传输对象
 * @createDate : 2025/6/5 15:30
 */

@Data
public class LoginDTO {
    @NotBlank(message = "用户名或邮箱不能为空")
    private String loginField; // 可以是用户名或邮箱

    @NotBlank(message = "密码不能为空")
    private String password;
    
    @NotBlank(message = "验证码不能为空")
    private String captcha; // 验证码
    
    @NotBlank(message = "会话ID不能为空")
    private String sessionId; // 会话ID
} 