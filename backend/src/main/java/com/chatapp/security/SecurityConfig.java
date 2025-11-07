package com.chatapp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
  @Bean
  SecurityFilterChain filter(HttpSecurity http) throws Exception {
    http.csrf(c -> c.ignoringRequestMatchers("/ws-chat/**"))
        .authorizeHttpRequests(a -> a
            .requestMatchers("/ws-chat/**").permitAll()
            .anyRequest().permitAll())
        .httpBasic(b -> b.disable())
        .formLogin(f -> f.disable());
    return http.build();
  }
}
