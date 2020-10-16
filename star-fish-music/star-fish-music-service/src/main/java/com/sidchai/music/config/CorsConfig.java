package com.sidchai.music.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    //解决跨域问题
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")//放行哪些原始域
                .allowedMethods("GET", "HEAD", "POST", "DELETE", "PUT", "OPTIONS")//放行哪些原始域(请求方式)
                .allowCredentials(true)//是否发生cookie信息
                .maxAge(3600)//预测请求最大缓存时间
                .allowedHeaders("*");
    }
}
