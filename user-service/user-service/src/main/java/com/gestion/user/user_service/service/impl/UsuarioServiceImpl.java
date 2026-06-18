package com.gestion.user.user_service.service.impl;

import com.gestion.user.user_service.credential.CredencialEntity;
import com.gestion.user.user_service.credential.CredencialRepository;
import com.gestion.user.user_service.credential.RolEntity;
import com.gestion.user.user_service.credential.RolRepository;
import com.gestion.user.user_service.dto.request.UsuarioRequest;
import com.gestion.user.user_service.dto.response.UsuarioResponse;
import com.gestion.user.user_service.entity.Usuario;
import com.gestion.user.user_service.exception.DuplicateResourceException;
import com.gestion.user.user_service.exception.ResourceNotFoundException;
import com.gestion.user.user_service.repository.UsuarioRepository;
import com.gestion.user.user_service.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository repository;
    private final CredencialRepository credencialRepository;
    private final RolRepository rolRepository;
    private final ModelMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UsuarioResponse crearUsuario(UsuarioRequest request) {
        if (credencialRepository.existsByCorreo(request.getCorreo())) {
            throw new DuplicateResourceException("El correo ya está registrado");
        }

        Usuario usuario = mapper.map(request, Usuario.class);
        Usuario guardado = repository.save(usuario);

        RolEntity rol = rolRepository.findByNombre(request.getRol().name())
                .orElseThrow(() -> new IllegalArgumentException("El rol no existe: " + request.getRol()));

        CredencialEntity credencial = CredencialEntity.builder()
                .userId(guardado.getId())
                .correo(request.getCorreo())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(rol)
                .activo(true)
                .build();

        credencialRepository.save(credencial);

        log.info("Usuario {} creado con credenciales", guardado.getId());

        return mapper.map(guardado, UsuarioResponse.class);
    }

    @Override
    public UsuarioResponse obtenerPorId(Long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        return mapper.map(usuario, UsuarioResponse.class);
    }

    @Override
    public List<UsuarioResponse> listarTodos() {
        List<Usuario> usuarios = repository.findAll();
        return usuarios.stream()
                .map(usuario -> mapper.map(usuario, UsuarioResponse.class))
                .toList();
    }

    @Override
    public UsuarioResponse actualizarUsuario(Long id, UsuarioRequest request) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        mapper.map(request, usuario);
        Usuario updated = repository.save(usuario);
        return  mapper.map(updated, UsuarioResponse.class);
    }

    @Override
    public void eliminarUsuario(Long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        usuario.setActivo(false);
        repository.save(usuario);
    }
}
