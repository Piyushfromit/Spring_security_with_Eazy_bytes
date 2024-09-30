package com.eazybytes.config;

import com.eazybytes.exceptionhandling.CustomAccessDeniedHandler;
import com.eazybytes.exceptionhandling.CustomBasicAuthenticationEntryPoint;
import com.eazybytes.filter.CsrfCookieFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@Profile("prod")
public class ProjectSecurityProdConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        CsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler = new CsrfTokenRequestAttributeHandler();


        http
                .securityContext(contextConfig -> contextConfig.requireExplicitSave(false))

                .sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))

                .cors(corsConfig -> corsConfig.configurationSource(new CorsConfigurationSource(){
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration config = new CorsConfiguration();
                        config.setAllowedOrigins(Collections.singletonList("https://localhost:4200"));
                        config.setAllowedMethods(Collections.singletonList("*"));
                        config.setAllowCredentials(true);
                        config.setAllowedHeaders(Collections.singletonList("*"));
                        config.setMaxAge(3600L);
                        return config;
                    }
                }))

                .csrf(csrfConfigurer -> csrfConfigurer.csrfTokenRequestHandler(csrfTokenRequestAttributeHandler)
                        .ignoringRequestMatchers("/contact", "/register")  // to ignore the CSRF Protection for these URLs
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class) // Only HTTPS

                .sessionManagement(sessionManagementConfiguration ->  sessionManagementConfiguration.invalidSessionUrl("/invalidSession").maximumSessions(10).maxSessionsPreventsLogin(true))
                .requiresChannel(requestChannelConfiguration -> requestChannelConfiguration.anyRequest().requiresSecure())
                .authorizeHttpRequests((requests) -> requests
//                    .requestMatchers("/", "/myAccount", "/myBalance", "/myLoans", "/myCards", "/welcome", "/user").authenticated()
//                    .requestMatchers( "/myAccount").hasAuthority("VIEWACCOUNT")
//                    .requestMatchers( "/myBalance").hasAnyAuthority("VIEWBALANCE", "VIEWACCOUNT")
//                    .requestMatchers( "/myLoans").hasAuthority("VIEWLOANS")
//                    .requestMatchers(  "/myCards").hasAuthority("VIEWCARDS")
//                    .requestMatchers( "/user").authenticated()
                        .requestMatchers( "/myAccount").hasRole("USER")
                        .requestMatchers( "/myBalance").hasAnyRole("USER", "ADMIN")
                        .requestMatchers( "/myLoans").hasRole("USER")
                        .requestMatchers(  "/myCards").hasRole("USER")
                        .requestMatchers( "/user").authenticated()
                    .requestMatchers("/notices", "/contact", "/error", "/logout", "/register", "/invalidSession").permitAll());
        http.formLogin(withDefaults());
        http.httpBasic(httpBasicConfig -> httpBasicConfig.authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint()));
        http.exceptionHandling(exceptionHandlingConfig ->exceptionHandlingConfig.accessDeniedHandler(new CustomAccessDeniedHandler()));
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