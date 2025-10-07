package com.alquileres.controller;

import com.alquileres.dto.ContratoDTO;
import com.alquileres.dto.ContratoCreateDTO;
import com.alquileres.dto.EstadoContratoUpdateDTO;
import com.alquileres.service.ContratoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;

@RestController
@RequestMapping("/api/contratos")
@Tag(name = "Contratos", description = "API para gestión de contratos de alquiler")
public class ContratoController {

    @Autowired
    private ContratoService contratoService;

    // GET /api/contratos - Obtener todos los contratos
    @GetMapping
    @Operation(summary = "Obtener todos los contratos")
    public ResponseEntity<List<ContratoDTO>> obtenerTodosLosContratos() {
        List<ContratoDTO> contratos = contratoService.obtenerTodosLosContratos();
        return ResponseEntity.ok(contratos);
    }

    // GET /api/contratos/{id} - Obtener contrato por ID
    @GetMapping("/{id}")
    @Operation(summary = "Obtener contrato por ID")
    public ResponseEntity<ContratoDTO> obtenerContratoPorId(@PathVariable Long id) {
        ContratoDTO contrato = contratoService.obtenerContratoPorId(id);
        return ResponseEntity.ok(contrato);
    }

    // GET /api/contratos/inmueble/{inmuebleId} - Obtener contratos por inmueble
    @GetMapping("/inmueble/{inmuebleId}")
    @Operation(summary = "Obtener contratos por inmueble")
    public ResponseEntity<List<ContratoDTO>> obtenerContratosPorInmueble(@PathVariable Long inmuebleId) {
        List<ContratoDTO> contratos = contratoService.obtenerContratosPorInmueble(inmuebleId);
        return ResponseEntity.ok(contratos);
    }

    // GET /api/contratos/inquilino/{inquilinoId} - Obtener contratos por inquilino
    @GetMapping("/inquilino/{inquilinoId}")
    @Operation(summary = "Obtener contratos por inquilino")
    public ResponseEntity<List<ContratoDTO>> obtenerContratosPorInquilino(@PathVariable Long inquilinoId) {
        List<ContratoDTO> contratos = contratoService.obtenerContratosPorInquilino(inquilinoId);
        return ResponseEntity.ok(contratos);
    }

    // GET /api/contratos/vigentes - Obtener contratos vigentes
    @GetMapping("/vigentes")
    @Operation(summary = "Obtener contratos vigentes")
    public ResponseEntity<List<ContratoDTO>> obtenerContratosVigentes() {
        List<ContratoDTO> contratos = contratoService.obtenerContratosVigentes();
        return ResponseEntity.ok(contratos);
    }

    // GET /api/contratos/no-vigentes - Obtener contratos no vigentes
    @GetMapping("/no-vigentes")
    @Operation(summary = "Obtener contratos no vigentes")
    public ResponseEntity<List<ContratoDTO>> obtenerContratosNoVigentes() {
        List<ContratoDTO> contratos = contratoService.obtenerContratosNoVigentes();
        return ResponseEntity.ok(contratos);
    }

    // GET /api/contratos/count/vigentes - Contar contratos vigentes
    @GetMapping("/count/vigentes")
    @Operation(summary = "Contar contratos vigentes")
    public ResponseEntity<Long> contarContratosVigentes() {
        Long count = contratoService.contarContratosVigentes();
        return ResponseEntity.ok(count);
    }

    // GET /api/contratos/proximos-vencer - Obtener contratos próximos a vencer
    @GetMapping("/proximos-vencer")
    @Operation(summary = "Obtener contratos próximos a vencer")
    public ResponseEntity<List<ContratoDTO>> obtenerContratosProximosAVencer(
            @RequestParam(defaultValue = "30") int diasAntes) {
        List<ContratoDTO> contratos = contratoService.obtenerContratosProximosAVencer(diasAntes);
        return ResponseEntity.ok(contratos);
    }

    // GET /api/contratos/count/proximos-vencer - Contar contratos próximos a vencer
    @GetMapping("/count/proximos-vencer")
    @Operation(summary = "Contar contratos próximos a vencer")
    public ResponseEntity<Long> contarContratosProximosAVencer(
            @RequestParam(defaultValue = "30") int diasAntes) {
        Long count = contratoService.contarContratosProximosAVencer(diasAntes);
        return ResponseEntity.ok(count);
    }

    // POST /api/contratos - Crear nuevo contrato
    @PostMapping
    @Operation(summary = "Crear nuevo contrato")
    public ResponseEntity<ContratoDTO> crearContrato(@Valid @RequestBody ContratoCreateDTO contratoDTO) {
        ContratoDTO contratoCreado = contratoService.crearContrato(contratoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(contratoCreado);
    }

    // PUT /api/contratos/{id} - Actualizar contrato
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar contrato")
    public ResponseEntity<ContratoDTO> actualizarContrato(
            @PathVariable Long id,
            @Valid @RequestBody ContratoDTO contratoDTO) {
        ContratoDTO contratoActualizado = contratoService.actualizarContrato(id, contratoDTO);
        return ResponseEntity.ok(contratoActualizado);
    }

    // PATCH /api/contratos/{id}/estado - Cambiar estado del contrato
    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado del contrato")
    public ResponseEntity<ContratoDTO> terminarContrato(
            @PathVariable Long id,
            @Valid @RequestBody EstadoContratoUpdateDTO estadoContratoUpdateDTO) {
        ContratoDTO contratoActualizado = contratoService.terminarContrato(id, estadoContratoUpdateDTO);
        return ResponseEntity.ok(contratoActualizado);
    }

    // GET /api/contratos/{id}/existe - Verificar si existe un contrato
    @GetMapping("/{id}/existe")
    @Operation(summary = "Verificar si existe un contrato")
    public ResponseEntity<Boolean> existeContrato(@PathVariable Long id) {
        boolean existe = contratoService.existeContrato(id);
        return ResponseEntity.ok(existe);
    }
}
