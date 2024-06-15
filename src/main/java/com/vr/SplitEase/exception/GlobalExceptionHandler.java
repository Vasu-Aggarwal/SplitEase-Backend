package com.vr.SplitEase.exception;

import com.vr.SplitEase.dto.response.BadApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BadApiResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException methodArgumentNotValidException){
        BadApiResponse badApiResponse = new BadApiResponse();
        methodArgumentNotValidException.getBindingResult().getAllErrors().forEach((error) -> {
            String message = error.getDefaultMessage();
            assert message != null;
            badApiResponse.setMessage(String.format(message));
            badApiResponse.setStatus(0);
        });
        return new ResponseEntity<>(badApiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<BadApiResponse> handleResourceNotFoundException(ResourceNotFoundException resourceNotFoundException){
        String message = resourceNotFoundException.getMessage();
        BadApiResponse badApiResponse = new BadApiResponse(message, 0);
        return new ResponseEntity<>(badApiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadApiRequestException.class)
    public ResponseEntity<BadApiResponse> handleBadApiRequestException(BadApiRequestException badApiRequestException){
        String message = badApiRequestException.getMessage();
        BadApiResponse badApiResponse = new BadApiResponse(message, 0);
        return new ResponseEntity<>(badApiResponse, HttpStatus.BAD_REQUEST);
    }

}
