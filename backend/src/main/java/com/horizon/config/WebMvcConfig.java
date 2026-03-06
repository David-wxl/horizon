package com.horizon.config;

import com.horizon.interceptor.JwtInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/user/login",
                        "/user/register",
                        "/user/**",
                        "/card/square",
                        "/card/detail/**",
                        "/card/user/*/public",
                        "/card/category/**",
                        "/comment/card/**",
                        "/like/count/**",
                        "/like/check/**",
                        "/follow/check",
                        "/follow/stats/**",
                        "/favorite/check",
                        "/favorite/count/**",
                        "/profile/**",
                        "/admin/**"
                );
    }
}
