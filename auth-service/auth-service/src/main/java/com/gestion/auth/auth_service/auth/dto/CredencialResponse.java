package com.gestion.auth.auth_service.auth.dto;

public record CredencialResponse(
        Long id,
        Long userId,
        String correo,
        String rol,
        Boolean activo
) {
}