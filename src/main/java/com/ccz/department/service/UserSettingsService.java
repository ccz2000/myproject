package com.ccz.department.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ccz.department.dto.UserSettingsDTO;
import com.ccz.department.entity.UserSettings;

/**
 * @author : Chenchunze
 * @description : 用户设置服务接口
 * @createDate : 2025/6/6 11:41
 */
public interface UserSettingsService extends IService<UserSettings> {

    /**
     * 根据用户ID获取用户设置
     * @param userId 用户ID
     * @return 用户设置DTO
     */
    UserSettingsDTO getSettingsByUserId(Long userId);

    /**
     * 更新用户设置
     * @param userSettingsDTO 用户设置信息
     * @return 更新后的用户设置
     */
    UserSettingsDTO updateSettings(UserSettingsDTO userSettingsDTO);

    /**
     * 初始化用户设置（注册时调用）
     * @param userId 用户ID
     * @return 是否初始化成功
     */
    boolean initializeSettings(Long userId);

    /**
     * 重置用户设置为默认值
     * @param userId 用户ID
     * @return 重置后的用户设置
     */
    UserSettingsDTO resetToDefault(Long userId);
} 