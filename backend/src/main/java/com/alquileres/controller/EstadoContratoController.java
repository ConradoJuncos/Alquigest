package com.alquileres.controller;

import com.alquileres.dto.EstadoContratoDTO;
import com.alquileres.service.EstadoContratoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;

@RestController
@RequestMapping("/api/estados-contrato")
@Tag(name = "Estados de Contrato", description = "API para gestión de estados de contratos")
public class EstadoContratoController {

    @Autowired
    private EstadoContratoService estadoContratoService;

    // GET /api/estados-contrato - Obtener todos los estados de contrato
    @GetMapping
    @Operation(summary = "Obtener todos los estados de contrato")
    public ResponseEntity<List<EstadoContratoDTO>> obtenerTodosLosEstadosContrato() {
        List<EstadoContratoDTO> estados = estadoContratoService.obtenerTodosLosEstadosContrato();
        return ResponseEntity.ok(estados);
    }

    // GET /api/estados-contrato/{id} - Obtener estado de contrato por ID
    @GetMapping("/{id}")
    @Operation(summary = "Obtener estado de contrato por ID")
    public ResponseEntity<EstadoContratoDTO> obtenerEstadoContratoPorId(@PathVariable Integer id) {
        EstadoContratoDTO estado = estadoContratoService.obtenerEstadoContratoPorId(id);
        return ResponseEntity.ok(estado);
    }

    // GET /api/estados-contrato/nombre/{nombre} - Obtener estado de contrato por nombre
    @GetMapping("/nombre/{nombre}")
    @Operation(summary = "Obtener estado de contrato por nombre")
    public ResponseEntity<EstadoContratoDTO> obtenerEstadoContratoPorNombre(@PathVariable String nombre) {
        EstadoContratoDTO estado = estadoContratoService.obtenerEstadoContratoPorNombre(nombre);
        return ResponseEntity.ok(estado);
    }

    // POST /api/estados-contrato - Crear nuevo estado de contrato
    @PostMapping
    @Operation(summary = "Crear nuevo estado de contrato")
    public ResponseEntity<EstadoContratoDTO> crearEstadoContrato(@Valid @RequestBody EstadoContratoDTO estadoContratoDTO) {
        EstadoContratoDTO estadoCreado = estadoContratoService.crearEstadoContrato(estadoContratoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(estadoCreado);
    }

    // PUT /api/estados-contrato/{id} - Actualizar estado de contrato
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar estado de contrato")
    public ResponseEntity<EstadoContratoDTO> actualizarEstadoContrato(
            @PathVariable Integer id,
            @Valid @RequestBody EstadoContratoDTO estadoContratoDTO) {
        EstadoContratoDTO estadoActualizado = estadoContratoService.actualizarEstadoContrato(id, estadoContratoDTO);
        return ResponseEntity.ok(estadoActualizado);
    }

    // DELETE /api/estados-contrato/{id} - Eliminar estado de contrato
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar estado de contrato")
    public ResponseEntity<Void> eliminarEstadoContrato(@PathVariable Integer id) {
        estadoContratoService.eliminarEstadoContrato(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/estados-contrato/{id}/existe - Verificar si existe un estado de contrato
    @GetMapping("/{id}/existe")
    @Operation(summary = "Verificar si existe un estado de contrato")
    public ResponseEntity<Boolean> existeEstadoContrato(@PathVariable Integer id) {
        boolean existe = estadoContratoService.existeEstadoContrato(id);
        return ResponseEntity.ok(existe);
    }
}
