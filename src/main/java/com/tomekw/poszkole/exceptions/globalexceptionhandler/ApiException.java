package com.tomekw.poszkole.exceptions.globalexceptionhandler;

import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

public record ApiException(String message,
                           HttpStatus responseHttpStatus, ZonedDateTime timestamp) {
}
