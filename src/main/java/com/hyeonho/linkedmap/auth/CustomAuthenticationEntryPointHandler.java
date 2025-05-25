package com.hyeonho.linkedmap.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * AuthenticationEntryPoint 의 구현체
 * 사용자가 인증되지않거나, 유효한 인증정보를 가지고 있지 않은 경우 동작
 * ex. 로그인 하지 않은 유저가 내정보 요청 보내는 등...
 */
@Slf4j
@Component
public class CustomAuthenticationEntryPointHandler implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        log.info("[JwtAuthenticationFilter] Authorization 헤더: {}", authorizationHeader);

        log.info("[CustomAuthenticationEntryPointHandler] :: {}", authException.getMessage());
        log.info("[CustomAuthenticationEntryPointHandler] :: {}", request.getRequestURL());
        log.info("[CustomAuthenticationEntryPointHandler] :: 토근 정보가 만료되었거나 존재하지 않음");

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

//        JsonObject returnJson = new JsonObject();
//        returnJson.addProperty("errorCode", ApiExceptionEnum.ACCESS_DENIED.getCode());
//        returnJson.addProperty("errorMsg", ApiExceptionEnum.ACCESS_DENIED.getMessage());
//
//        PrintWriter out = response.getWriter();
//        out.print(returnJson);
        response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"토큰이 없거나 만료되었습니다.ㅋㅋ\"}");
    }
}
