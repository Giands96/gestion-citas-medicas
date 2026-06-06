package com.trello.auth.auth_service.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("El correo electrónico '" + email + "' ya está registrado.");
    }
}
