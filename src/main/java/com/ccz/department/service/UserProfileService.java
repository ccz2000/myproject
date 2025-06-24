package com.ccz.department.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ccz.department.dto.ChangePasswordDTO;
import com.ccz.department.dto.UserProfileDTO;
import com.ccz.department.entity.UserProfile;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author : Chenchunze
 * @description : 用户资料服务接口
 * @createDate : 2025/6/6 11:41
 */
public interface UserProfileService extends IService<UserProfile> {

    /**
     * 根据用户ID获取用户资料（包含用户基本信息）
     * @param userId 用户ID
     * @return 用户资料DTO
     */
    UserProfileDTO getProfileByUserId(Long userId);

    /**
     * 创建用户资料
     * @param userProfileDTO 用户资料信息
     * @return 创建的用户资料
     */
    UserProfileDTO createProfile(UserProfileDTO userProfileDTO);

    /**
     * 更新用户资料
     * @param userProfileDTO 用户资料信息
     * @return 更新后的用户资料
     */
    UserProfileDTO updateProfile(UserProfileDTO userProfileDTO);

    /**
     * 修改密码
     * @param userId 用户ID
     * @param changePasswordDTO 修改密码信息
     * @return 是否修改成功
     */
    boolean changePassword(Long userId, ChangePasswordDTO changePasswordDTO);

    /**
     * 上传头像
     * @param userId 用户ID
     * @param file 头像文件
     * @return 头像URL
     */
    String uploadAvatar(Long userId, MultipartFile file);

    /**
     * 检查昵称是否可用
     * @param nickname 昵称
     * @param userId 当前用户ID
     * @return 是否可用
     */
    boolean isNicknameAvailable(String nickname, Long userId);

    /**
     * 初始化用户资料（注册时调用）
     * @param userId 用户ID
     * @return 是否初始化成功
     */
    boolean initializeProfile(Long userId);
} 