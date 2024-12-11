package goodpartner.user.application;


import goodpartner.global.auth.jwt.JwtProvider;
import goodpartner.global.auth.kakao.KakaoAuthService;
import goodpartner.global.auth.kakao.dto.KakaoUserInfoResponse;
import goodpartner.user.application.dto.request.SocialLoginRequest;
import goodpartner.user.application.dto.request.UserUpdateRequest;
import goodpartner.user.application.dto.response.SocialLoginResponse;
import goodpartner.user.application.dto.response.UserResponse;
import goodpartner.user.entity.User;
import goodpartner.user.service.UserGetService;
import goodpartner.user.service.UserSaveService;
import goodpartner.user.service.UserUpdateService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static goodpartner.user.entity.enums.LoginStatus.LOGIN;
import static goodpartner.user.entity.enums.LoginStatus.REGISTER;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserUsecase {

    private final KakaoAuthService kakaoAuthService;
    private final UserSaveService userSaveService;
    private final UserGetService userGetService;
    private final UserUpdateService userUpdateService;
    private final JwtProvider jwtProvider;

    @Transactional
    public SocialLoginResponse login(SocialLoginRequest dto) {
//        KakaoTokenResponse tokenResponse = kakaoAuthService.getKakaoToken(dto.authCode());
        KakaoUserInfoResponse userInfo = kakaoAuthService.getUserInfo(dto.authCode());

        if (existUser(userInfo)) {
            return login(userInfo);
        }
        return registerUser(userInfo);
    }

    public boolean existUser(KakaoUserInfoResponse userInfo) {
        return userGetService.check(userInfo.id());
    }

    public UserResponse getUser(String userId) {
        UUID id = UUID.fromString(userId);
        User user = userGetService.getUser(id);
        return UserResponse.from(user);
    }

    @Transactional
    public void update(UserUpdateRequest dto, String userId) {
        UUID id = UUID.fromString(userId);
        User user = userGetService.getUser(id);
        userUpdateService.updateUser(user, dto);
    }

    private SocialLoginResponse registerUser(KakaoUserInfoResponse userInfo) {
        String email = userInfo.kakao_account().email();
        String nickName = userInfo.kakao_account().profile().nickname();

        User user = User.of(userInfo.id(), email, nickName);
        userSaveService.saveUser(user);

        String accessToken = jwtProvider.generateAccessToken(user.getId());
        String refreshToken = jwtProvider.generateRefreshToken();

        return SocialLoginResponse.of(user.getId(), REGISTER, accessToken, refreshToken);
    }

    private SocialLoginResponse login(KakaoUserInfoResponse userInfo) {
        User user = userGetService.getUser(userInfo.id());

        String accessToken = jwtProvider.generateAccessToken(user.getId());
        String refreshToken = jwtProvider.generateRefreshToken();

        return new SocialLoginResponse(user.getId(), LOGIN, accessToken, refreshToken);
    }
}
