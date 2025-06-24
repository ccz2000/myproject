package com.ccz.department.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ccz.department.entity.EmailVerification;
import com.ccz.department.mapper.EmailVerificationMapper;
import com.ccz.department.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
public class EmailServiceImpl extends ServiceImpl<EmailVerificationMapper, EmailVerification> implements EmailService {

    @Resource
    private JavaMailSender mailSender;

    @Resource
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${app.mail.verify-url}")
    private String verifyUrl;

    @Override
    public boolean sendRegisterVerificationEmail(String email, Long userId) {
        try {
            // 生成验证码
            EmailVerification verification = generateVerificationCode(email, userId, "REGISTER");
            
            // 构建邮件内容
            Context context = new Context();
            context.setVariable("verificationCode", verification.getVerificationCode());
            context.setVariable("verifyUrl", verifyUrl + "?email=" + email + "&code=" + verification.getVerificationCode());
            context.setVariable("expireTime", verification.getExpireTime());
            
            String htmlContent = templateEngine.process("email/register-verification", context);
            
            // 发送邮件
            return sendHtmlEmail(email, "【部门管理系统】邮箱验证", htmlContent);
        } catch (Exception e) {
            log.error("发送注册验证邮件失败: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean verifyEmailCode(String email, String code, String purpose) {
        try {
            QueryWrapper<EmailVerification> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("email", email)
                      .eq("verification_code", code)
                      .eq("purpose", purpose)
                      .eq("status", 0) // 未使用
                      .gt("expire_time", LocalDateTime.now()); // 未过期

            EmailVerification verification = getOne(queryWrapper);
            
            if (verification != null) {
                // 标记为已使用
                verification.setStatus(1);
                verification.setUpdatedAt(LocalDateTime.now());
                updateById(verification);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("验证邮箱验证码失败: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public EmailVerification generateVerificationCode(String email, Long userId, String purpose) {
        // 生成6位数验证码
        String code = String.format("%06d", new Random().nextInt(999999));
        
        EmailVerification verification = new EmailVerification();
        verification.setEmail(email);
        verification.setUserId(userId);
        verification.setVerificationCode(code);
        verification.setPurpose(purpose);
        verification.setStatus(0); // 未使用
        verification.setExpireTime(LocalDateTime.now().plusMinutes(30)); // 30分钟后过期
        verification.setCreatedAt(LocalDateTime.now());
        verification.setUpdatedAt(LocalDateTime.now());
        
        save(verification);
        return verification;
    }

    @Override
    public boolean sendHtmlEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true); // true表示发送HTML格式邮件
            
            mailSender.send(message);
            log.info("邮件发送成功: {} -> {}", from, to);
            return true;
        } catch (Exception e) {
            log.error("邮件发送失败: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean sendResetPasswordEmail(String email, Long userId) {
        try {
            // 生成验证码
            EmailVerification verification = generateVerificationCode(email, userId, "RESET_PASSWORD");
            
            // 构建邮件内容
            String htmlContent = String.format(
                "<div style='padding: 20px; font-family: Arial, sans-serif;'>" +
                "<h2 style='color: #333;'>重置密码验证</h2>" +
                "<p>您好，您正在申请重置密码。</p>" +
                "<p>验证码：<strong style='color: #007bff; font-size: 18px;'>%s</strong></p>" +
                "<p>验证码有效期：30分钟</p>" +
                "<p style='color: #666; font-size: 12px;'>如果不是您本人操作，请忽略此邮件。</p>" +
                "<hr style='margin: 20px 0;'>" +
                "<p style='color: #999; font-size: 12px;'>此邮件由系统自动发送，请勿回复。</p>" +
                "</div>",
                verification.getVerificationCode()
            );
            
            // 发送邮件
            return sendHtmlEmail(email, "【部门管理系统】重置密码验证", htmlContent);
        } catch (Exception e) {
            log.error("发送重置密码邮件失败: {}", e.getMessage());
            return false;
        }
    }
} 