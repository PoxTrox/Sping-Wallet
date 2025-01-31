package org.example.spingwallet.config;

import org.example.spingwallet.security.SessionCheckInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {


    @Autowired
    private SessionCheckInterceptor sessionCheckInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        WebMvcConfigurer.super.addInterceptors(registry);
        registry.addInterceptor(sessionCheckInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/js/**", "/images/**", "/fonts/**");
    }
}
