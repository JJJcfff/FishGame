package com.example.fishgame.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class SecurityConfig {

    @Autowired
    private TimestampVerificationFilter timestampVerificationFilter;

    @Autowired
    private TokenVerificationFilter tokenVerificationFilter;

    @Autowired
    private DynamicRateLimiter dynamicRateLimiter;

    @Bean
    public FilterRegistrationBean<TimestampVerificationFilter> timestampVerificationFilterRegistration() {
        FilterRegistrationBean<TimestampVerificationFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(timestampVerificationFilter);
        registration.addUrlPatterns("/*");
        registration.setName("timestampVerificationFilter");
        return registration;
    }

    @Bean
    public FilterRegistrationBean<TokenVerificationFilter> tokenVerificationFilterRegistration() {
        FilterRegistrationBean<TokenVerificationFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(tokenVerificationFilter);
        registration.addUrlPatterns("/*");
        registration.setName("tokenVerificationFilter");
        return registration;
    }

    @Bean
    public FilterRegistrationBean<DynamicRateLimiter> dynamicRateLimiterRegistration() {
        FilterRegistrationBean<DynamicRateLimiter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(dynamicRateLimiter);
        registrationBean.addUrlPatterns("/*"); // 全局限流
        registrationBean.setName("dynamicRateLimiter");
        return registrationBean;
    }

} 