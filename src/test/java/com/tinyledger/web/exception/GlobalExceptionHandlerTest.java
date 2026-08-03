package com.tinyledger.web.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.tinyledger.exception.BadRequestException;
import com.tinyledger.web.dto.TransactionRequest;
import com.tinyledger.web.dto.TransactionTypeDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldReturnBadRequestWithExceptionMessage() {
        BadRequestException exception = new BadRequestException("There isn't enough balance to withdraw");

        ResponseEntity<ErrorResponse> response = handler.handleBadRequest(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("There isn't enough balance to withdraw", response.getBody().message());
    }

    @Test
    void shouldReturnAllowedValuesWhenEnumFieldIsInvalid() {
        InvalidFormatException invalidFormatException = new InvalidFormatException("not a valid enum value", "SOME-WRONG-TYPE", TransactionTypeDto.class);
        invalidFormatException.prependPath(TransactionRequest.class, "transactionType");
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("message", invalidFormatException);

        ResponseEntity<ErrorResponse> response = handler.handleUnreadableBody(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid value for transactionType. Allowed values: [WITHDRAWAL, DEPOSIT]", response.getBody().message());
    }

    @Test
    void shouldReturnGenericMessageWhenBodyIsMalformedForOtherReasons() {
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("message", new RuntimeException("unexpected!!!"));

        ResponseEntity<ErrorResponse> response = handler.handleUnreadableBody(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Malformed request body", response.getBody().message());
    }
}
