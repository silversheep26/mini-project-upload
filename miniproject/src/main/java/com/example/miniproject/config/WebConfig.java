package com.example.miniproject.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    public void addCorsMappings(final CorsRegistry registry){
        registry.addMapping("/**")
                .exposedHeaders("ACCESS_KEY")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST"); // 허용할 HTTP method
    }
}
