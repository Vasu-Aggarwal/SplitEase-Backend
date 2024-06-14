package com.vr.SplitEase.exception;

import com.vr.SplitEase.dto.response.BadApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

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
