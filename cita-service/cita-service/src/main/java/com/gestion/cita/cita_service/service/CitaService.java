package com.gestion.cita.cita_service.service;

import java.util.List;

import com.gestion.cita.cita_service.dto.CitaDto;

public interface CitaService {
    
    public void createCita(CitaDto citaDto);
    public CitaDto updateCita(Long id, CitaDto citaDto);
    public CitaDto getCitaById(Long id);
    public void deleteCita(Long id);
    public List<CitaDto> getAllCitas();
}
