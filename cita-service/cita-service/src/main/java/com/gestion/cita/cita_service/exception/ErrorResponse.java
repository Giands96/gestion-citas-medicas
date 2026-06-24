package com.gestion.cita.cita_service.exception;

public record ErrorResponse(
        String message,
        int status,
        long timestamp,
        String path,
        String error
) {
}
