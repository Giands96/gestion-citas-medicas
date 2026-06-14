package com.gestion.medical.medical_service.service;

import com.gestion.medical.medical_service.dto.EspecialidadDto;
import com.gestion.medical.medical_service.entity.Especialidad;
import com.gestion.medical.medical_service.repository.EspecialidadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EspecialidadServiceImpl implements EspecialidadService {

    private final EspecialidadRepository especialidadRepository;

    @Override
    public List<EspecialidadDto> getAllEspecialidades() {
        return especialidadRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public EspecialidadDto getEspecialidadById(Long id) {
        Especialidad especialidad = especialidadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Especialidad no encontrada con id: " + id));
        return toDto(especialidad);
    }

    @Override
    public EspecialidadDto createEspecialidad(EspecialidadDto dto) {
        Especialidad especialidad = toEntity(dto);
        especialidad.setActiva(true);
        Especialidad saved = especialidadRepository.save(especialidad);
        return toDto(saved);
    }

    @Override
    public EspecialidadDto updateEspecialidad(Long id, EspecialidadDto dto) {
        Especialidad especialidad = especialidadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Especialidad no encontrada con id: " + id));
        especialidad.setNombre(dto.getNombre());
        especialidad.setDescripcion(dto.getDescripcion());
        especialidad.setActiva(dto.getActiva());
        Especialidad updated = especialidadRepository.save(especialidad);
        return toDto(updated);
    }

    @Override
    public void deleteEspecialidad(Long id) {
        Especialidad especialidad = especialidadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Especialidad no encontrada con id: " + id));
        especialidadRepository.delete(especialidad);
    }

    private EspecialidadDto toDto(Especialidad e) {
        EspecialidadDto dto = new EspecialidadDto();
        dto.setId(e.getId());
        dto.setNombre(e.getNombre());
        dto.setDescripcion(e.getDescripcion());
        dto.setActiva(e.getActiva());
        return dto;
    }

    private Especialidad toEntity(EspecialidadDto dto) {
        Especialidad e = new Especialidad();
        e.setNombre(dto.getNombre());
        e.setDescripcion(dto.getDescripcion());
        return e;
    }
}