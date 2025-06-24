package com.ccz.department.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author : Chenchunze
 * @description : 用户设置实体类
 * @createDate : 2025/6/6
 */
@Data
@TableName("user_settings")
public class UserSettings {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID，关联users表
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 主题设置：light-浅色，dark-深色
     */
    private String theme = "light";

    /**
     * 语言设置
     */
    private String language = "zh-CN";

    /**
     * 时区设置
     */
    private String timezone = "Asia/Shanghai";

    /**
     * 邮件通知：0-关闭，1-开启
     */
    private Integer emailNotification = 1;

    /**
     * 短信通知：0-关闭，1-开启
     */
    private Integer smsNotification = 1;

    /**
     * 隐私级别：0-完全公开，1-部分公开，2-完全私密
     */
    private Integer privacyLevel = 1;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
} 