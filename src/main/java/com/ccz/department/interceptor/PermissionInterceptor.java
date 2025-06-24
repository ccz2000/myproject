package com.ccz.department.interceptor;

import com.ccz.department.annotation.RequirePermission;
import com.ccz.department.entity.Users;
import com.ccz.department.enums.UserPermission;
import com.ccz.department.service.UsersService;
import com.ccz.department.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : Chenchunze
 * @description : 权限拦截器
 * @createDate : 2025/6/16
 */
@Slf4j
@Component
public class PermissionInterceptor implements HandlerInterceptor {

    @Resource
    private JwtUtils jwtUtils;

    @Resource
    private UsersService usersService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取token
        String token = getTokenFromRequest(request);
        if (token == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"code\":401,\"message\":\"未登录或token无效\"}");
            return false;
        }

        // 验证token
        if (!jwtUtils.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"code\":401,\"message\":\"token已过期\"}");
            return false;
        }

        // 获取用户信息
        Long userId = jwtUtils.getUserIdFromToken(token);
        Users user = usersService.getById(userId);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"code\":401,\"message\":\"用户不存在\"}");
            return false;
        }

        // 检查用户权限
        UserPermission permission = UserPermission.fromCode(user.getPermission());
        if (!permission.canLogin()) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"code\":403,\"message\":\"您没有访问权限，请联系管理员\"}");
            return false;
        }

        // 将用户信息存储到request中，供后续使用
        request.setAttribute("currentUser", user);
        request.setAttribute("userPermission", permission);

        // 检查方法级别的权限
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            RequirePermission requirePermission = handlerMethod.getMethodAnnotation(RequirePermission.class);
            
            if (requirePermission != null) {
                if (!checkMethodPermission(requirePermission, permission, user, request)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"code\":403,\"message\":\"权限不足，无法执行此操作\"}");
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 从请求中获取token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * 检查方法级别的权限
     */
    private boolean checkMethodPermission(RequirePermission requirePermission, UserPermission userPermission, Users user, HttpServletRequest request) {
        // 检查管理用户权限
        if (requirePermission.manageUsers() && !userPermission.canManageUsers()) {
            return false;
        }

        // 检查管理部门权限
        if (requirePermission.manageDepartments() && !userPermission.canManageDepartments()) {
            return false;
        }

        // 检查管理员工权限
        if (requirePermission.manageEmployees() && !userPermission.canManageEmployees()) {
            return false;
        }

        // 检查管理项目权限
        if (requirePermission.manageProjects() && !userPermission.canManageProjects()) {
            return false;
        }

        // 检查查看薪资权限
        if (requirePermission.viewSalary() && !userPermission.canViewSalary()) {
            return false;
        }

        // 检查查看用户列表权限
        if (requirePermission.viewUserList() && !userPermission.canViewUserList()) {
            return false;
        }

        // 检查查看用户详情权限
        if (requirePermission.viewUserDetails()) {
            String pathInfo = request.getRequestURI();
            if (pathInfo.contains("/users/")) {
                // 从路径中提取用户ID
                String[] pathParts = pathInfo.split("/");
                for (int i = 0; i < pathParts.length; i++) {
                    if ("users".equals(pathParts[i]) && i + 1 < pathParts.length) {
                        try {
                            Long targetUserId = Long.parseLong(pathParts[i + 1]);
                            if (!userPermission.canViewUserDetails(user.getId(), targetUserId)) {
                                return false;
                            }
                        } catch (NumberFormatException e) {
                            // 如果不是数字，可能是其他路径，继续检查
                        }
                        break;
                    }
                }
            }
        }

        // 检查修改用户权限
        if (requirePermission.modifyUser()) {
            String pathInfo = request.getRequestURI();
            if (pathInfo.contains("/users/")) {
                // 从路径中提取用户ID
                String[] pathParts = pathInfo.split("/");
                for (int i = 0; i < pathParts.length; i++) {
                    if ("users".equals(pathParts[i]) && i + 1 < pathParts.length) {
                        try {
                            Long targetUserId = Long.parseLong(pathParts[i + 1]);
                            if (!userPermission.canModifyUser(user.getId(), targetUserId)) {
                                return false;
                            }
                        } catch (NumberFormatException e) {
                            // 如果不是数字，可能是其他路径，继续检查
                        }
                        break;
                    }
                }
            }
        }

        return true;
    }
}