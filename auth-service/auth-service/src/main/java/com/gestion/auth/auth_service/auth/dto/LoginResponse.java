package com.gestion.auth.auth_service.auth.dto;

public record LoginResponse(
        String token,
        String tipo,
        Long userId,
        String correo,
        String rol
) {
}