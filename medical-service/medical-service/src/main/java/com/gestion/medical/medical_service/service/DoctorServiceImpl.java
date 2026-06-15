package com.gestion.medical.medical_service.service;

import com.gestion.medical.medical_service.dto.DoctorRequest;
import com.gestion.medical.medical_service.dto.DoctorResponse;
import com.gestion.medical.medical_service.entity.Doctor;
import com.gestion.medical.medical_service.entity.Especialidad;
import com.gestion.medical.medical_service.repository.DoctorRepository;
import com.gestion.medical.medical_service.repository.EspecialidadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository doctorRepository;
    private final EspecialidadRepository especialidadRepository;

    @Override
    public DoctorResponse crear(DoctorRequest request) {
        if (doctorRepository.existsByUsuarioId(request.getUsuarioId())) {
            throw new IllegalArgumentException("El usuario ya está registrado como doctor");
        }
        Especialidad especialidad = especialidadRepository.findById(request.getEspecialidadId())
                .orElseThrow(() -> new RuntimeException("Especialidad no encontrada"));
        Doctor entity = Doctor.builder()
                .usuarioId(request.getUsuarioId())
                .especialidad(especialidad)
                .cmp(request.getCmp())
                .disponible(request.getDisponible() != null ? request.getDisponible() : true)
                .build();
        return toResponse(doctorRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorResponse obtenerPorId(Long id) {
        Doctor entity = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor no encontrado"));
        return toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorResponse> listarTodos() {
        return doctorRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorResponse> listarPorEspecialidad(Long especialidadId) {
        return doctorRepository.findByEspecialidadId(especialidadId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DoctorResponse actualizar(Long id, DoctorRequest request) {
        Doctor entity = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor no encontrado"));
        Especialidad especialidad = especialidadRepository.findById(request.getEspecialidadId())
                .orElseThrow(() -> new RuntimeException("Especialidad no encontrada"));
        entity.setUsuarioId(request.getUsuarioId());
        entity.setEspecialidad(especialidad);
        entity.setCmp(request.getCmp());
        entity.setDisponible(request.getDisponible() != null ? request.getDisponible() : entity.getDisponible());
        return toResponse(doctorRepository.save(entity));
    }

    @Override
    public void eliminar(Long id) {
        Doctor entity = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor no encontrado"));
        entity.setDisponible(false);
        doctorRepository.save(entity);
    }

    private DoctorResponse toResponse(Doctor d) {
        return DoctorResponse.builder()
                .id(d.getId())
                .usuarioId(d.getUsuarioId())
                .especialidadId(d.getEspecialidad().getId())
                .especialidadNombre(d.getEspecialidad().getNombre())
                .cmp(d.getCmp())
                .disponible(d.getDisponible())
                .createdAt(d.getCreatedAt())
                .build();
    }
}
