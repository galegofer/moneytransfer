package com.tribalscale.test.handler;

import com.tribalscale.test.domain.MoneyTransferApplicationException;
import com.tribalscale.test.domain.payload.ErrorPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MoneyTransferApplicationException.class)
    @ResponseStatus
    public ResponseEntity<?> handleApplicationException(final MoneyTransferApplicationException exception) {
        log.error("Received application exception: {}, with message: {}", exception.getClass().getName(), exception.getMessage());
        log.debug("Error debug: ", exception);

        return ResponseEntity.status(exception.getStatusCode())
                .body(ErrorPayload.builder()
                        .code(exception.getStatusCode())
                        .message(exception.getStatusCode() == NOT_FOUND.value()
                                ? "Not found"
                                : "Application error while trying to access to the provided operation")
                        .build());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus
    public ResponseEntity<?> handleException(final Exception exception) {
        log.error("Received application exception: {}, with message: {}", exception.getClass().getName(), exception.getMessage());
        log.debug("Error debug: ", exception);

        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(ErrorPayload.builder()
                        .code(INTERNAL_SERVER_ERROR.value())
                        .message("Generic error while trying to access to the provided operation")
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus
    public ResponseEntity<?> handleConstraintException(final MethodArgumentNotValidException exception) {
        log.error("Received application constraint exception: {}, with message: {}", exception.getClass().getName(), exception.getMessage());
        log.debug("Error debug: ", exception);

        return ResponseEntity.status(BAD_REQUEST)
                .body(ErrorPayload.builder()
                        .code(BAD_REQUEST.value())
                        .message("Error while validating input parameters")
                        .build());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus
    public ResponseEntity<?> handleConstraintException(final ConstraintViolationException exception) {
        log.error("Received application constraint exception: {}, with message: {}", exception.getClass().getName(), exception.getMessage());
        log.debug("Error debug: ", exception);

        return ResponseEntity.status(BAD_REQUEST)
                .body(ErrorPayload.builder()
                        .code(BAD_REQUEST.value())
                        .message("Error while validating input parameters")
                        .build());
    }
}
