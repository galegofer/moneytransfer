package com.tribalscale.test.handler

import com.tribalscale.test.domain.MoneyTransferApplicationException
import com.tribalscale.test.domain.payload.ErrorPayload
import mu.KotlinLogging
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import javax.validation.ConstraintViolationException

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = KotlinLogging.logger {}

    @ExceptionHandler(MoneyTransferApplicationException::class)
    @ResponseStatus
    fun handleApplicationException(exception: MoneyTransferApplicationException): ResponseEntity<*> =
        log.error(
            "Received application exception: {}, with message: {}",
            exception.javaClass.name,
            exception.message
        ).also { log.debug("Error debug: ", exception) }
            .let {
                ResponseEntity.status(exception.statusCode)
                    .body(
                        ErrorPayload(
                            code = exception.statusCode,
                            message = if (exception.statusCode == NOT_FOUND.value()) "Not found"
                            else "Application error while trying to access to the provided operation"
                        )
                    )
            }

    @ExceptionHandler(Exception::class)
    @ResponseStatus
    fun handleException(exception: Exception): ResponseEntity<*> =
        log.error(
            "Received application exception: {}, with message: {}",
            exception.javaClass.name,
            exception.message
        ).also { log.debug("Error debug: ", exception) }
            .let {
                ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(
                        ErrorPayload(
                            code = INTERNAL_SERVER_ERROR.value(),
                            message = "Generic error while trying to access to the provided operation"
                        )
                    )
            }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus
    fun handleConstraintException(exception: MethodArgumentNotValidException): ResponseEntity<*> =
        log.error(
            "Received application constraint exception: {}, with message: {}",
            exception.javaClass.name,
            exception.message
        ).also { log.debug("Error debug: ", exception) }
            .let {
                ResponseEntity.status(BAD_REQUEST)
                    .body(
                        ErrorPayload(
                            code = BAD_REQUEST.value(),
                            message = "Error while validating input parameters"
                        )
                    )
            }

    @ExceptionHandler(ConstraintViolationException::class)
    @ResponseStatus
    fun handleConstraintException(exception: ConstraintViolationException): ResponseEntity<*> =
        log.error(
            "Received application constraint exception: {}, with message: {}",
            exception.javaClass.name,
            exception.message
        ).also { log.debug("Error debug: ", exception) }
            .let {
                ResponseEntity.status(BAD_REQUEST)
                    .body(
                        ErrorPayload(
                            code = BAD_REQUEST.value(),
                            message = "Error while validating input parameters"
                        )
                    )
            }
}