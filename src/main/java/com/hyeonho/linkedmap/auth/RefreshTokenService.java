package com.hyeonho.linkedmap.auth;

import com.hyeonho.linkedmap.enumlist.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService{

    private final JWTProvider jwtProvider;

    public RefreshTokenResponseDTO refreshToken(final String refreshToken) {
        // refresh token 유효성 검증
        checkRefreshToken(refreshToken);

        // refresh token id 조회
        var id = RefreshToken.getUserIdByRefreshToken(refreshToken);

        // TODO: 여기 나중에 role도 가져와야함
        // 새로운 access token 생성
        String newAccessToken = jwtProvider.generateAccessToken(id, Role.ROLE_USER);

        // 기존에 가지고 있는 사용자의 refresh token 제거
        RefreshToken.removeUserRefreshToken(id);

        // 새로운 refresh token 생성 후 저장
        String newRefreshToken = jwtProvider.generateRefreshToken(id);
        RefreshToken.putRefreshToken(newRefreshToken, id);

        return RefreshTokenResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    /**
     * refresh token 검증
     *
     * @param refreshToken refresh token
     */
    private void checkRefreshToken(final String refreshToken) {
        if(Boolean.FALSE.equals(jwtProvider.validateToken(refreshToken)))
            throw new RuntimeException("Refresh token is ?? ㅋㅋ");
    }

}
