package com.vr.SplitEase.exception;

import com.vr.SplitEase.dto.response.BadApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

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

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<BadApiResponse> duplicateEntry(DataIntegrityViolationException ex){
        String message = ex.getMessage();
        BadApiResponse badApiResponse = new BadApiResponse(message, 0);
        return new ResponseEntity<>(badApiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialException.class)
    public ResponseEntity<BadApiResponse> badCredentials(BadCredentialException ex){
        String message = ex.getMessage();
        BadApiResponse badApiResponse = new BadApiResponse(message, 0);
        return new ResponseEntity<>(badApiResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<BadApiResponse> authenticationException(AuthenticationException ex){
        String message = ex.getMessage();
        BadApiResponse badApiResponse = new BadApiResponse(message, 0);
        return new ResponseEntity<>(badApiResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(CannotRemoveUserFromGroupException.class)
    public ResponseEntity<Map<String, Object>> cannotRemoveUserFromGroup(CannotRemoveUserFromGroupException cannotRemoveUserFromGroupException){
        String message = cannotRemoveUserFromGroupException.message;
        Integer status = cannotRemoveUserFromGroupException.status;
        Double netBal = cannotRemoveUserFromGroupException.netBalance;
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("status", status);
        response.put("netBalance", netBal);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
