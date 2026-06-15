package com.gestion.cita.cita_service.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestion.cita.cita_service.client.MedicalServiceClient;
import com.gestion.cita.cita_service.client.UserServiceClient;
import com.gestion.cita.cita_service.client.dto.DoctorResponse;
import com.gestion.cita.cita_service.client.dto.UsuarioResponse;
import com.gestion.cita.cita_service.dto.CitaRequest;
import com.gestion.cita.cita_service.dto.CitaResponse;
import com.gestion.cita.cita_service.entity.Cita;
import com.gestion.cita.cita_service.errors.NoResponse;
import com.gestion.cita.cita_service.repository.CitaRepository;
import com.gestion.cita.cita_service.service.CitaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CitaServiceImpl implements CitaService {

    private final CitaRepository citaRepository;
    private final UserServiceClient userServiceClient;
    private final MedicalServiceClient medicalServiceClient;

    @Override
    public CitaResponse createCita(CitaRequest request) {
        Cita cita = Cita.builder()
                .pacienteId(request.getPacienteId())
                .doctorId(request.getDoctorId())
                .fecha(request.getFecha())
                .hora(request.getHora())
                .motivo(request.getMotivo())
                .estado("PENDIENTE")
                .build();

    }

    private void setUpdate(Cita cita, CitaDto citaDto) {
        cita.setPacienteId(citaDto.getPacienteId());
        cita.setDoctorId(citaDto.getDoctorId());
        cita.setFecha(LocalDate.now());
        cita.setHora(LocalTime.now());
        cita.setMotivo(citaDto.getMotivo());
        cita.setEstado(citaDto.getEstado());
    }

    @Override
    public CitaResponse updateCita(Long id, CitaRequest request) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new NoResponse("Cita no encontrada"));
        cita.setPacienteId(request.getPacienteId());
        cita.setDoctorId(request.getDoctorId());
        cita.setFecha(request.getFecha());
        cita.setHora(request.getHora());
        cita.setMotivo(request.getMotivo());
        if (request.getEstado() != null) {
            cita.setEstado(request.getEstado());
        }
        return toResponse(citaRepository.save(cita));
    }

    @Override
    @Transactional(readOnly = true)
    public CitaResponse getCitaById(Long id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new NoResponse("Cita no encontrada"));
        return toResponse(cita);
    }

    @Override
    public void deleteCita(Long id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new NoResponse("Cita no encontrada"));
        citaRepository.delete(cita);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaResponse> getAllCitas() {
        return citaRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private CitaResponse toResponse(Cita cita) {
        CitaResponse.CitaResponseBuilder builder = CitaResponse.builder()
                .id(cita.getId())
                .pacienteId(cita.getPacienteId())
                .doctorId(cita.getDoctorId())
                .fecha(cita.getFecha())
                .hora(cita.getHora())
                .motivo(cita.getMotivo())
                .estado(cita.getEstado());

        try {
            UsuarioResponse paciente = userServiceClient.obtenerUsuarioPorId(cita.getPacienteId());
            builder.pacienteNombre(paciente.getNombres());
            builder.pacienteApellido(paciente.getApellidos());
        } catch (Exception e) {
            builder.pacienteNombre("N/A");
            builder.pacienteApellido("N/A");
        }

        try {
            DoctorResponse doctor = medicalServiceClient.obtenerDoctorPorId(cita.getDoctorId());
            builder.especialidadId(doctor.getEspecialidadId());
            builder.especialidadNombre(doctor.getEspecialidadNombre());

            try {
                UsuarioResponse doctorUser = userServiceClient.obtenerUsuarioPorId(doctor.getUsuarioId());
                builder.doctorNombre(doctorUser.getNombres());
                builder.doctorApellido(doctorUser.getApellidos());
            } catch (Exception e) {
                builder.doctorNombre("N/A");
                builder.doctorApellido("N/A");
            }
        } catch (Exception e) {
            builder.especialidadNombre("N/A");
            builder.doctorNombre("N/A");
            builder.doctorApellido("N/A");
        }

        return builder.build();
    }
}
