package com.ccz.department.controller;

import com.ccz.department.common.Result;
import com.ccz.department.dto.UserSettingsDTO;
import com.ccz.department.service.UserSettingsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * @author : Chenchunze
 * @description : 用户设置控制器
 * @createDate : 2025/6/6 11:41
 */

@RestController
@RequestMapping("/settings")
@CrossOrigin(origins = "*")
@Validated
public class UserSettingsController {

    @Resource
    private UserSettingsService userSettingsService;

    /**
     * 获取用户设置
     */
    @GetMapping("/{userId}")
    public Result<UserSettingsDTO> getSettings(
            @PathVariable @Positive(message = "用户ID必须为正数") Long userId) {
        UserSettingsDTO settings = userSettingsService.getSettingsByUserId(userId);
        return Result.success(settings);
    }

    /**
     * 更新用户设置
     */
    @PutMapping
    public Result<UserSettingsDTO> updateSettings(
            @Valid @RequestBody UserSettingsDTO userSettingsDTO,
            BindingResult bindingResult) {
        
        // 检查参数验证错误
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            StringBuilder errorMsg = new StringBuilder();
            for (FieldError error : fieldErrors) {
                errorMsg.append(error.getDefaultMessage()).append("; ");
            }
            return Result.error(errorMsg.toString());
        }
        
        UserSettingsDTO settings = userSettingsService.updateSettings(userSettingsDTO);
        Result<UserSettingsDTO> result = Result.success(settings);
        result.setMessage("用户设置更新成功");
        return result;
    }

    /**
     * 初始化用户设置
     */
    @PostMapping("/{userId}/initialize")
    public Result<Void> initializeSettings(
            @PathVariable @Positive(message = "用户ID必须为正数") Long userId) {
        boolean success = userSettingsService.initializeSettings(userId);
        if (success) {
            Result<Void> result = Result.success();
            result.setMessage("用户设置初始化成功");
            return result;
        } else {
            return Result.error("用户设置初始化失败");
        }
    }

    /**
     * 重置用户设置为默认值
     */
    @PostMapping("/{userId}/reset")
    public Result<UserSettingsDTO> resetToDefault(
            @PathVariable @Positive(message = "用户ID必须为正数") Long userId) {
        UserSettingsDTO settings = userSettingsService.resetToDefault(userId);
        Result<UserSettingsDTO> result = Result.success(settings);
        result.setMessage("用户设置已重置为默认值");
        return result;
    }
} 