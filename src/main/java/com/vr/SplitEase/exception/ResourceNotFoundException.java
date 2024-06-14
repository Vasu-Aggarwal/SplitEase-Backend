package com.vr.SplitEase.exception;

public class ResourceNotFoundException extends RuntimeException{
    String message;

    public ResourceNotFoundException(String message){
        super(message);
        this.message = message;
    }
}
