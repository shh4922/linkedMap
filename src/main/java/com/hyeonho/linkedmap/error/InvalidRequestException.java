package com.hyeonho.linkedmap.error;

public class InvalidRequestException extends RuntimeException{
    public InvalidRequestException(String msg) {
        super(msg);
    }

}
