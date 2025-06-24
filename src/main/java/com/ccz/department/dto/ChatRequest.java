package com.ccz.department.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class ChatRequest {
    
    /**
     * 用户输入的问题
     */
    @NotBlank(message = "问题不能为空")
    @Size(max = 2000, message = "问题长度不能超过2000字符")
    private String question;
    
    /**
     * 会话ID（可选，用于维持上下文）
     */
    private String sessionId;
    
    /**
     * 是否流式响应
     */
    private Boolean stream = false;
} 