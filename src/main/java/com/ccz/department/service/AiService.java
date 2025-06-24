package com.ccz.department.service;

import com.ccz.department.dto.ChatRequest;
import com.ccz.department.dto.ChatResponse;

public interface AiService {
    
    /**
     * 发送聊天请求到AI服务
     * 
     * @param request 聊天请求
     * @return 聊天响应
     */
    ChatResponse chat(ChatRequest request);
    
    /**
     * 检查AI服务是否可用
     * 
     * @return 是否可用
     */
    boolean isAvailable();
} 