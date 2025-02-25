package com.hyeonho.linkedmap.config;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RefreshTokenResponseDTO {
    private final String accessToken;
    private final String refreshToken;
}