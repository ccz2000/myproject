package com.ccz.department.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    
    /**
     * 响应内容
     */
    private String answer;
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 响应时间
     */
    private LocalDateTime timestamp;
    
    /**
     * 消耗的tokens数量
     */
    private Integer tokens;
    
    /**
     * 响应是否成功
     */
    private Boolean success;
    
    /**
     * 错误信息（如果有）
     */
    private String error;
    
    /**
     * AI模型名称
     */
    private String model;
    
    /**
     * 响应时长（毫秒）
     */
    private Long duration;
} 