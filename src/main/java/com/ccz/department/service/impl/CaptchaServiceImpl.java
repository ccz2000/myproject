package com.ccz.department.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ccz.department.entity.CaptchaVerification;
import com.ccz.department.mapper.CaptchaVerificationMapper;
import com.ccz.department.service.CaptchaService;
import com.ccz.department.utils.CaptchaUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Slf4j
@Service
public class CaptchaServiceImpl extends ServiceImpl<CaptchaVerificationMapper, CaptchaVerification> implements CaptchaService {

    @Resource
    private CaptchaVerificationMapper captchaMapper;
    
    @Resource
    private CaptchaUtils captchaUtils;

    @Override
    public CaptchaUtils.CaptchaResult generateCaptcha(String sessionId) {
        // 生成验证码
        CaptchaUtils.CaptchaResult captchaResult = captchaUtils.generateCaptcha();
        
        // 先删除该会话之前的验证码
        captchaMapper.delete(new LambdaQueryWrapper<CaptchaVerification>()
                .eq(CaptchaVerification::getSessionId, sessionId));
        
        // 保存新验证码到数据库
        CaptchaVerification verification = new CaptchaVerification();
        verification.setSessionId(sessionId);
        verification.setCaptchaCode(captchaResult.getCode());
        verification.setCaptchaImage(captchaResult.getImageBase64());
        verification.setStatus(0); // 未使用
        verification.setExpireTime(LocalDateTime.now().plusMinutes(5)); // 5分钟过期
        
        captchaMapper.insert(verification);
        
        log.info("为会话 {} 生成验证码", sessionId);
        return captchaResult;
    }

    @Override
    public boolean verifyCaptcha(String sessionId, String inputCode) {
        // 查找有效的验证码
        CaptchaVerification verification = captchaMapper.selectOne(
            new LambdaQueryWrapper<CaptchaVerification>()
                .eq(CaptchaVerification::getSessionId, sessionId)
                .eq(CaptchaVerification::getStatus, 0)
                .gt(CaptchaVerification::getExpireTime, LocalDateTime.now())
                .orderByDesc(CaptchaVerification::getCreatedAt)
                .last("LIMIT 1")
        );
        
        if (verification == null) {
            log.warn("验证码不存在或已过期，会话: {}", sessionId);
            return false;
        }
        
        // 验证验证码
        boolean isValid = captchaUtils.verifyCaptcha(inputCode, verification.getCaptchaCode());
        
        if (isValid) {
            // 标记为已使用
            verification.setStatus(1);
            captchaMapper.updateById(verification);
            log.info("验证码验证成功，会话: {}", sessionId);
        } else {
            log.warn("验证码验证失败，会话: {}, 输入: {}, 期望: {}", sessionId, inputCode, verification.getCaptchaCode());
        }
        
        return isValid;
    }

    @Override
    public void cleanExpiredCaptcha() {
        int count = captchaMapper.delete(new LambdaQueryWrapper<CaptchaVerification>()
                .lt(CaptchaVerification::getExpireTime, LocalDateTime.now()));
        
        if (count > 0) {
            log.info("清理过期验证码 {} 条", count);
        }
    }
} 