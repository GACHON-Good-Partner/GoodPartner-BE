package goodpartner.be.domain.user.application;

import goodpartner.be.domain.user.application.dto.request.SocialLoginRequest;
import goodpartner.be.domain.user.application.dto.response.SocialLoginResponse;
import goodpartner.be.domain.user.entity.User;
import goodpartner.be.domain.user.service.UserGetService;
import goodpartner.be.domain.user.service.UserSaveService;
import goodpartner.be.global.auth.jwt.JwtProvider;
import goodpartner.be.global.auth.kakao.KakaoAuthService;
import goodpartner.be.global.auth.kakao.dto.KakaoTokenResponse;
import goodpartner.be.global.auth.kakao.dto.KakaoUserInfoResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static goodpartner.be.domain.user.entity.enums.LoginStatus.LOGIN;
import static goodpartner.be.domain.user.entity.enums.LoginStatus.REGISTER;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserUsecase {

    private final KakaoAuthService kakaoAuthService;
    private final UserSaveService userSaveService;
    private final UserGetService userGetService;
    private final JwtProvider jwtProvider;

    @Transactional
    public SocialLoginResponse login(SocialLoginRequest dto) {
        KakaoTokenResponse tokenResponse = kakaoAuthService.getKakaoToken(dto.authCode());
        KakaoUserInfoResponse userInfo = kakaoAuthService.getUserInfo(tokenResponse.access_token());

        if (existUser(userInfo)) {
            return login(userInfo);
        }
        return registerUser(userInfo);
    }

    public boolean existUser(KakaoUserInfoResponse userInfo) {
        return userGetService.check(userInfo.id());
    }

    private SocialLoginResponse registerUser(KakaoUserInfoResponse userInfo) {
        String email = userInfo.kakao_account().email();
        String nickName = userInfo.kakao_account().profile().nickname();

        User user = User.of(userInfo.id(), email, nickName);
        userSaveService.saveUser(user);

        String accessToken = jwtProvider.generateAccessToken(userInfo.kakao_account().email());
        String refreshToken = jwtProvider.generateRefreshToken();

        return SocialLoginResponse.of(user.getId(), REGISTER, accessToken, refreshToken);
    }

    private SocialLoginResponse login(KakaoUserInfoResponse userInfo) {
        User user = userGetService.getUser(userInfo.id());

        String accessToken = jwtProvider.generateAccessToken(userInfo.kakao_account().email());
        String refreshToken = jwtProvider.generateRefreshToken();

        return new SocialLoginResponse(user.getId(), LOGIN, accessToken, refreshToken);
    }
}
