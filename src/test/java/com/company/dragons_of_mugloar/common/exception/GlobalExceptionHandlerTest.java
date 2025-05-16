package com.company.dragons_of_mugloar.common.exception;

import com.company.dragons_of_mugloar.common.model.CustomErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    @Test
    void shouldVerifyGameClientExceptionHandlerWorks() {
        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

        GameClientException exception = new GameClientException("Service is down", new RuntimeException());

        ResponseEntity<CustomErrorResponse> responseEntity = globalExceptionHandler.handleHttpClientError(exception);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, responseEntity.getStatusCode());
        assertEquals("Service is down", responseEntity.getBody().getMessage());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), responseEntity.getBody().getStatus());
    }

    @Test
    void shouldVerifyClientExceptionHandlerWorks() {
        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

        ClientException exception = new ClientException("Resource not found", new RuntimeException());

        ResponseEntity<CustomErrorResponse> responseEntity = globalExceptionHandler.handleHttpClientError(exception);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Resource not found", responseEntity.getBody().getMessage());
        assertEquals(HttpStatus.NOT_FOUND.value(), responseEntity.getBody().getStatus());
    }

    @Test
    void shouldVerifyGenericHandlerWorks() {
        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
        Exception exception = new Exception("Unexpected crash");

        ResponseEntity<CustomErrorResponse> responseEntity = globalExceptionHandler.genericHandler(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Generic server error: Unexpected crash", responseEntity.getBody().getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), responseEntity.getBody().getStatus());
    }
}
