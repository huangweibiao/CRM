package com.crm.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * Security Utility Class
 * Helper methods for security-related operations
 *
 * @author CRM Team
 */
@Slf4j
public class SecurityUtils {

    /**
     * Get the currently authenticated user
     *
     * @return Authentication object or null if not authenticated
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Get the current OAuth2 user
     *
     * @return OAuth2User or null if not authenticated
     */
    public static OAuth2User getCurrentOAuth2User() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof OAuth2User) {
            return (OAuth2User) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * Get the current user's username
     *
     * @return username or null if not authenticated
     */
    public static String getCurrentUsername() {
        OAuth2User oauth2User = getCurrentOAuth2User();
        if (oauth2User != null) {
            return oauth2User.getAttribute("email");
        }
        return null;
    }

    /**
     * Get the current user's full name
     *
     * @return full name or null if not authenticated
     */
    public static String getCurrentUserFullName() {
        OAuth2User oauth2User = getCurrentOAuth2User();
        if (oauth2User != null) {
            return oauth2User.getAttribute("name");
        }
        return null;
    }

    /**
     * Check if the current user is authenticated
     *
     * @return true if authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal());
    }
}
