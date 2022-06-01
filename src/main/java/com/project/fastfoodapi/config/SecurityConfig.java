package com.project.fastfoodapi.config;

import com.project.fastfoodapi.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    final JwtFilter jwtFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
//                .headers(headers -> headers
                        // allow same origin to frame our site to support iframe SockJS
//                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
//                )
                .cors().configurationSource(request -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOriginPatterns(List.of("*"));
                    configuration.setAllowedMethods(List.of("GET", "POST", "OPTIONS", "PUT", "DELETE", "PATCH"));
                    configuration.setAllowedHeaders(List.of("*"));
                    configuration.setAllowCredentials(true);
                    return configuration;
                }).and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/auth/**").permitAll()
                .antMatchers("/api/assets/**").permitAll()
                .antMatchers("/api/ws/**").permitAll()
                .antMatchers("/api/**").authenticated()
                .and().httpBasic().disable();
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }



}
