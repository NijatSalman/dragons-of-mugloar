package com.company.dragons_of_mugloar.common.exception;

import org.springframework.http.HttpStatus;

public class ClientException extends DragonClientException {
    public ClientException(String msg, Throwable cause) {
        super(msg, cause, HttpStatus.NOT_FOUND);
    }
}
