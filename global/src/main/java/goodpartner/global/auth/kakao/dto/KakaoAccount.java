package goodpartner.global.auth.kakao.dto;

public record KakaoAccount(
        Boolean is_email_valid,
        Boolean is_email_verified,
        String email,
        Profile profile
) {
}
