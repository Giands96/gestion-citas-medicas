package com.gestion.auth.auth_service.auth.service;

import com.gestion.auth.auth_service.auth.dto.CrearCredencialRequest;
import com.gestion.auth.auth_service.auth.dto.CredencialResponse;
import com.gestion.auth.auth_service.auth.dto.LoginRequest;
import com.gestion.auth.auth_service.auth.dto.LoginResponse;
import com.gestion.auth.auth_service.credential.entity.Credencial;
import com.gestion.auth.auth_service.credential.repository.CredencialRepository;
import com.gestion.auth.auth_service.exceptions.EmailAlreadyExists;
import com.gestion.auth.auth_service.exceptions.GlobalExceptionHandler;
import com.gestion.auth.auth_service.role.entity.Rol;
import com.gestion.auth.auth_service.role.repository.RolRepository;
import com.gestion.auth.auth_service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CredencialRepository credencialRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request){
        Credencial credencial = credencialRepository.findByCorreo(request.correo()).orElseThrow(() -> new BadCredentialsException("Credenciales invalidas"));

        if(!credencial.getActivo()){
            throw new BadCredentialsException("La cuenta no está inhabilitada");
        }
        boolean passwordValido = passwordEncoder.matches(request.password(), credencial.getPassword());

        if(!passwordValido) {
            throw new BadCredentialsException("Credenciales inválidas");
        }
        String token = jwtService.generateToken(credencial);

        return new LoginResponse(token, "Bearer", credencial.getUserId(), credencial.getCorreo(), credencial.getRol().getNombre());

    }

    public CredencialResponse createCredential(CrearCredencialRequest request) {
        if (credencialRepository.existsByCorreo(request.correo())) {
            throw new EmailAlreadyExists("Ya existe este usuario");
        }

        if (credencialRepository.existsByUserId(request.userId())) {
            throw new IllegalArgumentException("El usuario ya tiene credenciales registradas");
        }

        Rol role = rolRepository.findByNombre(request.rol())
                .orElseThrow(() -> new IllegalArgumentException("El rol no existe"));

        Credencial credential = Credencial.builder()
                .userId(request.userId())
                .correo(request.correo())
                .password(passwordEncoder.encode(request.password()))
                .rol(role)
                .activo(true)
                .createdAt(LocalDateTime.now())
                .build();

        Credencial savedCredential = credencialRepository.save(credential);

        return new CredencialResponse(
                savedCredential.getId(),
                savedCredential.getUserId(),
                savedCredential.getCorreo(),
                savedCredential.getRol().getNombre(),
                savedCredential.getActivo()
        );
    }


}
