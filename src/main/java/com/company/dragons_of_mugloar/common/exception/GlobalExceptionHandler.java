package com.company.dragons_of_mugloar.common.exception;

import com.company.dragons_of_mugloar.common.model.CustomErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DragonClientException.class)
    public ResponseEntity<CustomErrorResponse> handleHttpClientError(DragonClientException ex) {

        CustomErrorResponse error = CustomErrorResponse.builder()
                .status(ex.getStatus().value())
                .message(ex.getMessage())
                .build();

        log.error("Client exception: {}", error);

        return new ResponseEntity<>(error, HttpStatusCode.valueOf(ex.getStatus().value()));
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<CustomErrorResponse> genericHandler(Exception e) {
        log.error("Unknown error occurred", e);
        CustomErrorResponse response = CustomErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Generic server error: " + e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

}
