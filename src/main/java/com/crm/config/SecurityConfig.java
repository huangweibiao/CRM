package com.crm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 安全配置类
 * 配置系统的认证和授权规则
 * 简化版：已移除OAuth2，保留表单登录和基础安全功能
 *
 * @author CRM Team
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 安全过滤器链配置
     * 定义哪些URL需要认证，哪些可以匿名访问
     *
     * @param http HttpSecurity对象
     * @return 配置好的SecurityFilterChain
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF（前后端分离时通常需要禁用）
            .csrf(AbstractHttpConfigurer::disable)
            // 授权请求配置
            .authorizeHttpRequests(auth -> auth
                // 静态资源允许匿名访问：首页、登录页、错误页、静态文件
                .requestMatchers("/", "/login", "/error", "/static/**", "/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                // API接口允许访问（临时开放，实际使用时添加认证）
                .requestMatchers("/api/**").permitAll()
                // 所有其他页面允许访问（临时开放，实际使用时需要添加认证）
                .anyRequest().permitAll()
            )
            // 表单登录配置
            .formLogin(form -> form
                .loginPage("/login")          // 登录页面URL
                .defaultSuccessUrl("/dashboard")  // 登录成功后的默认跳转页面
                .permitAll()                  // 允许所有人访问登录页面
            )
            // 登出配置
            .logout(logout -> logout
                .logoutSuccessUrl("/login")   // 登出成功后的跳转页面
                .invalidateHttpSession(true)  // 使Session失效
                .clearAuthentication(true)    // 清除认证信息
                .deleteCookies("JSESSIONID")  // 删除Session Cookie
                .permitAll()                  // 允许所有人访问登出URL
            );

        return http.build();
    }

    /**
     * 密码加密器配置
     * 使用BCrypt算法对用户密码进行加密存储
     *
     * @return PasswordEncoder实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
