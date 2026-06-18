package com.gestion.auth.auth_service.exceptions;

public class EmailAlreadyExists extends RuntimeException {
    public EmailAlreadyExists(String message) {
        super(message);
    }
}
