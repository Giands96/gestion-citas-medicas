package com.trello.auth.auth_service.dto;

public record AuthResponse(
        String token,
        String tokenType,
        UserResponse user
) {
}