package com.hyeonho.linkedmap.auth;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RefreshTokenResponseDTO {
    private final String accessToken;
    private final String refreshToken;
}