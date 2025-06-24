package com.ccz.department.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ccz.department.dto.LoginDTO;
import com.ccz.department.dto.LoginResponseDTO;
import com.ccz.department.dto.RegisterDTO;
import com.ccz.department.dto.UsersDTO;
import com.ccz.department.entity.Users;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author : Chenchunze
 * @description : 用户服务接口
 * @createDate : 2025/6/5 15:08
 */
public interface UsersService extends IService<Users> {

    /**
     * 用户注册
     * @param registerDTO 注册信息
     * @return 用户信息
     */
    UsersDTO register(RegisterDTO registerDTO);

    /**
     * 用户登录
     * @param loginDTO 登录信息
     * @return 登录响应（包含token和用户信息）
     */
    LoginResponseDTO login(LoginDTO loginDTO);

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户实体
     */
    Users findByUsername(String username);

    /**
     * 根据邮箱查找用户
     * @param email 邮箱
     * @return 用户实体
     */
    Users findByEmail(String email);

    /**
     * 验证密码
     * @param rawPassword 原始密码
     * @param encodedPassword 加密密码
     * @return 是否匹配
     */
    boolean verifyPassword(String rawPassword, String encodedPassword);

    /**
     * 激活用户
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean activateUser(Long userId);

    /**
     * 通过邮箱验证激活用户
     * @param email 邮箱
     * @param verificationCode 验证码
     * @return 是否激活成功
     */
    boolean activateUserByEmail(String email, String verificationCode);

    /**
     * 重新发送验证邮件
     * @param email 邮箱
     * @return 是否发送成功
     */
    boolean resendVerificationEmail(String email);
    
    /**
     * 发送重置密码邮件
     * @param email 邮箱
     * @return 是否发送成功
     */
    boolean sendResetPasswordEmail(String email);
    
    /**
     * 重置密码
     * @param email 邮箱
     * @param verificationCode 验证码
     * @param newPassword 新密码
     * @return 是否重置成功
     */
    boolean resetPassword(String email, String verificationCode, String newPassword);

    /**
     * 刷新token
     * @param refreshToken 刷新token
     * @return 新的登录响应
     */
    LoginResponseDTO refreshToken(String refreshToken);

    /**
     * 验证token并获取用户信息
     * @param token JWT token
     * @return 用户实体
     */
    Users validateTokenAndGetUser(String token);

    /**
     * 获取所有用户列表（管理员可看全部，普通用户只能看基本信息）
     * @param request HTTP请求
     * @return 用户列表
     */
    List<UsersDTO> getAllUsers(HttpServletRequest request);

    /**
     * 根据ID获取用户详情（只能查看自己的详情，管理员可查看任何人）
     * @param id 用户ID
     * @param request HTTP请求
     * @return 用户信息
     */
    UsersDTO getUserById(Long id, HttpServletRequest request);

    /**
     * 更新用户信息（只能修改自己的信息，管理员可修改任何人）
     * @param id 用户ID
     * @param userDTO 用户信息
     * @param request HTTP请求
     * @return 更新后的用户信息
     */
    UsersDTO updateUser(Long id, UsersDTO userDTO, HttpServletRequest request);

    /**
     * 管理员授权用户权限
     * @param id 用户ID
     * @param permission 权限值
     */
    void updateUserPermission(Long id, String permission);

    /**
     * 管理员删除用户
     * @param id 用户ID
     */
    void deleteUser(Long id);

    /**
     * 获取当前用户信息
     * @param request HTTP请求
     * @return 当前用户信息
     */
    UsersDTO getCurrentUser(HttpServletRequest request);

    /**
     * 更新当前用户个人信息
     * @param userDTO 用户信息
     * @param request HTTP请求
     * @return 更新后的用户信息
     */
    UsersDTO updateProfile(UsersDTO userDTO, HttpServletRequest request);
}
