package com.ccz.department.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ccz.department.config.AiConfig;
import com.ccz.department.dto.ChatRequest;
import com.ccz.department.dto.ChatResponse;
import com.ccz.department.service.AiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@ConditionalOnProperty(name = "ai.provider", havingValue = "openai")
public class OpenAiServiceImpl implements AiService {

    @Autowired
    private AiConfig aiConfig;
    
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public ChatResponse chat(ChatRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            // 检查API Key配置
            if (!StringUtils.hasText(aiConfig.getAccessKeyId())) {
                return ChatResponse.builder()
                        .success(false)
                        .error("OpenAI API Key未配置，请检查access-key-id配置")
                        .timestamp(LocalDateTime.now())
                        .build();
            }

            // 构建请求URL
            String url = "https://api.openai.com/v1/chat/completions";

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-3.5-turbo");
            
            // 构建messages
            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", request.getQuestion());
            messages.add(userMessage);
            
            requestBody.put("messages", messages);
            requestBody.put("temperature", aiConfig.getTemperature());
            requestBody.put("max_tokens", aiConfig.getMaxTokens());

            String requestBodyJson = JSON.toJSONString(requestBody);
            
            log.debug("OpenAI请求信息 - URL: {}, Body: {}", url, requestBodyJson);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + aiConfig.getAccessKeyId());
            
            HttpEntity<String> entity = new HttpEntity<>(requestBodyJson, headers);

            // 发送请求
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            // 解析响应
            if (response.getStatusCode() == HttpStatus.OK && StringUtils.hasText(response.getBody())) {
                JSONObject responseJson = JSON.parseObject(response.getBody());
                
                if (responseJson.containsKey("error")) {
                    JSONObject error = responseJson.getJSONObject("error");
                    return ChatResponse.builder()
                            .success(false)
                            .error("OpenAI错误: " + error.getString("message"))
                            .timestamp(LocalDateTime.now())
                            .duration(System.currentTimeMillis() - startTime)
                            .build();
                }
                
                JSONObject choice = responseJson.getJSONArray("choices").getJSONObject(0);
                JSONObject message = choice.getJSONObject("message");
                String answer = message.getString("content");
                
                JSONObject usage = responseJson.getJSONObject("usage");
                Integer totalTokens = usage != null ? usage.getInteger("total_tokens") : null;
                
                return ChatResponse.builder()
                        .success(true)
                        .answer(answer)
                        .sessionId(request.getSessionId())
                        .timestamp(LocalDateTime.now())
                        .tokens(totalTokens)
                        .model("gpt-3.5-turbo")
                        .duration(System.currentTimeMillis() - startTime)
                        .build();
            } else {
                return ChatResponse.builder()
                        .success(false)
                        .error("OpenAI服务响应异常，状态码: " + response.getStatusCode())
                        .timestamp(LocalDateTime.now())
                        .duration(System.currentTimeMillis() - startTime)
                        .build();
            }
            
        } catch (Exception e) {
            log.error("调用OpenAI服务异常", e);
            return ChatResponse.builder()
                    .success(false)
                    .error("服务调用异常: " + e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .duration(System.currentTimeMillis() - startTime)
                    .build();
        }
    }

    @Override
    public boolean isAvailable() {
        try {
            // 检查API Key配置
            return StringUtils.hasText(aiConfig.getAccessKeyId());
        } catch (Exception e) {
            log.error("检查OpenAI服务可用性失败", e);
            return false;
        }
    }
} 