package com.hyeonho.linkedmap.config;

import com.hyeonho.linkedmap.enumlist.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;

    private final CustomAuthenticationEntryPointHandler customAuthenticationEntryPointHandler;

    private final CustomAccessDeniedHandler customAccessDeniedHandler;


    // 이건 또 뭐임?
//    @Bean
//    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
//        return new MvcRequestMatcher.Builder(introspector);
//    }

    @Bean
    public SecurityFilterChain config(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
//        MvcRequestMatcher.Builder mvc = new MvcRequestMatcher.Builder(introspector);

        RequestMatcher[] permitAllWhiteList = {
                new AntPathRequestMatcher("/api/v1/login"),
                new AntPathRequestMatcher("/api/v1//kakao/auth"),
                new AntPathRequestMatcher("/api/v1/register"),
                new AntPathRequestMatcher("/api/v1/token-refresh"),
                new AntPathRequestMatcher("/api/v1/favicon.ico"),
                new AntPathRequestMatcher("/api/v1/error")

        };

        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(permitAllWhiteList).permitAll()
                // 그 외 요청 체크
                .anyRequest().authenticated()
        );

        http.formLogin(AbstractHttpConfigurer::disable); // 기본 로그인 폼 비활성화
        http.logout(AbstractHttpConfigurer::disable); // 기본 로그아웃 기능 비활성화
        http.csrf(AbstractHttpConfigurer::disable); // CSRF 보호 비활성화 (restapi는 필요없음)
        http.cors(Customizer.withDefaults());

        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 미사용
        );

        // before filter
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // exception handler
        http.exceptionHandling(conf -> conf
                .authenticationEntryPoint(customAuthenticationEntryPointHandler)
                .accessDeniedHandler(customAccessDeniedHandler)
        );

        return http.build();
    }


}
