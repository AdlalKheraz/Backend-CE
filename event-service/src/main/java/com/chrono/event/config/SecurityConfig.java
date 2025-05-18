package com.chrono.event.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chrono.event.security.JwtAuthFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtFilter(JwtAuthFilter jwtAuthFilter) {
        FilterRegistrationBean<JwtAuthFilter> registrationBean = new FilterRegistrationBean<>();
        
        registrationBean.setFilter(jwtAuthFilter);
        registrationBean.addUrlPatterns("/api/events/*");
        registrationBean.setOrder(1);
        
        return registrationBean;
    }
} 