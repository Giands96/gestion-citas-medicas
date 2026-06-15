package com.gestion.user.user_service.client.dto;

public record CredencialResponse(
        Long id,
        Long userId,
        String correo,
        String rol,
        Boolean activo
) {
}
