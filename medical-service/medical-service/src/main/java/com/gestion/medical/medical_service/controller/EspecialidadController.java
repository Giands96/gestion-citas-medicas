package com.gestion.medical.medical_service.controller;

import com.gestion.medical.medical_service.dto.EspecialidadDto;
import com.gestion.medical.medical_service.service.EspecialidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/especialidades")
@RequiredArgsConstructor
public class EspecialidadController {

    private final EspecialidadService especialidadService;

    @GetMapping
    public ResponseEntity<List<EspecialidadDto>> getAllEspecialidades() {
        return ResponseEntity.ok(especialidadService.getAllEspecialidades());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EspecialidadDto> getEspecialidadById(@PathVariable Long id) {
        return ResponseEntity.ok(especialidadService.getEspecialidadById(id));
    }

    @PostMapping
    public ResponseEntity<EspecialidadDto> createEspecialidad(@RequestBody EspecialidadDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(especialidadService.createEspecialidad(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EspecialidadDto> updateEspecialidad(@PathVariable Long id, @RequestBody EspecialidadDto dto) {
        return ResponseEntity.ok(especialidadService.updateEspecialidad(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEspecialidad(@PathVariable Long id) {
        especialidadService.deleteEspecialidad(id);
        return ResponseEntity.noContent().build();
    }
}