package com.barmalat.medicalclinic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // doctors
                        .requestMatchers(HttpMethod.GET, "/doctors/**").hasAuthority("read:doctor")
                        .requestMatchers(HttpMethod.POST, "/doctors").hasAuthority("create:doctor")
                        .requestMatchers(HttpMethod.PUT, "/doctors/**").hasAuthority("update:doctor")
                        .requestMatchers(HttpMethod.DELETE, "/doctors/**").hasAuthority("delete:doctor")
                        .requestMatchers(HttpMethod.PATCH, "/doctors/**").hasAuthority("update:doctor")

                        // patients
                        .requestMatchers(HttpMethod.GET, "/patients/**").hasAuthority("read:patient")
                        .requestMatchers(HttpMethod.POST, "/patients").hasAuthority("create:patient")
                        .requestMatchers(HttpMethod.PUT, "/patients/**").hasAuthority("update:patient")
                        .requestMatchers(HttpMethod.DELETE, "/patients/**").hasAuthority("delete:patient")
                        .requestMatchers(HttpMethod.PATCH, "/patients/**").hasAuthority("update:patient")

                        // visits
                        .requestMatchers(HttpMethod.GET, "/visits/**").hasAuthority("read:visit")
                        .requestMatchers(HttpMethod.POST, "/visits").hasAuthority("create:visit")
                        .requestMatchers(HttpMethod.PATCH, "/visits/*/patient/*").hasAuthority("book:visit")
                        .requestMatchers(HttpMethod.PATCH, "/visits/*/cancel").hasAuthority("cancel:visit")
                        .requestMatchers(HttpMethod.DELETE, "/visits/**").hasAuthority("delete:visit")

                        // facilities
                        .requestMatchers(HttpMethod.GET, "/facilities/**").hasAuthority("read:facility")
                        .requestMatchers(HttpMethod.POST, "/facilities").hasAuthority("create:facility")
                        .requestMatchers(HttpMethod.PUT, "/facilities/**").hasAuthority("update:facility")
                        .requestMatchers(HttpMethod.DELETE, "/facilities/**").hasAuthority("delete:facility")

                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults()));
        return http.build();
    }
}