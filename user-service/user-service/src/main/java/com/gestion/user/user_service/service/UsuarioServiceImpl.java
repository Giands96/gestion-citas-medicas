package com.gestion.user.user_service.service;

import com.gestion.user.user_service.dto.UsuarioDto;
import com.gestion.user.user_service.entity.Usuario;
import com.gestion.user.user_service.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public List<UsuarioDto> getAllUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioDto getUsuarioById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
        return toDto(usuario);
    }

    @Override
    public UsuarioDto createUsuario(UsuarioDto dto) {
        Usuario usuario = toEntity(dto);
        usuario.setActivo(true);
        usuario.setCreatedAt(LocalDateTime.now());
        Usuario saved = usuarioRepository.save(usuario);
        return toDto(saved);
    }

    @Override
    public UsuarioDto updateUsuario(Long id, UsuarioDto dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
        usuario.setUsuarioId(dto.getUsuarioId());
        usuario.setDni(dto.getDni());
        usuario.setFechaNacimiento(dto.getFechaNacimiento());
        usuario.setDireccion(dto.getDireccion());
        usuario.setActivo(dto.getActivo());
        usuario.setUpdatedAt(LocalDateTime.now());
        Usuario updated = usuarioRepository.save(usuario);
        return toDto(updated);
    }

    @Override
    public void deleteUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
        usuarioRepository.delete(usuario);
    }

    private UsuarioDto toDto(Usuario u) {
        UsuarioDto dto = new UsuarioDto();
        dto.setId(u.getId());
        dto.setUsuarioId(u.getUsuarioId());
        dto.setDni(u.getDni());
        dto.setFechaNacimiento(u.getFechaNacimiento());
        dto.setDireccion(u.getDireccion());
        dto.setActivo(u.getActivo());
        dto.setCreatedAt(u.getCreatedAt());
        dto.setUpdatedAt(u.getUpdatedAt());
        return dto;
    }

    private Usuario toEntity(UsuarioDto dto) {
        Usuario u = new Usuario();
        u.setUsuarioId(dto.getUsuarioId());
        u.setDni(dto.getDni());
        u.setFechaNacimiento(dto.getFechaNacimiento());
        u.setDireccion(dto.getDireccion());
        return u;
    }
}