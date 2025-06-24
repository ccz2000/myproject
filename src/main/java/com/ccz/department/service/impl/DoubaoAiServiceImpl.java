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
@ConditionalOnProperty(name = "ai.provider", havingValue = "doubao")
public class DoubaoAiServiceImpl implements AiService {

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
                        .error("豆包API Key未配置，请检查access-key-id配置")
                        .timestamp(LocalDateTime.now())
                        .build();
            }

            // 构建请求URL - 豆包使用火山引擎的API
            String baseUrl = StringUtils.hasText(aiConfig.getBaseUrl()) ? 
                    aiConfig.getBaseUrl() : "https://ark.cn-beijing.volces.com/api/v3";
            String url = baseUrl + "/chat/completions";

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            
            // 豆包需要指定模型，如果没有配置则使用默认值
            String model = StringUtils.hasText(aiConfig.getModel()) ? 
                    aiConfig.getModel() : "doubao-1-5-pro-32k-250115";
            requestBody.put("model", model);
            
            // 构建messages
            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", request.getQuestion());
            messages.add(userMessage);
            
            requestBody.put("messages", messages);
            requestBody.put("temperature", aiConfig.getTemperature());
            requestBody.put("max_tokens", aiConfig.getMaxTokens());
            
            // 豆包特有参数
            requestBody.put("stream", false);

            String requestBodyJson = JSON.toJSONString(requestBody);
            
            log.debug("豆包请求信息 - URL: {}, Model: {}, Body: {}", url, model, requestBodyJson);

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
                            .error("豆包错误: " + error.getString("message"))
                            .timestamp(LocalDateTime.now())
                            .duration(System.currentTimeMillis() - startTime)
                            .build();
                }
                
                // 解析豆包响应格式
                if (responseJson.containsKey("choices") && responseJson.getJSONArray("choices").size() > 0) {
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
                            .model(model)
                            .duration(System.currentTimeMillis() - startTime)
                            .build();
                } else {
                    return ChatResponse.builder()
                            .success(false)
                            .error("豆包响应格式异常：未找到有效的回答内容")
                            .timestamp(LocalDateTime.now())
                            .duration(System.currentTimeMillis() - startTime)
                            .build();
                }
            } else {
                return ChatResponse.builder()
                        .success(false)
                        .error("豆包服务响应异常，状态码: " + response.getStatusCode())
                        .timestamp(LocalDateTime.now())
                        .duration(System.currentTimeMillis() - startTime)
                        .build();
            }
            
        } catch (Exception e) {
            log.error("调用豆包服务异常", e);
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
            log.error("检查豆包服务可用性失败", e);
            return false;
        }
    }
} 