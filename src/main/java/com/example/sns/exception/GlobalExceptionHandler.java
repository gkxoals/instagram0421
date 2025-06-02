package com.example.sns.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 *   GlobalExceptionHandler 클래스
 * - 애플리케이션 전반에서 발생하는 예외를 전역적으로 처리하는 역할을 담당
 * - @ControllerAdvice를 사용하여 전역 예외 처리 기능을 제공
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     *  ConstraintViolationException 예외 처리
     * - @Valid 또는 @Validated 검증에서 발생하는 예외를 처리
     * - 유효성 검사 실패 시 400 Bad Request 응답을 반환
     *
     * @param ex ConstraintViolationException 예외 객체
     * @return 400 Bad Request 응답과 예외 메시지를 포함한 ResponseEntity
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleValidationException(ConstraintViolationException ex) {
        // 사용자에게 보여줄 첫 번째 메시지만 추출
        String message = ex.getConstraintViolations().stream()
                .map(violation -> violation.getMessage())
                .findFirst()
                .orElse("입력값이 올바르지 않습니다.");
        return ResponseEntity.badRequest().body(message);
    }

    /**
     *   IllegalArgumentException 예외 처리
     * - 잘못된 인자 값이 전달될 때 발생하는 예외 처리
     * - 예를 들어, 파일 업로드 시 허용되지 않은 확장자를 업로드할 경우 예외 발생 가능
     * - 예외 메시지를 그대로 반환하여 클라이언트가 알 수 있도록 함
     *
     * @param ex IllegalArgumentException 예외 객체
     * @return 400 Bad Request 응답과 예외 메시지를 포함한 ResponseEntity
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
