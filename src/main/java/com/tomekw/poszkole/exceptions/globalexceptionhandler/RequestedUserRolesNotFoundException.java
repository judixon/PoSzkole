package com.tomekw.poszkole.exceptions.globalexceptionhandler;

public class RequestedUserRolesNotFoundException extends RuntimeException{
    public RequestedUserRolesNotFoundException(String message) {
        super(message);
    }
}
