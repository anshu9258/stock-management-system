package com.niet.stockmanagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                    "/login",
                    "/signup",
                    "/register",
                    "/logout",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/videos/**"
                );
    }
}
