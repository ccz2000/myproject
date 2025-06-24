package com.ccz.department.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("captcha_verification")
public class CaptchaVerification {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String sessionId;
    private String captchaCode;
    private String captchaImage;
    private Integer status; // 0:未使用 1:已使用 2:已过期
    private LocalDateTime expireTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
} 