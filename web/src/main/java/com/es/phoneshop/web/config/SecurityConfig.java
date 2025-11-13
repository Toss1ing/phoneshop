package com.es.phoneshop.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.admin.username}")
    private String adminUser;

    @Value("${app.admin.password}")
    private String adminPassword;

    private static final String LOGIN_URL = "/login";
    private static final String ADMIN_URL_PATTERN = "/admin/**";
    private static final String LOGOUT_URL = "/logout";
    private static final String DEFAULT_SUCCESS_URL = "/";
    private static final String LOGIN_FAILURE_URL = "/login?error=true";
    private static final String ADMIN_ROLE = "ADMIN";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(new AntPathRequestMatcher(LOGIN_URL)).permitAll()
                        .requestMatchers(new AntPathRequestMatcher(ADMIN_URL_PATTERN)).hasRole(ADMIN_ROLE)
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage(LOGIN_URL)
                        .loginProcessingUrl(LOGIN_URL)
                        .defaultSuccessUrl(DEFAULT_SUCCESS_URL, true)
                        .failureHandler(customAuthenticationFailureHandler())
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl(LOGOUT_URL)
                        .logoutSuccessUrl(DEFAULT_SUCCESS_URL)
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public CustomAuthenticationFailureHandler customAuthenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler(LOGIN_FAILURE_URL);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(
                User.withUsername(adminUser)
                        .password(
                                passwordEncoder().encode(adminPassword)
                        )
                        .roles(ADMIN_ROLE)
                        .build()
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
