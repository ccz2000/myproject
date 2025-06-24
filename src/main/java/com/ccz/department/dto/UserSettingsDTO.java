package com.ccz.department.dto;

import lombok.Data;

import javax.validation.constraints.*;

/**
 * @author : Chenchunze
 * @description : 用户设置传输对象
 * @createDate : 2025/6/6
 */
@Data
public class UserSettingsDTO {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @Positive(message = "用户ID必须为正数")
    private Long userId;

    /**
     * 主题设置：light-浅色，dark-深色
     */
    @Pattern(regexp = "^(light|dark)$", message = "主题设置只能是light或dark")
    private String theme;

    /**
     * 语言设置
     */
    @Pattern(regexp = "^(zh-CN|en-US|zh-TW|ja-JP)$", message = "语言设置不支持")
    private String language;

    /**
     * 时区设置
     */
    @NotBlank(message = "时区设置不能为空")
    @Size(max = 50, message = "时区名称长度不能超过50字符")
    private String timezone;

    /**
     * 邮件通知：0-关闭，1-开启
     */
    @NotNull(message = "邮件通知设置不能为空")
    @Min(value = 0, message = "邮件通知设置值无效")
    @Max(value = 1, message = "邮件通知设置值无效")
    private Integer emailNotification;

    /**
     * 短信通知：0-关闭，1-开启
     */
    @NotNull(message = "短信通知设置不能为空")
    @Min(value = 0, message = "短信通知设置值无效")
    @Max(value = 1, message = "短信通知设置值无效")
    private Integer smsNotification;

    /**
     * 隐私级别：0-完全公开，1-部分公开，2-完全私密
     */
    @NotNull(message = "隐私级别不能为空")
    @Min(value = 0, message = "隐私级别值无效")
    @Max(value = 2, message = "隐私级别值无效")
    private Integer privacyLevel;
} 