package goodpartner.global.common.exception;

import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {

    private final int errorCode;

    public BaseException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}