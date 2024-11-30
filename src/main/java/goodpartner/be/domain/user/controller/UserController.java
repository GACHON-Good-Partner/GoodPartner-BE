package goodpartner.be.domain.user.controller;

import goodpartner.be.domain.user.application.UserUsecase;
import goodpartner.be.domain.user.application.dto.request.SocialLoginRequest;
import goodpartner.be.domain.user.application.dto.response.SocialLoginResponse;
import goodpartner.be.global.common.response.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static goodpartner.be.domain.user.controller.ResponseMessage.SOCIAL_LOGIN_SUCCESS;
import static goodpartner.be.domain.user.controller.ResponseMessage.SOCIAL_REGISTER_SUCCESS;
import static goodpartner.be.domain.user.entity.enums.LoginStatus.LOGIN;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserUsecase userUsecase;

    @PostMapping("/social-login")
    @Operation(summary = "카카오 소셜 로그인/회원가입 API")
    public ResponseDto<SocialLoginResponse> login(@RequestBody @Valid SocialLoginRequest dto) {
        SocialLoginResponse response = userUsecase.login(dto);
        if (response.status() == LOGIN) {
            return ResponseDto.response(OK.value(), SOCIAL_LOGIN_SUCCESS.getMessage(), response);
        }
        return ResponseDto.response(OK.value(), SOCIAL_REGISTER_SUCCESS.getMessage(), response);
    }
}
