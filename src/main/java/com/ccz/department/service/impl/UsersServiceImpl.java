package com.ccz.department.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ccz.department.common.BusinessException;
import com.ccz.department.dto.LoginDTO;
import com.ccz.department.dto.LoginResponseDTO;
import com.ccz.department.dto.RegisterDTO;
import com.ccz.department.dto.UsersDTO;
import com.ccz.department.entity.Users;
import com.ccz.department.enums.UserPermission;
import com.ccz.department.mapper.UsersMapper;
import com.ccz.department.service.EmailService;
import com.ccz.department.service.UsersService;
import com.ccz.department.service.CaptchaService;
import com.ccz.department.utils.JwtUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {

        @Resource
    private EmailService emailService;

    @Resource
    private JwtUtils jwtUtils;
    
    @Resource
    private CaptchaService captchaService;

    @Override
    public UsersDTO register(RegisterDTO registerDTO) {
        // 验证确认密码
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }

        // 检查用户名是否已存在
        if (findByUsername(registerDTO.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (findByEmail(registerDTO.getEmail()) != null) {
            throw new BusinessException("邮箱已被注册");
        }

        // 创建用户实体
        Users user = new Users();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(encodePassword(registerDTO.getPassword()));
        user.setEmail(registerDTO.getEmail());
        user.setStatus(0); // 默认未激活状态，需要邮箱验证
        user.setPermission("2"); // 默认普通用户权限
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // 保存用户
        save(user);

        // 发送验证邮件
        boolean emailSent = emailService.sendRegisterVerificationEmail(user.getEmail(), user.getId());
        if (!emailSent) {
            throw new BusinessException("注册成功，但验证邮件发送失败，请联系管理员");
        }

        // 转换为DTO返回
        UsersDTO usersDTO = new UsersDTO();
        BeanUtils.copyProperties(user, usersDTO);
        return usersDTO;
    }

    @Override
    public LoginResponseDTO login(LoginDTO loginDTO) {
        // 先验证验证码
        if (!captchaService.verifyCaptcha(loginDTO.getSessionId(), loginDTO.getCaptcha())) {
            throw new BusinessException("验证码错误或已过期");
        }
        
        // 尝试通过用户名查找用户
        Users user = findByUsername(loginDTO.getLoginField());
        
        // 如果用户名没找到，尝试通过邮箱查找
        if (user == null) {
            user = findByEmail(loginDTO.getLoginField());
        }

        // 用户不存在
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 验证密码
        if (!verifyPassword(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException("账户尚未激活，请查收邮箱验证邮件");
        }

        // 检查用户权限
        UserPermission permission = UserPermission.fromCode(user.getPermission());
        if (!permission.canLogin()) {
            throw new BusinessException("您没有登录权限，请联系管理员");
        }

        // 使用JWT生成token
        String accessToken = jwtUtils.generateAccessToken(user);
        String refreshToken = jwtUtils.generateRefreshToken(user);

        // 构造返回结果
        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(jwtUtils.getTokenRemainingTimeInSeconds(accessToken));
        
        UsersDTO userInfo = new UsersDTO();
        BeanUtils.copyProperties(user, userInfo);
        response.setUserInfo(userInfo);
        response.setMessage("登录成功");

        return response;
    }

    @Override
    public Users findByUsername(String username) {
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return getOne(queryWrapper);
    }

    @Override
    public Users findByEmail(String email) {
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        return getOne(queryWrapper);
    }

    @Override
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return encodedPassword.equals(encodePassword(rawPassword));
    }

    @Override
    public boolean activateUser(Long userId) {
        Users user = getById(userId);
        if (user != null) {
            user.setStatus(1);
            user.setUpdatedAt(LocalDateTime.now());
            return updateById(user);
        }
        return false;
    }

    /**
     * 通过邮箱验证激活用户
     * @param email 邮箱
     * @param verificationCode 验证码
     * @return 是否激活成功
     */
    public boolean activateUserByEmail(String email, String verificationCode) {
        // 验证邮箱验证码
        boolean isValid = emailService.verifyEmailCode(email, verificationCode, "REGISTER");
        if (!isValid) {
            throw new BusinessException("验证码无效或已过期");
        }

        // 激活用户
        Users user = findByEmail(email);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (user.getStatus() == 1) {
            throw new BusinessException("用户已激活，请直接登录");
        }

        user.setStatus(1);
        user.setUpdatedAt(LocalDateTime.now());
        return updateById(user);
    }

    /**
     * 重新发送验证邮件
     * @param email 邮箱
     * @return 是否发送成功
     */
    public boolean resendVerificationEmail(String email) {
        Users user = findByEmail(email);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (user.getStatus() == 1) {
            throw new BusinessException("用户已激活，无需重新验证");
        }

        return emailService.sendRegisterVerificationEmail(email, user.getId());
    }

    /**
     * 密码加密（使用MD5）
     */
    private String encodePassword(String rawPassword) {
        return DigestUtils.md5DigestAsHex((rawPassword + "dept_salt").getBytes());
    }

    /**
     * 刷新token
     */
    public LoginResponseDTO refreshToken(String refreshToken) {
        // 验证refresh token
        if (!jwtUtils.validateToken(refreshToken)) {
            throw new BusinessException("刷新token无效或已过期");
        }

        // 从refresh token中获取用户信息
        Long userId = jwtUtils.getUserIdFromToken(refreshToken);
        Users user = getById(userId);
        
        if (user == null || user.getStatus() != 1) {
            throw new BusinessException("用户不存在或已被禁用");
        }

        // 生成新的access token
        String newAccessToken = jwtUtils.generateAccessToken(user);

        // 构造返回结果
        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(newAccessToken);
        response.setRefreshToken(refreshToken); // 复用原refresh token
        response.setExpiresIn(jwtUtils.getTokenRemainingTimeInSeconds(newAccessToken));
        
        UsersDTO userInfo = new UsersDTO();
        BeanUtils.copyProperties(user, userInfo);
        response.setUserInfo(userInfo);
        response.setMessage("Token刷新成功");

        return response;
    }

    /**
     * 验证token并获取用户信息
     */
    public Users validateTokenAndGetUser(String token) {
        if (!jwtUtils.validateToken(token)) {
            return null;
        }

        Long userId = jwtUtils.getUserIdFromToken(token);
        return getById(userId);
    }

    @Override
    public List<UsersDTO> getAllUsers(HttpServletRequest request) {
        Users currentUser = (Users) request.getAttribute("currentUser");
        UserPermission permission = UserPermission.fromCode(currentUser.getPermission());
        
        List<Users> users = list();
        
        if (permission == UserPermission.ADMIN) {
            // 管理员可以看到所有用户的完整信息
            return users.stream().map(this::convertToDTO).collect(Collectors.toList());
        } else {
            // 普通用户只能看到基本信息，不能看到敏感信息
            return users.stream().map(user -> {
                UsersDTO dto = new UsersDTO();
                dto.setId(user.getId());
                dto.setUsername(user.getUsername());
                dto.setEmail(user.getEmail());
                dto.setStatus(user.getStatus());
                dto.setCreatedAt(user.getCreatedAt());
                // 不返回密码、权限等敏感信息
                return dto;
            }).collect(Collectors.toList());
        }
    }

    @Override
    public UsersDTO getUserById(Long id, HttpServletRequest request) {
        Users currentUser = (Users) request.getAttribute("currentUser");
        UserPermission permission = UserPermission.fromCode(currentUser.getPermission());
        
        Users user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 检查权限：只能查看自己的详情，管理员可查看任何人
        if (!permission.canViewUserDetails(currentUser.getId(), id)) {
            throw new BusinessException("权限不足，无法查看该用户详情");
        }
        
        return convertToDTO(user);
    }

    @Override
    public UsersDTO updateUser(Long id, UsersDTO userDTO, HttpServletRequest request) {
        Users currentUser = (Users) request.getAttribute("currentUser");
        UserPermission permission = UserPermission.fromCode(currentUser.getPermission());
        
        Users user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 检查权限：只能修改自己的信息，管理员可修改任何人
        if (!permission.canModifyUser(currentUser.getId(), id)) {
            throw new BusinessException("权限不足，无法修改该用户信息");
        }
        
        // 更新用户信息
        if (StringUtils.hasText(userDTO.getEmail()) && !userDTO.getEmail().equals(user.getEmail())) {
            // 检查邮箱是否已被使用
            if (findByEmail(userDTO.getEmail()) != null) {
                throw new BusinessException("邮箱已被使用");
            }
            user.setEmail(userDTO.getEmail());
        }
        
        // 普通用户不能修改权限，只有管理员可以
        if (permission == UserPermission.ADMIN && StringUtils.hasText(userDTO.getPermission())) {
            user.setPermission(userDTO.getPermission());
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        updateById(user);
        
        return convertToDTO(user);
    }

    @Override
    public void updateUserPermission(Long id, String permission) {
        Users user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 验证权限值
        try {
            UserPermission.fromCode(permission);
        } catch (Exception e) {
            throw new BusinessException("无效的权限值");
        }
        
        user.setPermission(permission);
        user.setUpdatedAt(LocalDateTime.now());
        updateById(user);
    }

    @Override
    public void deleteUser(Long id) {
        Users user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        removeById(id);
    }

    @Override
    public UsersDTO getCurrentUser(HttpServletRequest request) {
        Users currentUser = (Users) request.getAttribute("currentUser");
        return convertToDTO(currentUser);
    }

    @Override
    public UsersDTO updateProfile(UsersDTO userDTO, HttpServletRequest request) {
        Users currentUser = (Users) request.getAttribute("currentUser");
        
        // 更新个人信息
        if (StringUtils.hasText(userDTO.getEmail()) && !userDTO.getEmail().equals(currentUser.getEmail())) {
            // 检查邮箱是否已被使用
            if (findByEmail(userDTO.getEmail()) != null) {
                throw new BusinessException("邮箱已被使用");
            }
            currentUser.setEmail(userDTO.getEmail());
        }
        
        currentUser.setUpdatedAt(LocalDateTime.now());
        updateById(currentUser);
        
        return convertToDTO(currentUser);
    }

    /**
     * 转换为DTO
     */
    private UsersDTO convertToDTO(Users user) {
        UsersDTO dto = new UsersDTO();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }
    
    @Override
    public boolean sendResetPasswordEmail(String email) {
        // 检查用户是否存在
        Users user = findByEmail(email);
        if (user == null) {
            throw new BusinessException("该邮箱未注册");
        }
        
        if (user.getStatus() != 1) {
            throw new BusinessException("账户未激活，请先激活账户");
        }
        
        // 发送重置密码邮件
        return emailService.sendResetPasswordEmail(email, user.getId());
    }
    
    @Override
    public boolean resetPassword(String email, String verificationCode, String newPassword) {
        // 验证邮箱验证码
        boolean isValid = emailService.verifyEmailCode(email, verificationCode, "RESET_PASSWORD");
        if (!isValid) {
            throw new BusinessException("验证码无效或已过期");
        }
        
        // 查找用户
        Users user = findByEmail(email);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 更新密码
        user.setPassword(encodePassword(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        
        return updateById(user);
    }
} 