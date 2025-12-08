package dev.yurets.db_demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Контролер для сторінки логіну та перенаправлення після входу
 */
@Controller
public class LoginController {

    /**
     * Сторінка логіну
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Перенаправлення після успішного логіну залежно від ролі:
     * - ADMIN → головна сторінка (CRUD)
     * - USER → сторінка перегляду (Read-only)
     */
    @GetMapping("/default")
    public String defaultAfterLogin(Authentication authentication) {
        // Отримуємо роль користувача
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("USER");

        // Перенаправляємо залежно від ролі
        if ("ADMIN".equals(role)) {
            return "redirect:/";
        } else {
            return "redirect:/viewAll";
        }
    }
}