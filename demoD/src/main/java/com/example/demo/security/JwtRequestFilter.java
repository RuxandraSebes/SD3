package com.example.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtRequestFilter.class);
    private final JwtUtil jwtUtil;

    public JwtRequestFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        Long userId = null;
        String role = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);

            LOGGER.info("Attempting to validate JWT for request: {}", request.getRequestURI());

            if (jwtUtil.validateToken(jwt)) {
                try {
                    userId = jwtUtil.extractUserId(jwt);
                    role = jwtUtil.extractRole(jwt);

                    LOGGER.info("JWT Validated. Extracted User ID: {} and Role: {}", userId, role);
                } catch (Exception e) {
                    LOGGER.error("Error extracting claims from JWT: {}", e.getMessage());
                    userId = null;
                    role = null;
                }
            } else {
                LOGGER.warn("JWT validation failed for request: {}", request.getRequestURI());
            }
        } else {
            LOGGER.info("No Bearer token found in Authorization header for request: {}", request.getRequestURI());
        }

        if (userId != null && role != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            String grantedAuthority = "ROLE_" + role.toUpperCase();
            LOGGER.info("Setting Security Context for Auth ID: {} with Granted Authority: {}", userId, grantedAuthority);

            UserAuthInfo authInfo = new UserAuthInfo(userId, role);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    authInfo, null, Collections.singletonList(new SimpleGrantedAuthority(grantedAuthority)));

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        chain.doFilter(request, response);
    }
}