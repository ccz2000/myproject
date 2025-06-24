package com.ccz.department.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ccz.department.common.BaiduIamAuth;
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
@ConditionalOnProperty(name = "ai.provider", havingValue = "baidu", matchIfMissing = true)
public class BaiduAiServiceImpl implements AiService {

    @Autowired
    private AiConfig aiConfig;
    
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public ChatResponse chat(ChatRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            // 检查IAM配置
            if (!StringUtils.hasText(aiConfig.getAccessKeyId()) || !StringUtils.hasText(aiConfig.getSecretAccessKey())) {
                return ChatResponse.builder()
                        .success(false)
                        .error("IAM认证配置不完整，请检查accessKeyId和secretAccessKey")
                        .timestamp(LocalDateTime.now())
                        .build();
            }

            // 构建请求URL - 使用简化的IAM路径
            String path = "/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/ernie_bot_turbo";
            String url = aiConfig.getBaseUrl() + path;

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            
            // 指定模型
            requestBody.put("model", "ERNIE-Bot-turbo");
            
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
            
            // 生成统一的时间戳，用于认证签名和请求头
            String timestamp = java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC)
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
            
            // 生成IAM认证签名
            String authorization = BaiduIamAuth.generateAuth(
                    aiConfig.getAccessKeyId(),
                    aiConfig.getSecretAccessKey(),
                    "POST",
                    path,
                    null,
                    null,  // headers在方法内部标准化
                    requestBodyJson,
                    timestamp
            );
            
            log.debug("IAM认证信息 - URL: {}, Authorization: {}, Timestamp: {}", url, authorization, timestamp);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", authorization);
            headers.set("Host", "aip.baidubce.com");
            headers.set("x-bce-date", timestamp);
            
            HttpEntity<String> entity = new HttpEntity<>(requestBodyJson, headers);

            // 发送请求
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            // 解析响应
            if (response.getStatusCode() == HttpStatus.OK && StringUtils.hasText(response.getBody())) {
                JSONObject responseJson = JSON.parseObject(response.getBody());
                
                if (responseJson.containsKey("error_code")) {
                    return ChatResponse.builder()
                            .success(false)
                            .error("AI服务错误: " + responseJson.getString("error_msg"))
                            .timestamp(LocalDateTime.now())
                            .duration(System.currentTimeMillis() - startTime)
                            .build();
                }
                
                String answer = responseJson.getString("result");
                JSONObject usage = responseJson.getJSONObject("usage");
                Integer totalTokens = usage != null ? usage.getInteger("total_tokens") : null;
                
                return ChatResponse.builder()
                        .success(true)
                        .answer(answer)
                        .sessionId(request.getSessionId())
                        .timestamp(LocalDateTime.now())
                        .tokens(totalTokens)
                        .model(aiConfig.getModel())
                        .duration(System.currentTimeMillis() - startTime)
                        .build();
            } else {
                return ChatResponse.builder()
                        .success(false)
                        .error("AI服务响应异常，状态码: " + response.getStatusCode())
                        .timestamp(LocalDateTime.now())
                        .duration(System.currentTimeMillis() - startTime)
                        .build();
            }
            
        } catch (Exception e) {
            log.error("调用AI服务异常", e);
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
            // 检查IAM配置是否完整
            return StringUtils.hasText(aiConfig.getAccessKeyId()) && 
                   StringUtils.hasText(aiConfig.getSecretAccessKey());
        } catch (Exception e) {
            log.error("检查AI服务可用性失败", e);
            return false;
        }
    }
} 