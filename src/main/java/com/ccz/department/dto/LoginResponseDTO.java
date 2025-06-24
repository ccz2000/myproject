package com.ccz.department.dto;

import lombok.Data;

/**
 * @author : Chenchunze
 * @description : 登录响应数据传输对象
 * @createDate : 2025/6/5 15:30
 */

@Data
public class LoginResponseDTO {
    private String token;
    private String refreshToken;
    private Long expiresIn; // token剩余有效时间（秒）
    private UsersDTO userInfo;
    private String message;
} 