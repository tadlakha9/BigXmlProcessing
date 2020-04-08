package com.soprasteria.springboot.utils;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsFilterConfig implements WebMvcConfigurer{
	 /**
     * {@inheritDoc}
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
		/*
		 * registry.addMapping("/**") .allowedMethods(HttpMethod.GET.name(),
		 * HttpMethod.POST.name());
		 */
        registry.addMapping("/**").allowedMethods("GET", "POST", "PUT", "DELETE").allowedOrigins("*")
        .allowedHeaders("*");
		/*
		 * .allowedMethods(HttpMethod.GET.name(), HttpMethod.POST.name())
		 * .allowCredentials(false)
		 */
    }   
}
