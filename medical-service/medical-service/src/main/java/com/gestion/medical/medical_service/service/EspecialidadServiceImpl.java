package com.gestion.medical.medical_service.service;

import com.gestion.medical.medical_service.dto.EspecialidadRequest;
import com.gestion.medical.medical_service.dto.EspecialidadResponse;
import com.gestion.medical.medical_service.entity.Especialidad;
import com.gestion.medical.medical_service.exception.DuplicateResourceException;
import com.gestion.medical.medical_service.exception.ResourceNotFoundException;
import com.gestion.medical.medical_service.repository.EspecialidadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EspecialidadServiceImpl implements EspecialidadService {
    private final EspecialidadRepository repository;

    @Override
    public EspecialidadResponse crear(EspecialidadRequest request) {
        if (repository.existsByNombre(request.getNombre())) {
            throw new DuplicateResourceException("Ya existe una especialidad con ese nombre");
        }
        Especialidad entity = Especialidad.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .activa(true)
                .build();
        return toResponse(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public EspecialidadResponse obtenerPorId(Long id) {
        Especialidad entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada"));
        return toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EspecialidadResponse> listarTodas() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EspecialidadResponse actualizar(Long id, EspecialidadRequest request) {
        Especialidad entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada"));
        entity.setNombre(request.getNombre());
        entity.setDescripcion(request.getDescripcion());
        return toResponse(repository.save(entity));
    }

    @Override
    public void eliminar(Long id) {
        Especialidad entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada"));
        entity.setActiva(false);
        repository.save(entity);
    }

    private EspecialidadResponse toResponse(Especialidad e) {
        return EspecialidadResponse.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .descripcion(e.getDescripcion())
                .activa(e.getActiva())
                .createdAt(e.getCreatedAt())
                .build();
    }
}
