package com.ccz.department.service;

import com.ccz.department.entity.EmailVerification;

/**
 * @author : Chenchunze
 * @description : 邮件服务接口
 * @createDate : 2025/6/5 18:10
 */
public interface EmailService {

    /**
     * 发送注册验证邮件
     * @param email 邮箱地址
     * @param userId 用户ID
     * @return 是否发送成功
     */
    boolean sendRegisterVerificationEmail(String email, Long userId);

    /**
     * 验证邮箱验证码
     * @param email 邮箱地址
     * @param code 验证码
     * @param purpose 验证目的
     * @return 验证是否成功
     */
    boolean verifyEmailCode(String email, String code, String purpose);

    /**
     * 生成验证码
     * @param email 邮箱地址
     * @param userId 用户ID
     * @param purpose 验证目的
     * @return 验证码记录
     */
    EmailVerification generateVerificationCode(String email, Long userId, String purpose);

    /**
     * 发送HTML邮件
     * @param to 收件人
     * @param subject 主题
     * @param content HTML内容
     * @return 是否发送成功
     */
    boolean sendHtmlEmail(String to, String subject, String content);
    
    /**
     * 发送重置密码邮件
     * @param email 邮箱地址
     * @param userId 用户ID
     * @return 是否发送成功
     */
    boolean sendResetPasswordEmail(String email, Long userId);
} 