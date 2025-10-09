package com.alquileres.controller;

import com.alquileres.dto.CancelacionContratoDTO;
import com.alquileres.service.CancelacionContratoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cancelaciones-contratos")
@Tag(name = "Cancelaciones de Contratos", description = "API para gestión de cancelaciones de contratos")
public class CancelacionContratoController {

    @Autowired
    private CancelacionContratoService cancelacionContratoService;

    /**
     * Obtiene todas las cancelaciones de contratos
     */
    @GetMapping
    @Operation(summary = "Obtener todas las cancelaciones",
               description = "Retorna todas las cancelaciones de contratos con el nombre del motivo de cancelación")
    public ResponseEntity<List<CancelacionContratoDTO>> obtenerTodasLasCancelaciones() {
        try {
            List<CancelacionContratoDTO> cancelaciones = cancelacionContratoService.obtenerTodasLasCancelaciones();
            return ResponseEntity.ok(cancelaciones);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        }
    }

    /**
     * Obtiene una cancelación por su ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener cancelación por ID",
               description = "Retorna una cancelación específica con el nombre del motivo de cancelación")
    public ResponseEntity<CancelacionContratoDTO> obtenerCancelacionPorId(@PathVariable Long id) {
        try {
            return cancelacionContratoService.obtenerCancelacionPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        }
    }

    /**
     * Obtiene la cancelación de un contrato específico
     */
    @GetMapping("/contrato/{contratoId}")
    @Operation(summary = "Obtener cancelación de un contrato",
               description = "Retorna la cancelación de un contrato específico con el nombre del motivo de cancelación")
    public ResponseEntity<CancelacionContratoDTO> obtenerCancelacionPorContrato(@PathVariable Long contratoId) {
        try {
            return cancelacionContratoService.obtenerCancelacionPorContratoId(contratoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        }
    }

    /**
     * Obtiene todas las cancelaciones por motivo
     */
    @GetMapping("/motivo/{motivoId}")
    @Operation(summary = "Obtener cancelaciones por motivo",
               description = "Retorna todas las cancelaciones que tienen un motivo específico")
    public ResponseEntity<List<CancelacionContratoDTO>> obtenerCancelacionesPorMotivo(@PathVariable Integer motivoId) {
        try {
            List<CancelacionContratoDTO> cancelaciones =
                cancelacionContratoService.obtenerCancelacionesPorMotivo(motivoId);
            return ResponseEntity.ok(cancelaciones);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        }
    }
}
