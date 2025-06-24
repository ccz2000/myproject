package com.ccz.department.controller;

import com.ccz.department.common.Result;
import com.ccz.department.dto.ChangePasswordDTO;
import com.ccz.department.dto.UserProfileDTO;
import com.ccz.department.service.UserProfileService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @author : Chenchunze
 * @description : 用户资料控制器
 * @createDate : 2025/6/6 11:41
 */

@RestController
@RequestMapping("/profile")
@CrossOrigin(origins = "*")
public class UserProfileController {

    @Resource
    private UserProfileService userProfileService;

    /**
     * 获取用户资料
     */
    @GetMapping("/{userId}")
    public Result<UserProfileDTO> getProfile(@PathVariable Long userId) {
        UserProfileDTO profile = userProfileService.getProfileByUserId(userId);
        return Result.success(profile);
    }

    /**
     * 创建用户资料
     */
    @PostMapping
    public Result<UserProfileDTO> createProfile(@Validated @RequestBody UserProfileDTO userProfileDTO) {
        UserProfileDTO profile = userProfileService.createProfile(userProfileDTO);
        Result<UserProfileDTO> result = Result.success(profile);
        result.setMessage("用户资料创建成功");
        return result;
    }

    /**
     * 更新用户资料
     */
    @PutMapping
    public Result<UserProfileDTO> updateProfile(@Validated @RequestBody UserProfileDTO userProfileDTO) {
        UserProfileDTO profile = userProfileService.updateProfile(userProfileDTO);
        Result<UserProfileDTO> result = Result.success(profile);
        result.setMessage("用户资料更新成功");
        return result;
    }

    /**
     * 修改密码
     */
    @PostMapping("/{userId}/change-password")
    public Result<Void> changePassword(@PathVariable Long userId, 
                                      @Validated @RequestBody ChangePasswordDTO changePasswordDTO) {
        boolean success = userProfileService.changePassword(userId, changePasswordDTO);
        if (success) {
            Result<Void> result = Result.success();
            result.setMessage("密码修改成功");
            return result;
        } else {
            return Result.error("密码修改失败");
        }
    }

    /**
     * 上传头像
     */
    @PostMapping("/{userId}/upload-avatar")
    public Result<String> uploadAvatar(@PathVariable Long userId, 
                                      @RequestParam("file") MultipartFile file) {
        String avatarUrl = userProfileService.uploadAvatar(userId, file);
        Result<String> result = Result.success(avatarUrl);
        result.setMessage("头像上传成功");
        return result;
    }

    /**
     * 检查昵称是否可用
     */
    @GetMapping("/check-nickname")
    public Result<Boolean> checkNickname(@RequestParam String nickname, 
                                        @RequestParam Long userId) {
        boolean available = userProfileService.isNicknameAvailable(nickname, userId);
        return Result.success(available);
    }

    /**
     * 初始化用户资料
     */
    @PostMapping("/{userId}/initialize")
    public Result<Void> initializeProfile(@PathVariable Long userId) {
        boolean success = userProfileService.initializeProfile(userId);
        if (success) {
            Result<Void> result = Result.success();
            result.setMessage("用户资料初始化成功");
            return result;
        } else {
            return Result.error("用户资料初始化失败");
        }
    }
} 