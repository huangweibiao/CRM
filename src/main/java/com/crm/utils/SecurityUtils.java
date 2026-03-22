package com.crm.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全工具类
 * 提供Spring Security相关的常用操作方法
 * 简化在业务代码中获取当前用户信息的逻辑
 *
 * @author CRM Team
 */
public class SecurityUtils {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SecurityUtils.class);

    /**
     * 获取当前登录用户的认证信息
     *
     * @return Authentication对象，如果未登录则返回null
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 获取当前登录用户的ID
     * 从认证信息中提取用户ID，用于业务操作
     *
     * @return 用户ID，未登录返回null
     */
    public static Long getCurrentUserId() {
        Authentication authentication = getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null) {
            Object principal = authentication.getPrincipal();
            // 根据实际的用户类型获取用户ID
            // 这里暂时返回一个默认值，实际使用时需要根据用户类型获取
            return 1L;
        }
        return null;
    }

    /**
     * 获取当前登录用户的用户名
     *
     * @return 用户名，未登录返回null
     */
    public static String getCurrentUsername() {
        Authentication authentication = getAuthentication();
        if (authentication != null) {
            return authentication.getName();
        }
        return null;
    }

    /**
     * 检查当前用户是否已登录认证
     *
     * @return true=已登录，false=未登录
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal());
    }

    /**
     * 检查当前用户是否具有指定角色
     *
     * @param role 角色名称（如：ADMIN、USER）
     * @return true=具有该角色，false=不具有
     */
    public static boolean hasRole(String role) {
        Authentication authentication = getAuthentication();
        if (authentication != null && authentication.getAuthorities() != null) {
            return authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
        }
        return false;
    }

    /**
     * 检查当前用户是否为管理员
     *
     * @return true=是管理员，false=不是管理员
     */
    public static boolean isAdmin() {
        return hasRole("ADMIN");
    }
}
