package com.ccz.department.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author : Chenchunze
 * @description : 邮箱验证码实体类
 * @createDate : 2025/6/5 18:00
 */
@Data
@TableName("email_verification")
public class EmailVerification {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String email;

    private String verificationCode;

    private String purpose; // 验证目的：REGISTER（注册）、RESET_PASSWORD（重置密码）等

    private Integer status; // 0:未使用 1:已使用 2:已过期

    @TableField("user_id")
    private Long userId;

    @TableField("expire_time")
    private LocalDateTime expireTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
} 