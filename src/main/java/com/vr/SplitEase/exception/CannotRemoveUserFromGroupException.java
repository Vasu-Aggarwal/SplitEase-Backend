package com.vr.SplitEase.exception;

public class CannotRemoveUserFromGroupException extends RuntimeException{
    String message;
    Integer status;
    Double netBalance;

    public CannotRemoveUserFromGroupException(String message, Integer status, Double netBalance){
        super(message);
        this.message = message;
        this.status = status;
        this.netBalance = netBalance;
    }
}
