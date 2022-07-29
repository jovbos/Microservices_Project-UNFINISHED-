package com.example.Back_Empresa.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig {

    // --- Se configura quien podra acceder a cada endpoint ---
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .addFilterAfter(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/h2-console/*").permitAll()
                .antMatchers("/api/v0/token").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v0/client").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/v0/client*").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/api/v0/client").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/v0/client*").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/api/v0/employee").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/v0/employee*").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/api/v0/employee").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/v0/employee*").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/api/v0/email").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/v0/email*").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v0/email*").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/v0/email*").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/api/v0/ticket*").authenticated()
                .antMatchers(HttpMethod.GET, "/api/v0/ticket*").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/v0/ticket*").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/api/v0/trip").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/v0/trip*").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/api/v0/trip*").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/v0/trip*").hasRole("ADMIN");
        http.headers().frameOptions().disable();

        return http.build();
    }

}

