package com.gestion.user.user_service.client.dto;

public record CredencialRequest(
        Long userId,
        String correo,
        String password,
        String rol
) {
}
