package com.gestion.medical.medical_service.service;

import com.gestion.medical.medical_service.dto.DoctorDto;
import java.util.List;

public interface DoctorService {

    List<DoctorDto> getAllDoctores();

    DoctorDto getDoctorById(Long id);

    DoctorDto createDoctor(DoctorDto dto);

    DoctorDto updateDoctor(Long id, DoctorDto dto);

    void deleteDoctor(Long id);
}