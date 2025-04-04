package com.smhrd.teamjo.config;

import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 브라우저에서 /uploads/** 로 접근하면 실제 PC 경로에서 파일을 가져옴
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:///C:/Users/smhrd/Desktop/gitTestJo-1/upload/");
    }
}
