package com.crm.controller;

import com.crm.entity.User;
import com.crm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Auth Controller
 * Handles authentication-related requests
 *
 * @author CRM Team
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal OAuth2User oauth2User, Model model) {
        log.info("Profile page accessed");

        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String sub = oauth2User.getAttribute("sub");

        // Create or update user in database
        User user = userRepository.findByProviderAndProviderId("oauth2", sub)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUsername(email);
                    newUser.setEmail(email);
                    newUser.setFullName(name);
                    newUser.setProvider("oauth2");
                    newUser.setProviderId(sub);
                    newUser.setRole("USER");
                    return newUser;
                });

        userRepository.save(user);

        model.addAttribute("user", user);
        model.addAttribute("oauthUser", oauth2User);

        return "profile";
    }

    @PostMapping("/logout")
    public String logout() {
        return "redirect:/login?logout";
    }
}
