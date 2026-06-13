package com.z.bookcreat.common;

import jakarta.validation.ConstraintViolationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<Void> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ApiResponse.fail(ex.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ApiResponse<Void> handleValidationException(Exception ex) {
        String message = "参数校验失败";
        if (ex instanceof MethodArgumentNotValidException validException
                && validException.getBindingResult().hasFieldErrors()) {
            message = validException.getBindingResult().getFieldErrors().getFirst().getDefaultMessage();
        }
        if (ex instanceof BindException bindException && bindException.getBindingResult().hasFieldErrors()) {
            message = bindException.getBindingResult().getFieldErrors().getFirst().getDefaultMessage();
        }
        return ApiResponse.fail(message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResponse<Void> handleConstraintViolationException(ConstraintViolationException ex) {
        return ApiResponse.fail(ex.getMessage());
    }
}
