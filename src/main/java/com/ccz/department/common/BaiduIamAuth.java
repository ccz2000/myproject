package com.ccz.department.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
public class BaiduIamAuth {
    
    private static final String BCE_PREFIX = "bce-auth-v1";
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final String ALGORITHM = "HmacSHA256";
    
    /**
     * 生成IAM认证签名
     */
    public static String generateAuth(String accessKeyId, String secretAccessKey, 
                                    String httpMethod, String uri, String queryString, 
                                    Map<String, String> headers, String requestBody, String timestamp) {
        try {
            // 1. 准备标准的headers (必须包含host, content-type, 和date)
            Map<String, String> standardHeaders = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            standardHeaders.put("host", "aip.baidubce.com");
            standardHeaders.put("content-type", "application/json");
            standardHeaders.put("x-bce-date", timestamp);
            if (headers != null) {
                standardHeaders.putAll(headers);
            }
            
            // 2. 生成CanonicalRequest
            String canonicalRequest = createCanonicalRequest(httpMethod, uri, queryString, standardHeaders, requestBody);
            log.debug("CanonicalRequest: \n{}", canonicalRequest);
            
            // 3. 生成StringToSign
            String expirationPeriodInSeconds = "1800";
            String signedHeaders = getSignedHeaders(standardHeaders);
            String stringToSign = BCE_PREFIX + "/" + accessKeyId + "/" + timestamp + "/" + expirationPeriodInSeconds + "\n" + canonicalRequest;
            log.debug("StringToSign: \n{}", stringToSign);
            
            // 4. 生成SigningKey (直接使用secretAccessKey)
            String signingKey = secretAccessKey;
            
            // 5. 生成Signature
            String signature = hmacSha256Hex(signingKey, stringToSign);
            log.debug("Signature: {}", signature);
            
            // 6. 生成Authorization header
            String authHeader = BCE_PREFIX + "/" + accessKeyId + "/" + timestamp + "/" + expirationPeriodInSeconds + "/" + signedHeaders + "/" + signature;
            log.debug("Authorization Header: {}", authHeader);
            
            return authHeader;
            
        } catch (Exception e) {
            log.error("生成IAM认证签名失败", e);
            throw new RuntimeException("生成IAM认证签名失败", e);
        }
    }
    
    /**
     * 创建标准请求
     */
    private static String createCanonicalRequest(String httpMethod, String uri, String queryString, Map<String, String> headers, String requestBody) {
        StringBuilder canonicalRequest = new StringBuilder();
        
        // 1. HTTP Method
        canonicalRequest.append(httpMethod.toUpperCase()).append("\n");
        
        // 2. Canonical URI (需要进行URI编码，但保留/)
        String encodedUri = uriEncode(uri, false);
        canonicalRequest.append(encodedUri).append("\n");
        
        // 3. Canonical Query String (如果没有查询参数则为空)
        String canonicalQueryString = "";
        if (StringUtils.hasText(queryString)) {
            canonicalQueryString = normalizeQueryString(queryString);
        }
        canonicalRequest.append(canonicalQueryString).append("\n");
        
        // 4. Canonical Headers (必须按字母序排列，并且都是小写)
        StringBuilder canonicalHeaders = new StringBuilder();
        if (headers != null && !headers.isEmpty()) {
            Map<String, String> sortedHeaders = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            sortedHeaders.putAll(headers);
            
            for (Map.Entry<String, String> entry : sortedHeaders.entrySet()) {
                String key = entry.getKey().toLowerCase().trim();
                String value = entry.getValue().trim();
                canonicalHeaders.append(key).append(":").append(value).append("\n");
            }
        }
        canonicalRequest.append(canonicalHeaders.toString()).append("\n");
        
        // 5. Signed Headers (header名称列表，用分号分隔)
        String signedHeaders = getSignedHeaders(headers);
        canonicalRequest.append(signedHeaders).append("\n");
        
        // 6. 请求体的SHA256哈希
        String bodyHash = sha256Hex(requestBody != null ? requestBody : "");
        canonicalRequest.append(bodyHash);
        
        return canonicalRequest.toString();
    }
    
    /**
     * 获取签名的header列表
     */
    private static String getSignedHeaders(Map<String, String> headers) {
        if (headers == null || headers.isEmpty()) {
            return "";
        }
        
        Map<String, String> sortedHeaders = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        sortedHeaders.putAll(headers);
        
        StringBuilder signedHeaders = new StringBuilder();
        for (String key : sortedHeaders.keySet()) {
            if (signedHeaders.length() > 0) {
                signedHeaders.append(";");
            }
            signedHeaders.append(key.toLowerCase());
        }
        
        return signedHeaders.toString();
    }
    
    /**
     * HMAC-SHA256加密
     */
    private static String hmacSha256Hex(String key, String data) throws Exception {
        Mac mac = Mac.getInstance(ALGORITHM);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        mac.init(secretKeySpec);
        byte[] digest = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(digest);
    }
    
    /**
     * 字节数组转16进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
    
    /**
     * SHA256哈希
     */
    private static String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("SHA256计算失败", e);
        }
    }
    
    /**
     * URI编码
     */
    private static String uriEncode(String input, boolean encodeSlash) {
        try {
            String encoded = URLEncoder.encode(input, DEFAULT_ENCODING)
                    .replace("+", "%20")
                    .replace("*", "%2A")
                    .replace("%7E", "~");
            if (!encodeSlash) {
                encoded = encoded.replace("%2F", "/");
            }
            return encoded;
        } catch (Exception e) {
            throw new RuntimeException("URI编码失败", e);
        }
    }
    
    /**
     * 标准化查询字符串
     */
    private static String normalizeQueryString(String queryString) {
        if (!StringUtils.hasText(queryString)) {
            return "";
        }
        
        Map<String, String> params = new TreeMap<>();
        String[] pairs = queryString.split("&");
        
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            String key = uriEncode(keyValue[0], true);
            String value = keyValue.length > 1 ? uriEncode(keyValue[1], true) : "";
            params.put(key, value);
        }
        
        StringBuilder normalized = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (normalized.length() > 0) {
                normalized.append("&");
            }
            normalized.append(entry.getKey()).append("=").append(entry.getValue());
        }
        
        return normalized.toString();
    }
} 