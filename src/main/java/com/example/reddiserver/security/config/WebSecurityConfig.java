package com.example.reddiserver.security.config;

import com.example.reddiserver.security.jwt.JwtAuthenticationFilter;
import com.example.reddiserver.security.jwt.JwtExceptionHandlerFilter;
import com.example.reddiserver.security.jwt.exception.JwtAccessDeniedHandler;
import com.example.reddiserver.security.jwt.exception.JwtAuthenticationEntryPoint;
import com.example.reddiserver.security.oauth.handler.OAuthFailureHandler;
import com.example.reddiserver.security.oauth.handler.OAuthSuccessHandler;
import com.example.reddiserver.security.oauth.PrincipalOAuth2DetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtExceptionHandlerFilter jwtExceptionHandlerFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final PrincipalOAuth2DetailsService customOAuth2UserService;
    private final OAuthSuccessHandler oAuthSuccessHandler;
    private final OAuthFailureHandler oAuthFailureHandler;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors((cors) -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())
                .exceptionHandling(authenticationManager -> authenticationManager
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .oauth2Login(oauth2LoginConfigurer -> oauth2LoginConfigurer
                        .failureHandler(oAuthFailureHandler)
                        .successHandler(oAuthSuccessHandler)
                        .userInfoEndpoint()
                        .userService(customOAuth2UserService))
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/static/**", "/resources/**", "/css/**", "/js/**", "/images/**", "/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/swagger-ui/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtExceptionHandlerFilter, JwtAuthenticationFilter.class);

        return http.build();
    }
}
