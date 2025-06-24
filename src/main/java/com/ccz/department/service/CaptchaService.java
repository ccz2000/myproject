package com.ccz.department.service;

import com.ccz.department.utils.CaptchaUtils;

public interface CaptchaService {
    
    /**
     * 生成验证码
     * @param sessionId 会话ID
     * @return 验证码结果（包含图片base64）
     */
    CaptchaUtils.CaptchaResult generateCaptcha(String sessionId);
    
    /**
     * 验证验证码
     * @param sessionId 会话ID
     * @param inputCode 用户输入的验证码
     * @return 验证结果
     */
    boolean verifyCaptcha(String sessionId, String inputCode);
    
    /**
     * 清理过期验证码
     */
    void cleanExpiredCaptcha();
} 