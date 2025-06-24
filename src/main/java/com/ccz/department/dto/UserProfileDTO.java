package com.ccz.department.dto;

import lombok.Data;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * @author : Chenchunze
 * @description : 用户资料传输对象
 * @createDate : 2025/6/6
 */
@Data
public class UserProfileDTO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名（来自users表，只读）
     */
    private String username;

    /**
     * 邮箱（来自users表，只读）
     */
    private String email;

    /**
     * 真实姓名
     */
    @Size(max = 50, message = "真实姓名长度不能超过50字符")
    private String realName;

    /**
     * 昵称
     */
    @Size(max = 50, message = "昵称长度不能超过50字符")
    private String nickname;

    /**
     * 头像URL
     */
    @Size(max = 500, message = "头像URL长度不能超过500字符")
    private String avatar;

    /**
     * 手机号码
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 个人简介
     */
    @Size(max = 1000, message = "个人简介长度不能超过1000字符")
    private String bio;

    /**
     * 地址
     */
    @Size(max = 200, message = "地址长度不能超过200字符")
    private String address;

    /**
     * 公司
     */
    @Size(max = 100, message = "公司名称长度不能超过100字符")
    private String company;

    /**
     * 职位
     */
    @Size(max = 100, message = "职位长度不能超过100字符")
    private String position;

    /**
     * 个人网站
     */
    @Size(max = 200, message = "个人网站URL长度不能超过200字符")
    private String website;

    /**
     * QQ号码
     */
    @Pattern(regexp = "^[1-9]\\d{4,10}$", message = "QQ号格式不正确")
    private String qq;

    /**
     * 微信号
     */
    @Size(max = 50, message = "微信号长度不能超过50字符")
    private String wechat;

    /**
     * 微博
     */
    @Size(max = 100, message = "微博账号长度不能超过100字符")
    private String weibo;


} 