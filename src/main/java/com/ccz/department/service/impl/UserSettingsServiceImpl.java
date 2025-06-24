package com.ccz.department.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ccz.department.common.BusinessException;
import com.ccz.department.dto.UserSettingsDTO;
import com.ccz.department.entity.UserSettings;
import com.ccz.department.entity.Users;
import com.ccz.department.mapper.UserSettingsMapper;
import com.ccz.department.service.UserSettingsService;
import com.ccz.department.service.UsersService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author : Chenchunze
 * @description : 用户设置服务实现类
 * @createDate : 2025/6/6 11:41
 */
@Service
public class UserSettingsServiceImpl extends ServiceImpl<UserSettingsMapper, UserSettings> implements UserSettingsService {

    @Resource
    private UsersService usersService;

    @Override
    public UserSettingsDTO getSettingsByUserId(Long userId) {
        // 检查用户是否存在
        Users user = usersService.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 查询用户设置
        QueryWrapper<UserSettings> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        UserSettings userSettings = getOne(queryWrapper);

        UserSettingsDTO dto = new UserSettingsDTO();
        dto.setUserId(userId);

        if (userSettings != null) {
            BeanUtils.copyProperties(userSettings, dto);
        } else {
            // 如果没有设置，返回默认值
            dto.setTheme("light");
            dto.setLanguage("zh-CN");
            dto.setTimezone("Asia/Shanghai");
            dto.setEmailNotification(1);
            dto.setSmsNotification(1);
            dto.setPrivacyLevel(1);
        }

        return dto;
    }

    @Override
    public UserSettingsDTO updateSettings(UserSettingsDTO userSettingsDTO) {
        // 检查用户是否存在
        Users user = usersService.getById(userSettingsDTO.getUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 查询现有设置
        QueryWrapper<UserSettings> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userSettingsDTO.getUserId());
        UserSettings existingSettings = getOne(queryWrapper);

        if (existingSettings != null) {
            // 更新现有设置
            BeanUtils.copyProperties(userSettingsDTO, existingSettings, "id", "userId", "createdAt");
            existingSettings.setUpdatedAt(LocalDateTime.now());
            updateById(existingSettings);
        } else {
            // 创建新设置
            UserSettings userSettings = new UserSettings();
            BeanUtils.copyProperties(userSettingsDTO, userSettings);
            userSettings.setCreatedAt(LocalDateTime.now());
            userSettings.setUpdatedAt(LocalDateTime.now());
            save(userSettings);
        }

        // 返回更新后的设置
        return getSettingsByUserId(userSettingsDTO.getUserId());
    }

    @Override
    public boolean initializeSettings(Long userId) {
        // 检查是否已存在用户设置
        QueryWrapper<UserSettings> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        if (getOne(queryWrapper) != null) {
            return true; // 已存在，不需要初始化
        }

        // 创建默认用户设置
        UserSettings userSettings = new UserSettings();
        userSettings.setUserId(userId);
        userSettings.setTheme("light");
        userSettings.setLanguage("zh-CN");
        userSettings.setTimezone("Asia/Shanghai");
        userSettings.setEmailNotification(1);
        userSettings.setSmsNotification(1);
        userSettings.setPrivacyLevel(1);
        userSettings.setCreatedAt(LocalDateTime.now());
        userSettings.setUpdatedAt(LocalDateTime.now());

        return save(userSettings);
    }

    @Override
    public UserSettingsDTO resetToDefault(Long userId) {
        // 检查用户是否存在
        Users user = usersService.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 查询现有设置
        QueryWrapper<UserSettings> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        UserSettings existingSettings = getOne(queryWrapper);

        if (existingSettings != null) {
            // 重置为默认值
            existingSettings.setTheme("light");
            existingSettings.setLanguage("zh-CN");
            existingSettings.setTimezone("Asia/Shanghai");
            existingSettings.setEmailNotification(1);
            existingSettings.setSmsNotification(1);
            existingSettings.setPrivacyLevel(1);
            existingSettings.setUpdatedAt(LocalDateTime.now());
            updateById(existingSettings);
        } else {
            // 如果不存在，初始化设置
            initializeSettings(userId);
        }

        // 返回重置后的设置
        return getSettingsByUserId(userId);
    }
} 