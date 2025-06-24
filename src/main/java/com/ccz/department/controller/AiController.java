package com.ccz.department.controller;

import com.ccz.department.annotation.RequirePermission;
import com.ccz.department.dto.ChatRequest;
import com.ccz.department.dto.ChatResponse;
import com.ccz.department.service.AiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*") // 允许跨域请求
public class AiController {

    @Autowired
    private AiService aiService;

    /**
     * 聊天接口
     */
    @PostMapping("/chat")
    @RequirePermission
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        log.info("收到聊天请求: {}", request.getQuestion());
        
        try {
            ChatResponse response = aiService.chat(request);
            log.info("聊天响应: success={}, duration={}ms", response.getSuccess(), response.getDuration());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("聊天处理异常", e);
            ChatResponse errorResponse = ChatResponse.builder()
                    .success(false)
                    .error("服务异常: " + e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * 检查AI服务状态
     */
    @GetMapping("/status")
    @RequirePermission
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        try {
            boolean available = aiService.isAvailable();
            status.put("available", available);
            status.put("message", available ? "AI服务正常" : "AI服务不可用");
            status.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.error("检查AI服务状态异常", e);
            status.put("available", false);
            status.put("message", "检查服务状态异常: " + e.getMessage());
            status.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(500).body(status);
        }
    }

    /**
     * 获取AI配置信息（不包含敏感信息）
     */
    @GetMapping("/config")
    @RequirePermission
    public ResponseEntity<Map<String, Object>> getConfig() {
        Map<String, Object> config = new HashMap<>();
        
        // 根据当前提供商显示不同信息  
        String serviceClass = aiService.getClass().getSimpleName();
        if (serviceClass.contains("Doubao")) {
            config.put("provider", "AI Assistant");
            config.put("model", "Doubao-1.5-Pro-32K");
            config.put("authType", "API Key");
        } else if (aiService instanceof com.ccz.department.service.impl.OpenAiServiceImpl) {
            config.put("provider", "OpenAI");
            config.put("model", "GPT-3.5-Turbo");
            config.put("authType", "API Key");
        } else {
            config.put("provider", "百度千帆 (IAM认证)");
            config.put("model", "ERNIE-3.5-8K");
            config.put("authType", "IAM (Access Key)");
        }
        
        config.put("features", new String[]{
            "智能问答", "多轮对话", "知识问答", "创意写作", "代码助手"
        });
        config.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(config);
    }
} 