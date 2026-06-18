package com.gestion.auth.auth_service.exceptions;

public record ErrorResponse(
        String message,
        int status,
        long timestamp,
        String path,
        String error
) {
}
