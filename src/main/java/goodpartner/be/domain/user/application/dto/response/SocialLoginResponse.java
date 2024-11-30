package goodpartner.be.domain.user.application.dto.response;

import goodpartner.be.domain.user.entity.enums.LoginStatus;

import java.util.UUID;

public record SocialLoginResponse(
        UUID id,
        LoginStatus status,
        String accessToken,
        String refreshToken
) {
    public static SocialLoginResponse of(UUID userId, LoginStatus status, String accessToken, String refreshToken) {
        return new SocialLoginResponse(userId, status, accessToken, refreshToken);
    }
}
