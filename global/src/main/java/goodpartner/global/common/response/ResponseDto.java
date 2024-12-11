package goodpartner.global.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto<T> {

    private int code;
    private String message;
    private T data;

    // 실패시 호출(데이터가 비어있음)
    public static <T> ResponseDto<T> errorResponse(int code, String message) {
        return new ResponseDto<>(code, message, null);
    }

    // data가 없는 응답시 호출(성공)
    public static <T> ResponseDto<T> response(int code, String message) {
        return new ResponseDto<>(code, message, null);
    }

    // 성공시 호출(데이터 포함)
    public static <T> ResponseDto<T> response(int code, String message, T data) {
        return new ResponseDto<>(code, message, data);
    }

}
