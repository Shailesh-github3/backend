package com.edubridge.backend.config;

import com.edubridge.backend.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // PUBLIC ENDPOINTS (no auth required)
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/users/register").permitAll()
                        .requestMatchers("/api/users/college-students").permitAll()
                        .requestMatchers("/api/users/college-students/search").permitAll()
                        .requestMatchers("/api/college/**").permitAll()

                        // FORUM BROWSING ENDPOINTS (PUBLIC - read-only)
                        .requestMatchers("/api/forum/all").permitAll()                    // List all forums
                        .requestMatchers("/api/forum/college/**").permitAll()             // Forums by college
                        .requestMatchers("/api/forum/*/posts").permitAll()                // Posts in forum
                        .requestMatchers("/api/forum/post/*/comments").permitAll()        // Comments on post

                        // ADMIN ENDPOINTS (role-based)
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // ✅ Reporting endpoints (authenticated users can report)
                        .requestMatchers("/api/report/**").authenticated()

                        // ✅ Admin endpoints (role-based)
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // PROTECTED ENDPOINTS (require auth + role checks in service layer)
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}