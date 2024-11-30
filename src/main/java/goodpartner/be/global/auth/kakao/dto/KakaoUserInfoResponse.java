package goodpartner.be.global.auth.kakao.dto;

public record KakaoUserInfoResponse(
        Long id,
        KakaoAccount kakao_account
) {
}
