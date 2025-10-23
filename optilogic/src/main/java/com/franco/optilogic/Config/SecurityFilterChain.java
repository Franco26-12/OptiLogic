package com.franco.optilogic.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;

import static org.springframework.security.config.Customizer.*;

@Configuration
public class SecurityFilterChain {

    @Bean
    public DefaultSecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/admin/**").hasRole("ADMIN") // Solo usuarios admin
                .anyRequest().denyAll() // Restringe todo lo demás
            )
            .formLogin(withDefaults()) // Importa static
            .logout(withDefaults())
            .rememberMe(withDefaults());
        return http.build();
    }
}
