package com.crm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Home Controller
 * Handles home page requests
 *
 * @author CRM Team
 */
@Slf4j
@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        log.info("Home page requested");
        return "index";
    }

    @GetMapping("/health")
    public String health() {
        return "CRM System is running";
    }
}
