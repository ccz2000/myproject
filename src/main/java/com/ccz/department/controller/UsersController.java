package com.ccz.department.controller;

import com.ccz.department.annotation.RequirePermission;
import com.ccz.department.common.Result;
import com.ccz.department.dto.*;
import com.ccz.department.service.UsersService;
import com.ccz.department.service.CaptchaService;
import com.ccz.department.utils.CaptchaUtils;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author : Chenchunze
 * @description : 用户控制器
 * @createDate : 2025/6/5 16:00
 */

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class UsersController {

    @Resource
    private UsersService usersService;
    
    @Resource
    private CaptchaService captchaService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<UsersDTO> register(@Validated @RequestBody RegisterDTO registerDTO) {
        UsersDTO userDTO = usersService.register(registerDTO);
        Result<UsersDTO> result = Result.success(userDTO);
        result.setMessage("注册成功！请查收邮箱验证邮件");
        return result;
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponseDTO> login(@Validated @RequestBody LoginDTO loginDTO) {
        LoginResponseDTO response = usersService.login(loginDTO);
        return Result.success(response);
    }

    /**
     * 邮箱验证
     */
    @PostMapping("/verify-email")
    public Result<Void> verifyEmail(@RequestParam String email, @RequestParam String code) {
        boolean success = usersService.activateUserByEmail(email, code);
        if (success) {
            Result<Void> result = Result.success();
            result.setMessage("邮箱验证成功！您现在可以登录了");
            return result;
        } else {
            return Result.error("邮箱验证失败");
        }
    }
    
    /**
     * 生成验证码
     */
    @GetMapping("/captcha")
    public Result<CaptchaUtils.CaptchaResult> generateCaptcha(@RequestParam String sessionId) {
        CaptchaUtils.CaptchaResult captcha = captchaService.generateCaptcha(sessionId);
        return Result.success(captcha);
    }
    
    /**
     * 忘记密码 - 发送重置邮件
     */
    @PostMapping("/forgot-password")
    public Result<Void> forgotPassword(@Validated @RequestBody ForgotPasswordDTO forgotPasswordDTO) {
        boolean success = usersService.sendResetPasswordEmail(forgotPasswordDTO.getEmail());
        if (success) {
            Result<Void> result = Result.success();
            result.setMessage("重置密码邮件已发送，请查收邮箱");
            return result;
        } else {
            return Result.error("发送重置密码邮件失败");
        }
    }
    
    /**
     * 重置密码
     */
    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@Validated @RequestBody ResetPasswordDTO resetPasswordDTO) {
        // 验证两次密码输入是否一致
        if (!resetPasswordDTO.getNewPassword().equals(resetPasswordDTO.getConfirmPassword())) {
            return Result.error("两次输入的密码不一致");
        }
        
        boolean success = usersService.resetPassword(
            resetPasswordDTO.getEmail(), 
            resetPasswordDTO.getVerificationCode(), 
            resetPasswordDTO.getNewPassword()
        );
        
        if (success) {
            Result<Void> result = Result.success();
            result.setMessage("密码重置成功，请使用新密码登录");
            return result;
        } else {
            return Result.error("密码重置失败");
        }
    }

    /**
     * 重新发送验证邮件
     */
    @PostMapping("/resend-verification")
    public Result<Void> resendVerification(@RequestParam String email) {
        boolean success = usersService.resendVerificationEmail(email);
        if (success) {
            Result<Void> result = Result.success();
            result.setMessage("验证邮件已重新发送，请查收");
            return result;
        } else {
            return Result.error("验证邮件发送失败");
        }
    }

    /**
     * 激活用户
     */
    @PutMapping("/activate/{userId}")
    public Result<Void> activateUser(@PathVariable Long userId) {
        boolean success = usersService.activateUser(userId);
        if (success) {
            return Result.success();
        } else {
            return Result.error("激活失败");
        }
    }

    /**
     * 检查用户名是否可用
     */
    @GetMapping("/check-username/{username}")
    public Result<Boolean> checkUsername(@PathVariable String username) {
        boolean available = usersService.findByUsername(username) == null;
        return Result.success(available);
    }

    /**
     * 检查邮箱是否可用
     */
    @GetMapping("/check-email/{email}")
    public Result<Boolean> checkEmail(@PathVariable String email) {
        boolean available = usersService.findByEmail(email) == null;
        return Result.success(available);
    }

    /**
     * 刷新token
     */
    @PostMapping("/refresh-token")
    public Result<LoginResponseDTO> refreshToken(@RequestParam String refreshToken) {
        LoginResponseDTO response = usersService.refreshToken(refreshToken);
        return Result.success(response);
    }

    /**
     * 获取所有用户列表（管理员可看全部，普通用户只能看基本信息）
     */
    @GetMapping("/users")
    @RequirePermission(viewUserList = true)
    public Result<List<UsersDTO>> getAllUsers(HttpServletRequest request) {
        List<UsersDTO> users = usersService.getAllUsers(request);
        return Result.success(users);
    }

    /**
     * 根据ID获取用户详情（只能查看自己的详情，管理员可查看任何人）
     */
    @GetMapping("/users/{id}")
    @RequirePermission(viewUserDetails = true)
    public Result<UsersDTO> getUserById(@PathVariable Long id, HttpServletRequest request) {
        UsersDTO user = usersService.getUserById(id, request);
        return Result.success(user);
    }

    /**
     * 更新用户信息（只能修改自己的信息，管理员可修改任何人）
     */
    @PutMapping("/users/{id}")
    @RequirePermission(modifyUser = true)
    public Result<UsersDTO> updateUser(@PathVariable Long id, @Validated @RequestBody UsersDTO userDTO, HttpServletRequest request) {
        UsersDTO updatedUser = usersService.updateUser(id, userDTO, request);
        return Result.success(updatedUser);
    }

    /**
     * 管理员授权用户权限
     */
    @PutMapping("/users/{id}/permission")
    @RequirePermission(manageUsers = true)
    public Result<Void> updateUserPermission(@PathVariable Long id, @RequestParam String permission) {
        usersService.updateUserPermission(id, permission);
        Result<Void> result = Result.success();
        result.setMessage("用户权限更新成功");
        return result;
    }

    /**
     * 管理员删除用户
     */
    @DeleteMapping("/users/{id}")
    @RequirePermission(manageUsers = true)
    public Result<Void> deleteUser(@PathVariable Long id) {
        usersService.deleteUser(id);
        Result<Void> result = Result.success();
        result.setMessage("用户删除成功");
        return result;
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/profile")
    public Result<UsersDTO> getCurrentUser(HttpServletRequest request) {
        UsersDTO user = usersService.getCurrentUser(request);
        return Result.success(user);
    }

    /**
     * 更新当前用户个人信息
     */
    @PutMapping("/profile")
    public Result<UsersDTO> updateProfile(@Validated @RequestBody UsersDTO userDTO, HttpServletRequest request) {
        UsersDTO updatedUser = usersService.updateProfile(userDTO, request);
        return Result.success(updatedUser);
    }
} 