package com.eazybytes.config;

import com.eazybytes.exceptionhandling.CustomAccessDeniedHandler;
import com.eazybytes.exceptionhandling.CustomBasicAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@Profile("prod")
public class ProjectSecurityProdConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        http.requiresChannel(requestChannelConfiguration -> requestChannelConfiguration.anyRequest().requiresSecure())
                .csrf(csrfConfigurer -> csrfConfigurer.disable())
                .authorizeHttpRequests((requests) -> requests
                .requestMatchers("/", "/myAccount", "/myBalance", "/myLoans", "/myCards", "/welcome").authenticated()
                .requestMatchers("/notices", "/contact", "/error", "/logout", "/register").permitAll());
        http.formLogin(withDefaults());
        http.httpBasic(httpBasicConfig -> httpBasicConfig.authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint()));
        //http.exceptionHandling(exceptionHandlingConfig ->exceptionHandlingConfig.authenticationEntryPoint( new CustomBasicAuthenticationEntryPoint())); // It is an Global Config
        //http.exceptionHandling(exceptionHandlingConfig ->exceptionHandlingConfig.accessDeniedHandler(new CustomAccessDeniedHandler()));
        http.exceptionHandling(exceptionHandlingConfig ->exceptionHandlingConfig.accessDeniedHandler(new CustomAccessDeniedHandler()).accessDeniedPage("/errorJspPage"));// for MVC or jsp included project
        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // not to use simple password
    @Bean
    public CompromisedPasswordChecker compromisedPasswordChecker(){
        return new HaveIBeenPwnedRestApiPasswordChecker();
    }


}
