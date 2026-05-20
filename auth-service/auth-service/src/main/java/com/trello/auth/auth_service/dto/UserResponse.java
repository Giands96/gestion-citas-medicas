package com.trello.auth.auth_service.dto;

import com.trello.auth.auth_service.entity.Role;

public record UserResponse(

        Long id,
        String nombres,
        String apellidos,
        String email,
        Role rol


) {
}
