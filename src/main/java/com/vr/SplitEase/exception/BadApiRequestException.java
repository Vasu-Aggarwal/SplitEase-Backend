package com.vr.SplitEase.exception;

public class BadApiRequestException extends RuntimeException{

    String message;

    public BadApiRequestException(String message){
        super(message);
        this.message = message;
    }
}
