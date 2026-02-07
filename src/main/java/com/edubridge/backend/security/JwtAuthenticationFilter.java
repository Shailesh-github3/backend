package com.edubridge.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        logger.info("🔍 JWT Filter triggered for: {}", request.getRequestURI());

        final String authHeader = request.getHeader("Authorization");
        logger.info("Header Authorization: {}", authHeader != null ? authHeader.substring(0, Math.min(30, authHeader.length())) : "null");

        String username = null;
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            logger.info("Token extracted (first 20 chars): {}", token.substring(0, Math.min(20, token.length())));

            try {
                username = jwtUtil.extractUsername(token);
                logger.info("Username extracted from token: {}", username);
            } catch (Exception e) {
                logger.error("❌ Error extracting username from token", e);
            }
        } else {
            logger.warn("⚠️ No Authorization header or missing Bearer prefix");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.info("Attempting to authenticate user: {}", username);

            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                logger.info("UserDetails loaded. Username: {}, Authorities: {}",
                        userDetails.getUsername(), userDetails.getAuthorities());

                if (jwtUtil.validateToken(token)) {
                    logger.info("✅ Token validated successfully");

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.info("✅ Authentication SUCCESSFULLY set in SecurityContext");
                } else {
                    logger.warn("❌ Token validation failed");
                }
            } catch (Exception e) {
                logger.error("❌ Error during authentication", e);
            }
        } else {
            logger.info("SecurityContext already has authentication or no username extracted");
        }

        filterChain.doFilter(request, response);
    }
}