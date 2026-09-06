package com.ecommerce.ecommerce_api.infra.handler;

import com.ecommerce.ecommerce_api.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<BadRequestExceptionDetails> resourceNotFoundException(ResourceNotFoundException exception) {
        BadRequestExceptionDetails badRequestExceptionDetails = BadRequestExceptionDetails.builder()
                .message(exception.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .developer_message("Se vira ai cara - Dev Ivan")
                .build();
        return new ResponseEntity<>(badRequestExceptionDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({BadRequestException.class, InsufficientStockException.class})
    public ResponseEntity<BadRequestExceptionDetails> handleBadRequest(Exception exception) {
        BadRequestExceptionDetails badRequestExceptionDetails = BadRequestExceptionDetails.builder()
                .message(exception.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .developer_message("Se vira ai cara - Dev Ivan")
                .build();
        return new ResponseEntity<>(badRequestExceptionDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationExceptionDetails> methodArgumentNotValidException(MethodArgumentNotValidException exception) {
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        String fields = fieldErrors.stream().map(FieldError::getField).collect(Collectors.joining(","));
        String fieldsMessage = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(","));

        return new ResponseEntity<>(ValidationExceptionDetails.builder()
                .message(exception.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .fields(fields)
                .fieldsMessage(fieldsMessage)
                .title("Bad Request Exception, Invalid Fields")
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .developer_message("Se vira ai cara - Dev Ivan")
                .build()
        , HttpStatus.BAD_REQUEST);
    }
}
