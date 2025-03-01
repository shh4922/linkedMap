package com.hyeonho.linkedmap.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 토큰 생성, 검증, 관리를 담당
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JWTProvider {
    // 만료시간 설정
    private static final long JWT_TOKEN_VALID = (long) 1000*60*30;

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey key;

    @PostConstruct
    public void init() {
        key= Keys.hmacShaKeyFor(secret.getBytes());
    }


    /**
     * Claims.getId() 는 Claims 중 jti라는 애를 가져와서 리턴함
     * jti는 JWT의 고유 식별자임. 중복제거를 위한 고유값인데, 블랙리스트, 중복로그인 방지를 위해 쓰임.
     */
    public String getUsernameFromToken(final String token) {
        return getClaimFromToken(token, Claims::getId);
    }

    /**
     * token 사용자 속성 정보 조회
     * @param token JWT
     * @param claimsResolver 함수를 매개변수로 받는 상황인거
     *                       getUsernameFromToken()에서 Claims.getId() 라는 함수를 넘겨줌.
     *                       그럼 getClaimFromToken() 서 claimsResolver에서 해당함수 받고 전달받은 함수 수행해서 리턴함.
     *                       그때 수행하게 하는게 apply임. apply하면 해당함수 실행하게 하는것같음.
     *                       리턴타입은 매개변수로 전달받은 함수의 리턴값을 해당함수의 리턴값으로 하는것같음.
     * @return Target Claim
     * @param <T>
     */
    public <T> T getClaimFromToken(final String token, final Function<Claims,T> claimsResolver) {
        if(Boolean.FALSE.equals(validateToken(token)))
            return null;
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * token 사용자의 모든 속성 조회
     * @param token
     * @return
     */
    private Claims getAllClaimsFromToken(final String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // 키로 서명하고
                .build() // 까고
                .parseClaimsJws(token) // 클래임 파싱하고
                .getBody(); // 파싱한거의 body(저장된데이터) 추출인가보군 ㅋㅋ
    }

    /**
     * 토큰 만료일 조회
     * @param token
     * @return
     */
    public Date getExpirationDateFromToken(final String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * access token 생성
     *
     * @param id token 생성 id
     * @return access token
     */
    public String generateAccessToken(final String id) {
        return generateAccessToken(id, new HashMap<>());
    }

    /**
     * access token 생성
     *
     * @param id token 생성 id
     * @return access token
     */
    public String generateAccessToken(final long id) {
        return generateAccessToken(String.valueOf(id), new HashMap<>());
    }

    /**
     * access token 생성
     *
     * @param id token 생성 id
     * @param claims token 생성 claims
     * @return access token
     */
    public String generateAccessToken(final String id, final Map<String, Object> claims) {
        return doGenerateAccessToken(id, claims);
    }

    /**
     * JWT access token 생성
     *
     * @param id token 생성 id
     * @param claims token 생성 claims
     * @return access token
     */
    private String doGenerateAccessToken(final String id, final Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setId(id)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALID)) // 30분
                .signWith(key)
                .compact();
    }

    /** Refresh ------------------------------------------------------------------------------- */

    /**
     * refresh token 생성
     *
     * @param id token 생성 id
     * @return refresh token
     */
    public String generateRefreshToken(final String id) {
        return doGenerateRefreshToken(id);
    }

    /**
     * refresh token 생성
     *
     * @param id token 생성 id
     * @return refresh token
     */
    public String generateRefreshToken(final long id) {
        return doGenerateRefreshToken(String.valueOf(id));
    }

    /**
     * refresh token 생성
     *
     * @param id token 생성 id
     * @return refresh token
     */
    private String doGenerateRefreshToken(final String id) {
        return Jwts.builder()
                .setId(id)
                .setExpiration(new Date(System.currentTimeMillis() + (JWT_TOKEN_VALID * 2) * 24)) // 24시간
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(key)
                .compact();
    }

    /**
     * token 검증
     *
     * @param token JWT
     * @return token 검증 결과
     */
    public Boolean validateToken(final String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

}
