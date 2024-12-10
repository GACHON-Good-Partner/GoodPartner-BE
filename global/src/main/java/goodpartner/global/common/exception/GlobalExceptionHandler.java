package goodpartner.global.common.exception;

import goodpartner.global.common.response.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String LOG_FORMAT = "Class : {}, Code : {}, Message : {}";

    // 사용자 정의 예외 발생 시
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ResponseDto<Void>> handle(BaseException e) {

        // 개발 용이성을 위해 전체 에러로그를 재출력. 운영 환경에서는 제거할 것  
        log.warn(e.getMessage(), e);
        // 간략한 예외 정보 출력  
        log.warn(LOG_FORMAT, e.getClass().getSimpleName(), e.getErrorCode(), e.getMessage());

        ResponseDto<Void> response = ResponseDto.errorResponse(e.getErrorCode(), e.getMessage());

        return ResponseEntity
                .status(e.getErrorCode()) // 실제 HTTP 상태 코드 설정  
                .body(response); // 본문에 ResponseDto 전달  
    }

    // 파라미터가 없을 시
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResponseDto<Void>> handle(MissingServletRequestParameterException e) {
        int statusCode = 400;

        log.warn(e.getMessage(), e);
        log.warn(LOG_FORMAT, e.getClass().getSimpleName(), statusCode, e.getMessage());

        ResponseDto<Void> response = ResponseDto.errorResponse(statusCode, e.getMessage());

        return ResponseEntity
                .status(statusCode)
                .body(response);
    }

    // @Valid에서 발생한 예외
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<Void>> handle(MethodArgumentNotValidException e) {
        int statusCode = 400;

        log.warn(e.getMessage(), e);
        log.warn(LOG_FORMAT, e.getClass().getSimpleName(), statusCode, e.getMessage());

        ResponseDto<Void> response = ResponseDto.errorResponse(statusCode, e.getMessage());

        return ResponseEntity
                .status(statusCode)
                .body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseDto<Void>> handle(IllegalArgumentException e) {
        int statusCode = 400;

        log.warn(e.getMessage(), e);
        log.warn(LOG_FORMAT, e.getClass().getSimpleName(), statusCode, e.getMessage());

        ResponseDto<Void> response = ResponseDto.errorResponse(statusCode, e.getMessage());

        return ResponseEntity
                .status(statusCode) // 실제 HTTP 상태 코드 설정
                .body(response);
    }

    // 소셜 로그인 중 발생할 수 있는 예외.
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ResponseDto<Void>> handle(HttpClientErrorException e) {
        int statusCode = 400;

        if(e instanceof ErrorResponse){
            statusCode = ((ErrorResponse) e).getStatusCode().value();
        }

        log.warn(e.getMessage(), e);
        log.warn(LOG_FORMAT, e.getClass().getSimpleName(), statusCode, e.getMessage());

        ResponseDto<Void> response = ResponseDto.errorResponse(statusCode, e.getMessage());

        return ResponseEntity
                .status(statusCode) // 실제 HTTP 상태 코드 설정
                .body(response);
    }

    // 잡지 못한 이외의 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<Void>> handle(Exception e) {
        int statusCode = 500;

        log.warn(e.getMessage(), e);
        log.warn(LOG_FORMAT, e.getClass().getSimpleName(), statusCode, e.getMessage());

        ResponseDto<Void> response = ResponseDto.errorResponse(statusCode, e.getMessage());

        return ResponseEntity
                .status(statusCode) // 실제 HTTP 상태 코드 설정
                .body(response);
    }

}