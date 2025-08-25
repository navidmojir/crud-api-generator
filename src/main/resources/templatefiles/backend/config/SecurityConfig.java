package com.example.demo.config;

import ir.mojir.my_kc_auth_client.spring_boot_integration.KeycloakAuthorizationSecurityFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private KeycloakAuthorizationSecurityFilter keycloakAuthorizationSecurityFilter;

    @Autowired
    private CorsConfig corsConfig;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests((authorize) -> authorize.anyRequest().authenticated())
                .oauth2ResourceServer((oauth) -> oauth.jwt(Customizer.withDefaults()))
                .addFilterAfter(keycloakAuthorizationSecurityFilter, AuthorizationFilter.class)
                .cors(cc -> cc.configurationSource(corsConfig))
                .csrf(cc -> cc.disable())
                .build();

    }


}
