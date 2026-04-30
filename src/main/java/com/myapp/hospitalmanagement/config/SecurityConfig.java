package com.myapp.hospitalmanagement.config;

import com.myapp.hospitalmanagement.filter.CustomLogoutHandler;
import com.myapp.hospitalmanagement.filter.CustomLogoutSuccessHandler;
import com.myapp.hospitalmanagement.filter.JWTFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
/*
* @EnableWebSecurity
*
* without this annotation spring-boot use default security filter chain method
*
* and if we add this annotation then spring-boot use define / custom security filter chain method as below
* */
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final JWTFilter jwtFilter;
    private final CustomLogoutHandler customLogoutHandler;
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;

    @Autowired
    public SecurityConfig(
            UserDetailsService userDetailsService,
            JWTFilter jwtFilter,
            CustomLogoutHandler customLogoutHandler,
            CustomLogoutSuccessHandler customLogoutSuccessHandler
    ) {
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
        this.customLogoutHandler = customLogoutHandler;
        this.customLogoutSuccessHandler = customLogoutSuccessHandler;
    }

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(customizer -> customizer.disable())
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth ->
                    auth
                            .requestMatchers("/api/patient/**").hasAnyRole("EMPLOYEE", "ADMIN")
                            .requestMatchers("/api/appointment/**").hasAnyRole("EMPLOYEE", "ADMIN")
                            .requestMatchers("/api/doctor/**").hasAnyRole("ADMIN")
                            .requestMatchers("/api/insurance/**").hasAnyRole("ADMIN")
                            .requestMatchers("/api/auth/sign-up").hasRole("ADMIN")
                            .requestMatchers("/api/auth/sign-in").permitAll()
                            .anyRequest().authenticated()
            )
            .logout(logout -> logout
                    .logoutUrl("/api/auth/logout")
                    .addLogoutHandler(customLogoutHandler)
                    .logoutSuccessHandler(customLogoutSuccessHandler)
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
            )
            .httpBasic(customizer -> customizer.disable())
            .sessionManagement(
                    session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
      return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(bCryptPasswordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) {
        return configuration.getAuthenticationManager();
    }

}
