package jcn.yourorder.authgateway.common.exception;

import jcn.yourorder.authgateway.common.exception.exceptions.BadRequestException;
import jcn.yourorder.authgateway.common.exception.exceptions.ForbiddenException;
import jcn.yourorder.authgateway.common.exception.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            NotFoundException ex,
            ServerWebExchange exchange
    ) {
        return buildResponse(HttpStatus.NOT_FOUND, ex, exchange);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
            BadRequestException ex,
            ServerWebExchange exchange
    ) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex, exchange);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(
            ForbiddenException ex,
            ServerWebExchange exchange
    ) {
        return buildResponse(HttpStatus.FORBIDDEN, ex, exchange);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex,
            ServerWebExchange exchange
    ) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex, exchange);
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            Exception ex,
            ServerWebExchange exchange
    ) {
        return ResponseEntity.status(status).body(
                new ErrorResponse(
                        status.value(),
                        status.getReasonPhrase(),
                        ex.getMessage(),
                        exchange.getRequest().getPath().value(),
                        Instant.now()
                )
        );
    }
}