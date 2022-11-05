package com.tomekw.poszkole.exceptions;

public class PaymentAlreadyExistsException extends RuntimeException{

    public PaymentAlreadyExistsException(String message) {
        super(message);
    }
}
