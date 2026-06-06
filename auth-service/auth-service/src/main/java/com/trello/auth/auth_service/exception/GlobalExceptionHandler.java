package com.trello.auth.auth_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

//RestController Advice para manejar excepciones globalmente en la aplicación
@RestControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {
    // Manejar la excepción personalizada de correo electrónico ya registrado
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(
            EmailAlreadyExistsException ex,
            HttpServletRequest request
    ) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage(), request, null);
    }

    // Manejar excepciones relacionadas con credenciales inválidas
    @ExceptionHandler({InvalidCredentialsException.class, BadCredentialsException.class})
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(
            RuntimeException ex,
            HttpServletRequest request
    ) {
        return buildError(HttpStatus.UNAUTHORIZED, "Credenciales inválidas", request, null);
    }

    // Manejar errores de validación de argumentos en las solicitudes
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return buildError(HttpStatus.BAD_REQUEST, "Error de validación", request, errors);
    }

    // Manejar cualquier otra excepción no controlada
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", request, null);
    }


    // Método auxiliar para construir la respuesta de error de manera consistentejm k
    private ResponseEntity<ErrorResponse> buildError(
            HttpStatus estado,
            String mensaje,
            HttpServletRequest request,
            Map<String, String> ErroresValidacion
    ) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                estado.value(),
                mensaje,
                request.getRequestURI(),
                ErroresValidacion
        );

        return ResponseEntity.status(estado).body(response);
    }
}
