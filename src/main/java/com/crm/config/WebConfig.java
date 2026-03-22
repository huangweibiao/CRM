package com.crm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web Configuration
 * Handles view controllers and static resource mappings
 *
 * @author CRM Team
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Login and Home
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/dashboard").setViewName("dashboard");

        // Customer Management
        registry.addViewController("/customers").setViewName("customers");
        registry.addViewController("/customers/public").setViewName("customers");

        // Business/Oppotunity Management
        registry.addViewController("/businesses").setViewName("businesses");

        // Contract Management
        registry.addViewController("/contracts").setViewName("contracts");

        // Statistics
        registry.addViewController("/statistics").setViewName("statistics");
    }
}
