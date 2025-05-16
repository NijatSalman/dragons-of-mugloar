package com.company.dragons_of_mugloar.common.exception;

import org.springframework.http.HttpStatus;

public class GameClientException extends DragonClientException{
    public GameClientException(String msg, Throwable cause) {
        super(msg, cause, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
