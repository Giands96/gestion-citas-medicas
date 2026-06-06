package com.gestion.cita.cita_service.service.impl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.gestion.cita.cita_service.dto.CitaDto;
import com.gestion.cita.cita_service.entity.Cita;
import com.gestion.cita.cita_service.errors.NoResponse;
import com.gestion.cita.cita_service.repository.CitaRepository;
import com.gestion.cita.cita_service.service.CitaService;

@Service
public class CitaServiceImpl implements CitaService {

    private final CitaRepository citaRepository;

    public CitaServiceImpl(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }

    @Override
    public void createCita(CitaDto citaDto) {
        Cita cita = mapperCita(citaDto);
        citaRepository.save(cita);
    }

    @Override
    public CitaDto updateCita(Long id, CitaDto citaDto) {
        Cita cita = citaRepository.findById(id).orElseThrow(() -> new RuntimeException("Cita no encontrada"));
        setUpdate(cita, citaDto);
        Cita updatedCita = citaRepository.save(cita);
        return CitaDto.builder()
                .id(updatedCita.getId())
                .pacienteId(updatedCita.getPacienteId())
                .doctorId(updatedCita.getDoctorId())
                .fecha(updatedCita.getFecha())
                .hora(updatedCita.getHora())
                .motivo(updatedCita.getMotivo())
                .estado(updatedCita.getEstado())
                .build();

    }

    private void setUpdate(Cita cita, CitaDto citaDto) {
        cita.setPacienteId(citaDto.getPacienteId());
        cita.setDoctorId(citaDto.getDoctorId());
        cita.setFecha(citaDto.getFecha());
        cita.setHora(citaDto.getHora());
        cita.setMotivo(citaDto.getMotivo());
        cita.setEstado(citaDto.getEstado());
    }

    @Override
    public CitaDto getCitaById(Long id) {
        Cita cita = citaRepository.findById(id).orElseThrow(() -> new NoResponse("Cita no encontrada"));
        return CitaDto.builder()
                .id(cita.getId())
                .pacienteId(cita.getPacienteId())
                .doctorId(cita.getDoctorId())
                .fecha(cita.getFecha())
                .hora(cita.getHora())
                .motivo(cita.getMotivo())
                .estado(cita.getEstado())
                .build();
    }

    private Cita mapperCita(CitaDto citaDto) {
        Cita cita = new Cita();
        cita.setPacienteId(citaDto.getPacienteId());
        cita.setDoctorId(citaDto.getDoctorId());
        cita.setFecha(LocalDate.now());
        cita.setHora(LocalTime.now());
        cita.setMotivo(citaDto.getMotivo());
        cita.setEstado(citaDto.getEstado());
        return cita;
    }

    @Override
    public void deleteCita(Long id) {
        Cita cita = citaRepository.findById(id).orElseThrow(() -> new NoResponse("Cita no encontrada"));
        citaRepository.delete(cita);
    }

    @Override
    public List<CitaDto> getAllCitas() {
        List<Cita> citas = citaRepository.findAll();
        return citas.stream()
                .map(cita -> CitaDto.builder()
                        .id(cita.getId())
                        .pacienteId(cita.getPacienteId())
                        .doctorId(cita.getDoctorId())
                        .fecha(cita.getFecha())
                        .hora(cita.getHora())
                        .motivo(cita.getMotivo())
                        .estado(cita.getEstado())
                        .build())
                .collect(Collectors.toList());
    }

}
