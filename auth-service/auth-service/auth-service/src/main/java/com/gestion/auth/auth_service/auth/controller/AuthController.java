package com.gestion.auth.auth_service.auth.controller;

import com.gestion.auth.auth_service.auth.dto.CrearCredencialRequest;
import com.gestion.auth.auth_service.auth.dto.CredencialResponse;
import com.gestion.auth.auth_service.auth.dto.LoginRequest;
import com.gestion.auth.auth_service.auth.dto.LoginResponse;
import com.gestion.auth.auth_service.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/credentials")
    public ResponseEntity<CredencialResponse> createCredential(
            @Valid @RequestBody CrearCredencialRequest request
    ) {
        CredencialResponse response = authService.createCredential(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
