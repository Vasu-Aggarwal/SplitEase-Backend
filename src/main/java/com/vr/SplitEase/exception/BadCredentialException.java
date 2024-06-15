package com.vr.SplitEase.exception;

public class BadCredentialException extends RuntimeException{
    public BadCredentialException(){
        super("Incorrect Password");
    }
}
