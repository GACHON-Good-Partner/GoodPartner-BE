package goodpartner.global.auth.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {

    INVALID_TOKEN(401,"유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(401, "만료된 토큰입니다."),
    UNAUTHORIZED(401,"인증정보가 존재하지 않습니다.");

    private final int code;
    private final String message;
}

