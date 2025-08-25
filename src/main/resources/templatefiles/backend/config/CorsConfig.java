package com.example.demo.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;

@Component
@Configuration
public class CorsConfig implements CorsConfigurationSource {

    @Value("${cors.allowed-origins:http://localhost:4200}")
    private String[] allowedOrigins;

    @Value("${cors.allowed-methods:GET, PUT, POST, DELETE, OPTIONS}")
    private String[] allowedMethods;

    @Value("${cors.allowed-headers:X-TOTAL-COUNT, X-File-Name, authorization, Content-Type}")
    private String[] allowedHeaders;

    @Value("${cors.exposed-headers:X-TOTAL-COUNT, X-File-Name}")
    private String[] exposedHeaders;

    @Value("${cors.max-age:3600}")
    private Long maxAge;


    @Override
    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(allowedOrigins));
        config.setAllowedMethods(Arrays.asList(allowedMethods));
        config.setAllowedHeaders(Arrays.asList(allowedHeaders));
        config.setExposedHeaders(Arrays.asList(exposedHeaders));
        config.setMaxAge(maxAge);
        return config;
    }
}
