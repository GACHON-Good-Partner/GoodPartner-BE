package goodpartner.user.application.exception;


import goodpartner.global.common.exception.BaseException;

public class UserNotFoundException extends BaseException {
    public UserNotFoundException() {
        super(404, "사용자가 존재하지 않습니다.");
    }
}
