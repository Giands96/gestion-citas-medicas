package com.gestion.medical.medical_service.service;

import com.gestion.medical.medical_service.dto.EspecialidadDto;
import java.util.List;

public interface EspecialidadService {

    List<EspecialidadDto> getAllEspecialidades();

    EspecialidadDto getEspecialidadById(Long id);

    EspecialidadDto createEspecialidad(EspecialidadDto dto);

    EspecialidadDto updateEspecialidad(Long id, EspecialidadDto dto);

    void deleteEspecialidad(Long id);
}