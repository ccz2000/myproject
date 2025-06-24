package com.ccz.department.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "ai")
public class AiConfig {
    
    /**
     * AI服务提供商 (baidu/openai/aliyun等)
     */
    private String provider = "baidu";
    
    /**
     * IAM Access Key ID
     */
    private String accessKeyId;
    
    /**
     * IAM Secret Access Key
     */
    private String secretAccessKey;
    
    /**
     * API基础URL
     */
    private String baseUrl = "https://aip.baidubce.com";
    
    /**
     * 模型名称
     */
    private String model = "ernie-3.5-8k";
    
    /**
     * 请求超时时间(秒)
     */
    private int timeout = 30;
    
    /**
     * 最大tokens数
     */
    private int maxTokens = 2000;
    
    /**
     * 温度参数(0-1)
     */
    private double temperature = 0.7;
} 