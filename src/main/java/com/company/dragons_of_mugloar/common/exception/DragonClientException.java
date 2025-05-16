package com.company.dragons_of_mugloar.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DragonClientException extends RuntimeException {

    private final HttpStatus status;

    protected DragonClientException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }

}
