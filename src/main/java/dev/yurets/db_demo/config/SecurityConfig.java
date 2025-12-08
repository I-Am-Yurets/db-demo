package dev.yurets.db_demo.config;

import dev.yurets.db_demo.service.MyUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Конфігурація Spring Security
 * - BCrypt для хешування паролів
 * - Розмежування доступу за ролями
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final MyUserDetailsService userDetailsService;

    public SecurityConfig(MyUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Вимикаємо CSRF для REST API (для production краще увімкнути)
                .csrf().disable()

                // Вимикаємо X-Frame-Options для можливості вбудовування
                .headers().frameOptions().disable()
                .and()

                // Налаштування авторизації
                .authorizeHttpRequests(auth -> auth
                        // Публічні сторінки (доступні всім)
                        .antMatchers("/login", "/css/**", "/js/**", "/images/**", "/error/**").permitAll()

                        // REST API корінь (інформація про endpoints) - доступний всім
                        .antMatchers("/api").permitAll()

                        // REST API endpoints - USER може тільки GET, ADMIN може все
                        .antMatchers("/api/**").hasAnyAuthority("USER", "ADMIN")
                        .antMatchers("/api/*/add", "/api/*/update", "/api/*/delete").hasAuthority("ADMIN")

                        // Веб-інтерфейс: CRUD операції тільки для ADMIN
                        .antMatchers("/add**", "/delete**", "/edit**", "/update**", "/").hasAuthority("ADMIN")

                        // Перегляд таблиць доступний для USER та ADMIN
                        .antMatchers("/viewAll").hasAnyAuthority("USER", "ADMIN")

                        // Всі інші запити вимагають авторизації
                        .anyRequest().authenticated()
                )

                // Налаштування форми логіну (для веб-інтерфейсу)
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                        .defaultSuccessUrl("/default", true) // Перенаправлення залежно від ролі
                        .failureUrl("/login?error=true")
                )

                // Налаштування Basic Auth (для REST API)
                .httpBasic()
                .and()

                // Налаштування виходу
                .logout(logout -> logout
                        .permitAll()
                        .logoutSuccessUrl("/login?logout=true")
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * BCrypt Password Encoder (10 раундів хешування)
     * ВАЖЛИВО: Використовуємо BCrypt, а не Base64!
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}