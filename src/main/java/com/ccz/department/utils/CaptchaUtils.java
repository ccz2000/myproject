package com.ccz.department.utils;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

@Component
public class CaptchaUtils {

    private static final String NUMBERS = "0123456789";
    private static final String LETTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String ALL_CHARS = NUMBERS + LETTERS;
    
    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;
    private static final int CODE_LENGTH = 4;

    @Data
    public static class CaptchaResult {
        private String code;
        private String imageBase64;
    }

    /**
     * 生成验证码
     */
    public CaptchaResult generateCaptcha() {
        String code = generateRandomCode();
        String imageBase64 = generateCaptchaImage(code);
        
        CaptchaResult result = new CaptchaResult();
        result.setCode(code);
        result.setImageBase64(imageBase64);
        return result;
    }

    /**
     * 生成随机验证码
     */
    private String generateRandomCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length())));
        }
        return code.toString();
    }

    /**
     * 生成验证码图片
     */
    private String generateCaptchaImage(String code) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 设置抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 设置背景色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 画干扰线
        Random random = new Random();
        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < 10; i++) {
            int x1 = random.nextInt(WIDTH);
            int y1 = random.nextInt(HEIGHT);
            int x2 = random.nextInt(WIDTH);
            int y2 = random.nextInt(HEIGHT);
            g.drawLine(x1, y1, x2, y2);
        }

        // 画验证码字符
        Font font = new Font("Arial", Font.BOLD, 24);
        g.setFont(font);
        
        for (int i = 0; i < code.length(); i++) {
            // 随机颜色
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            
            // 计算字符位置
            int x = (WIDTH / CODE_LENGTH) * i + 15;
            int y = HEIGHT / 2 + 8;
            
            // 随机旋转角度
            double angle = (random.nextDouble() - 0.5) * 0.5; // -0.25 到 0.25 弧度
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.rotate(angle, x, y);
            g2d.drawString(String.valueOf(code.charAt(i)), x, y);
            g2d.dispose();
        }

        // 画干扰点
        for (int i = 0; i < 50; i++) {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            g.fillOval(x, y, 1, 1);
        }

        g.dispose();

        // 转换为Base64
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            throw new RuntimeException("生成验证码图片失败", e);
        }
    }

    /**
     * 验证验证码（不区分大小写）
     */
    public boolean verifyCaptcha(String inputCode, String actualCode) {
        if (inputCode == null || actualCode == null) {
            return false;
        }
        return inputCode.equalsIgnoreCase(actualCode);
    }
} 