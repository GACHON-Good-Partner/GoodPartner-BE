package goodpartner.user.controller;


import goodpartner.global.common.response.ResponseDto;
import goodpartner.user.application.UserUsecase;
import goodpartner.user.application.dto.request.SocialLoginRequest;
import goodpartner.user.application.dto.request.UserUpdateRequest;
import goodpartner.user.application.dto.response.SocialLoginResponse;
import goodpartner.user.application.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static goodpartner.user.controller.ResponseMessage.*;
import static goodpartner.user.entity.enums.LoginStatus.LOGIN;
import static org.springframework.http.HttpStatus.OK;

@Tag(name = "USER")
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

    @GetMapping
    @Operation(summary = "회원 정보 조회")
    public ResponseDto<UserResponse> get(@AuthenticationPrincipal String userId) {
        UserResponse response = userUsecase.getUser(userId);
        return ResponseDto.response(OK.value(), GET_MY_INFO.getMessage(), response);
    }

    @PatchMapping
    @Operation(summary = "회원 정보 수정")
    public ResponseDto<String> update(@RequestBody @Valid UserUpdateRequest dto, @AuthenticationPrincipal String userId) {
        userUsecase.update(dto, userId);
        return ResponseDto.response(OK.value(), UPDATE_MY_INFO.getMessage());
    }

    @GetMapping("/health-check")
    public ResponseEntity<String> healthCheck(){
        return ResponseEntity.ok("health-check");
    }
}
