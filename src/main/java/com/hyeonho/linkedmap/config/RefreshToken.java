package com.hyeonho.linkedmap.config;

import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@NoArgsConstructor
public class RefreshToken {

    protected static final Map<String, String> refreshTokens = new HashMap<>();

    /**
     * refresh token get
     *
     * @param refreshToken refresh token
     * @return id
     */
    public static String getRefreshToken(final String refreshToken) {
        return Optional.ofNullable(refreshTokens.get(refreshToken))
                .orElseThrow(() -> new RuntimeException("Refresh token does not exist."));
    }

    /**
     * refresh token put
     *
     * @param refreshToken refresh token
     * @param email email
     */
    public static void putRefreshToken(final String refreshToken, String email) {
        refreshTokens.put(refreshToken, email);
    }

    /**
     * refresh token remove
     *
     * @param refreshToken refresh token
     */
    private static void removeRefreshToken(final String refreshToken) {
        refreshTokens.remove(refreshToken);
    }

    // user refresh token remove
    public static void removeUserRefreshToken(final String email) {
        refreshTokens.entrySet().removeIf(entry -> entry.getValue(). equals(email));
    }
}
