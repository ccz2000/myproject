package com.ccz.department.enums;

import lombok.Getter;

@Getter
public enum UserPermission {
    
    ADMIN("1", "管理员", "拥有所有权限"),
    USER("2", "普通用户", "普通用户权限"),
    NO_LOGIN("3", "无登录权限", "禁止登录系统");
    
    private final String code;
    private final String name;
    private final String description;
    
    UserPermission(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }
    
    public static UserPermission fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return USER;
        }
        for (UserPermission permission : values()) {
            if (permission.getCode().equals(code)) {
                return permission;
            }
        }
        return USER;
    }
    
    public boolean isAdmin() {
        return this == ADMIN;
    }
    
    public boolean canLogin() {
        return this != NO_LOGIN;
    }
    
    public boolean canManageUsers() {
        return this == ADMIN;
    }
    
    public boolean canViewSalary() {
        return this == ADMIN;
    }
    
    public boolean canManageDepartments() {
        return this == ADMIN;
    }
    
    public boolean canManageEmployees() {
        return this == ADMIN;
    }
    
    public boolean canManageProjects() {
        return this == ADMIN;
    }
    
    /**
     * 是否可以修改指定用户的信息（只能修改自己的）
     */
    public boolean canModifyUser(Long currentUserId, Long targetUserId) {
        if (this == ADMIN) {
            return true; // 管理员可以修改任何用户
        }
        if (this == USER) {
            return currentUserId != null && currentUserId.equals(targetUserId); // 普通用户只能修改自己
        }
        return false; // 无权限用户不能修改任何信息
    }
    
    /**
     * 是否可以查看指定用户的详细信息
     */
    public boolean canViewUserDetails(Long currentUserId, Long targetUserId) {
        if (this == ADMIN) {
            return true; // 管理员可以查看任何用户详情
        }
        if (this == USER) {
            return currentUserId != null && currentUserId.equals(targetUserId); // 普通用户只能查看自己的详情
        }
        return false;
    }
    
    /**
     * 是否可以查看用户列表（普通用户只能看基本信息，不能看详情）
     */
    public boolean canViewUserList() {
        return this == ADMIN || this == USER; // 管理员和普通用户都可以查看列表，但权限不同
    }
}
