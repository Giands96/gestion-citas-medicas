package com.gestion.medical.medical_service.controller;

import com.gestion.medical.medical_service.dto.DoctorRequest;
import com.gestion.medical.medical_service.dto.DoctorResponse;
import com.gestion.medical.medical_service.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicals/doctores")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;

    @GetMapping
    public ResponseEntity<List<DoctorResponse>> listarTodos() {
        return ResponseEntity.ok(doctorService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.obtenerPorId(id));
    }

    @GetMapping("/especialidad/{especialidadId}")
    public ResponseEntity<List<DoctorResponse>> listarPorEspecialidad(@PathVariable Long especialidadId) {
        return ResponseEntity.ok(doctorService.listarPorEspecialidad(especialidadId));
    }

    @PostMapping
    public ResponseEntity<DoctorResponse> crear(@Valid @RequestBody DoctorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponse> actualizar(@PathVariable Long id, @Valid @RequestBody DoctorRequest request) {
        return ResponseEntity.ok(doctorService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        doctorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
