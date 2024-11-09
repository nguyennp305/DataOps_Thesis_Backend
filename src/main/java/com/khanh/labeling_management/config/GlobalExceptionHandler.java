package com.khanh.labeling_management.config;

import com.khanh.labeling_management.model.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<BaseResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        BaseResponse<String> baseResponse = new BaseResponse<>();
        baseResponse.badRequest(String.join("\n", errors.values()));
        return new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({BindException.class})
    public ResponseEntity<BaseResponse> handleBindException(BindException bindException) {
        Map<String, String> errors = new HashMap<>();
        bindException.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        BaseResponse<String> baseResponse = new BaseResponse<>();
        baseResponse.badRequest(String.join("\n", errors.values()));
        return new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler({Exception.class})
    public ResponseEntity<Map<String, String>> handleSystemException(Exception e) {
        //  Logging
        e.printStackTrace();
        Map<String, String> errors = new HashMap<>();

        if (e instanceof AccessDeniedException) {
            errors.put("error", "Bạn không có thẩm quyền.");
            return new ResponseEntity<>(errors, HttpStatus.UNAUTHORIZED);
        }

        if (e instanceof BadCredentialsException) {
            errors.put("error", "Tên đăng nhập hoặc mật khẩu không đúng.");
            return new ResponseEntity<>(errors, HttpStatus.UNAUTHORIZED);
        }

        errors.put("error", "Invalid request.");
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

}
