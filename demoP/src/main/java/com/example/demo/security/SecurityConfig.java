// demoP/security/SecurityConfig.java (MODIFICAT)
package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/auth/**").permitAll()
                            .requestMatchers(HttpMethod.POST, "/people/internal-auth-insert").permitAll()
                            .requestMatchers(HttpMethod.DELETE, "/devices/user/**").permitAll()
                            .requestMatchers(HttpMethod.GET, "/people/by-auth/**").permitAll()
                            // FIX CRITIC: Adaugă excepție pentru GET /people/{id} folosit de Devices Service
                            .requestMatchers(HttpMethod.GET, "/people/**").permitAll();

                    auth.requestMatchers(HttpMethod.POST, "/people", "/devices").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.PUT, "/people/**", "/devices/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.DELETE, "/people/**", "/devices/**").hasRole("ADMIN");

                    auth.requestMatchers("/people/**", "/devices/**").authenticated();

                    auth.anyRequest().authenticated();
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}