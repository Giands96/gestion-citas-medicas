package com.gestion.medical.medical_service.service;

import com.gestion.medical.medical_service.dto.DoctorDto;
import com.gestion.medical.medical_service.entity.Doctor;
import com.gestion.medical.medical_service.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;

    @Override
    public List<DoctorDto> getAllDoctores() {
        return doctorRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public DoctorDto getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor no encontrado con id: " + id));
        return toDto(doctor);
    }

    @Override
    public DoctorDto createDoctor(DoctorDto dto) {
        Doctor doctor = toEntity(dto);
        doctor.setDisponible(true);
        doctor.setCreatedAt(LocalDateTime.now());
        Doctor saved = doctorRepository.save(doctor);
        return toDto(saved);
    }

    @Override
    public DoctorDto updateDoctor(Long id, DoctorDto dto) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor no encontrado con id: " + id));
        doctor.setUsuarioId(dto.getUsuarioId());
        doctor.setEspecialidadId(dto.getEspecialidadId());
        doctor.setCmp(dto.getCmp());
        doctor.setDisponible(dto.getDisponible());
        doctor.setUpdatedAt(LocalDateTime.now());
        Doctor updated = doctorRepository.save(doctor);
        return toDto(updated);
    }

    @Override
    public void deleteDoctor(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor no encontrado con id: " + id));
        doctorRepository.delete(doctor);
    }

    private DoctorDto toDto(Doctor d) {
        DoctorDto dto = new DoctorDto();
        dto.setId(d.getId());
        dto.setUsuarioId(d.getUsuarioId());
        dto.setEspecialidadId(d.getEspecialidadId());
        dto.setCmp(d.getCmp());
        dto.setDisponible(d.getDisponible());
        dto.setCreatedAt(d.getCreatedAt());
        dto.setUpdatedAt(d.getUpdatedAt());
        return dto;
    }

    private Doctor toEntity(DoctorDto dto) {
        Doctor d = new Doctor();
        d.setUsuarioId(dto.getUsuarioId());
        d.setEspecialidadId(dto.getEspecialidadId());
        d.setCmp(dto.getCmp());
        return d;
    }
}