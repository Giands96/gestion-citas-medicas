package com.gestion.auth.auth_service.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice //* Indica que esta clase se encargará de manejar excepciones a nivel global en los controladores REST.
public class GlobalExceptionHandler {

    @ExceptionHandler
// ex es la excepion
    //* Request es la solicitud http que se lanzó al servidor
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(EmailAlreadyExists ex, HttpServletRequest  request) {

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                400,
                System.currentTimeMillis(),
                request.getRequestURI(),
                "Bad Request"
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                "Ocurrió un error interno en el servidor",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                System.currentTimeMillis(),
                request.getRequestURI(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

}
