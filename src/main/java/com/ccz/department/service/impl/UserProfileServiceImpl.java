package com.ccz.department.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ccz.department.common.BusinessException;
import com.ccz.department.config.FileConfig;
import com.ccz.department.dto.ChangePasswordDTO;
import com.ccz.department.dto.UserProfileDTO;
import com.ccz.department.entity.UserProfile;
import com.ccz.department.entity.Users;
import com.ccz.department.mapper.UserProfileMapper;
import com.ccz.department.service.UserProfileService;
import com.ccz.department.service.UsersService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author : Chenchunze
 * @description : 用户资料服务实现类
 * @createDate : 2025/6/6 11:41
 */
@Service
public class UserProfileServiceImpl extends ServiceImpl<UserProfileMapper, UserProfile> implements UserProfileService {

    @Resource
    private UsersService usersService;
    
    @Resource
    private FileConfig fileConfig;

    @Override
    public UserProfileDTO getProfileByUserId(Long userId) {
        // 查询用户资料
        QueryWrapper<UserProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        UserProfile userProfile = getOne(queryWrapper);

        // 查询用户基本信息
        Users user = usersService.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 转换为DTO
        UserProfileDTO dto = new UserProfileDTO();
        dto.setUserId(userId);
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());

        if (userProfile != null) {
            BeanUtils.copyProperties(userProfile, dto, "userId");
        }

        return dto;
    }

    @Override
    public UserProfileDTO createProfile(UserProfileDTO userProfileDTO) {
        // 检查用户是否存在
        Users user = usersService.getById(userProfileDTO.getUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 检查是否已有资料
        QueryWrapper<UserProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userProfileDTO.getUserId());
        if (getOne(queryWrapper) != null) {
            throw new BusinessException("用户资料已存在，请使用更新功能");
        }

        // 检查昵称是否可用
        if (StringUtils.hasText(userProfileDTO.getNickname()) &&
            !isNicknameAvailable(userProfileDTO.getNickname(), userProfileDTO.getUserId())) {
            throw new BusinessException("昵称已被使用");
        }

        // 创建用户资料
        UserProfile userProfile = new UserProfile();
        BeanUtils.copyProperties(userProfileDTO, userProfile);
        userProfile.setCreatedAt(LocalDateTime.now());
        userProfile.setUpdatedAt(LocalDateTime.now());

        save(userProfile);

        // 返回完整信息
        return getProfileByUserId(userProfileDTO.getUserId());
    }

    @Override
    public UserProfileDTO updateProfile(UserProfileDTO userProfileDTO) {
        // 检查用户是否存在
        Users user = usersService.getById(userProfileDTO.getUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 查询现有资料
        QueryWrapper<UserProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userProfileDTO.getUserId());
        UserProfile existingProfile = getOne(queryWrapper);

        // 检查昵称是否可用
        if (StringUtils.hasText(userProfileDTO.getNickname()) &&
            !isNicknameAvailable(userProfileDTO.getNickname(), userProfileDTO.getUserId())) {
            throw new BusinessException("昵称已被使用");
        }

        if (existingProfile != null) {
            // 更新现有资料
            BeanUtils.copyProperties(userProfileDTO, existingProfile, "id", "userId", "createdAt");
            existingProfile.setUpdatedAt(LocalDateTime.now());
            updateById(existingProfile);
        } else {
            // 创建新资料
            return createProfile(userProfileDTO);
        }

        // 返回完整信息
        return getProfileByUserId(userProfileDTO.getUserId());
    }

    @Override
    public boolean changePassword(Long userId, ChangePasswordDTO changePasswordDTO) {
        // 检查新密码和确认密码是否一致
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            throw new BusinessException("新密码和确认密码不一致");
        }

        // 查询用户
        Users user = usersService.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证当前密码
        if (!usersService.verifyPassword(changePasswordDTO.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException("当前密码不正确");
        }

        // 更新密码
        String newEncodedPassword = encodePassword(changePasswordDTO.getNewPassword());
        user.setPassword(newEncodedPassword);
        user.setUpdatedAt(LocalDateTime.now());

        return usersService.updateById(user);
    }

    @Override
    public String uploadAvatar(Long userId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("只能上传图片文件");
        }

        // 检查文件大小（限制为5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BusinessException("文件大小不能超过5MB");
        }

        try {
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? 
                originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String filename = "avatar_" + userId + "_" + UUID.randomUUID().toString() + extension;

            // 使用项目内的static目录
            String resourcePath = "src/main/resources/static/avatars";
            Path uploadPath = Paths.get(resourcePath);
            
            // 确保目录存在
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 保存文件
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);

            // 更新用户资料中的头像信息 - 存储static路径
            String avatarUrl = "/avatars/" + filename;
            QueryWrapper<UserProfile> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            UserProfile userProfile = getOne(queryWrapper);

            if (userProfile != null) {
                userProfile.setAvatar(avatarUrl);
                userProfile.setUpdatedAt(LocalDateTime.now());
                updateById(userProfile);
            } else {
                // 如果用户资料不存在，创建一个
                userProfile = new UserProfile();
                userProfile.setUserId(userId);
                userProfile.setAvatar(avatarUrl);
                userProfile.setCreatedAt(LocalDateTime.now());
                userProfile.setUpdatedAt(LocalDateTime.now());
                save(userProfile);
            }

            return avatarUrl;

        } catch (IOException e) {
            throw new BusinessException("文件上传失败：" + e.getMessage());
        }
    }

    @Override
    public boolean isNicknameAvailable(String nickname, Long userId) {
        if (!StringUtils.hasText(nickname)) {
            return true;
        }
        return baseMapper.checkNicknameExists(nickname, userId) == 0;
    }

    @Override
    public boolean initializeProfile(Long userId) {
        // 检查是否已存在用户资料
        QueryWrapper<UserProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        if (getOne(queryWrapper) != null) {
            return true; // 已存在，不需要初始化
        }

        // 创建默认用户资料
        UserProfile userProfile = new UserProfile();
        userProfile.setUserId(userId);
        userProfile.setCreatedAt(LocalDateTime.now());
        userProfile.setUpdatedAt(LocalDateTime.now());

        return save(userProfile);
    }

    /**
     * 密码加密（使用MD5，与UsersService保持一致）
     */
    private String encodePassword(String rawPassword) {
        return DigestUtils.md5DigestAsHex((rawPassword + "dept_salt").getBytes());
    }
} 