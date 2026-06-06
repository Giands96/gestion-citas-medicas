package com.trello.auth.auth_service.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Credenciales inválidas. Por favor, verifica tus datos.");
    }
}
