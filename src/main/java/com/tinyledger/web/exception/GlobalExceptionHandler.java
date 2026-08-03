package com.tinyledger.web.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.tinyledger.exception.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
                .findFirst()
                .orElse("Invalid request");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableBody(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException invalidFormatException && invalidFormatException.getTargetType().isEnum()) {
            String field = invalidFormatException.getPath().isEmpty() ? "field" : invalidFormatException.getPath().get(0).getFieldName();
            String allowedValues = Arrays.toString(invalidFormatException.getTargetType().getEnumConstants());
            String message = "Invalid value for " + field + ". Allowed values: " + allowedValues;

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(message));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Malformed request body"));
    }
}
