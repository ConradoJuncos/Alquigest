package com.alquileres.controller;

import com.alquileres.dto.ActualizacionMontosServiciosRequest;
import com.alquileres.dto.PagoServicioResponseDTO;
import com.alquileres.dto.ActualizarPagoServicioRequest;
import com.alquileres.model.PagoServicio;
import com.alquileres.service.PagoServicioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pagos-servicios")
@Tag(name = "Pagos de Servicios", description = "API para gestión de pagos de servicios")
public class PagoServicioController {

    @Autowired
    private PagoServicioService pagoServicioService;

    /**
     * Actualiza los montos de los pagos de servicios no pagados de un contrato
     *
     * @param request JSON con contratoId y array de [tipoServicioId - nuevoMonto]
     * @return Resumen de actualizaciones realizadas
     */
    @PutMapping("/actualizar-montos")
    @Operation(summary = "Actualizar montos de pagos no pagados",
               description = "Actualiza los montos de todos los pagos de servicios no pagados de un contrato. " +
                           "Recibe un contratoId y un array de actualizaciones con tipoServicioId y nuevoMonto. " +
                           "Solo actualiza montos que no sean nulos o cero.")
    public ResponseEntity<?> actualizarMontosPagosNoPagados(
            @Valid @RequestBody ActualizacionMontosServiciosRequest request) {
        try {
            Map<String, Object> resultado = pagoServicioService.actualizarMontosPagosNoPagados(request);
            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "error", e.getMessage(),
                    "mensaje", "No se pudo completar la actualización"
                ));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Error interno del servidor",
                    "mensaje", e.getMessage()
                ));
        }
    }

    /**
     * Obtiene todos los pagos no pagados de un contrato (solo información necesaria)
     *
     * @param contratoId ID del contrato
     * @return Lista de pagos no pagados (DTO)
     */
    @GetMapping("/contrato/{contratoId}/no-pagados")
    @Operation(summary = "Obtener pagos no pagados de un contrato",
               description = "Retorna todos los pagos de servicios que aún no han sido pagados para un contrato específico")
    public ResponseEntity<List<PagoServicioResponseDTO>> obtenerPagosNoPagadosPorContrato(
            @PathVariable Long contratoId) {
        try {
            List<PagoServicio> pagos = pagoServicioService.obtenerPagosNoPagadosPorContrato(contratoId);
            List<PagoServicioResponseDTO> dtos = pagos.stream()
                    .map(PagoServicioResponseDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        }
    }


    /**
     * Obtiene todos los pagos de un contrato (solo información necesaria)
     *
     * @param contratoId ID del contrato
     * @return Lista de todos los pagos (DTO)
     */
    @GetMapping("/contrato/{contratoId}")
    @Operation(summary = "Obtener todos los pagos de un contrato",
               description = "Retorna todos los pagos de servicios (pagados y no pagados) para un contrato específico")
    public ResponseEntity<List<PagoServicioResponseDTO>> obtenerPagosPorContrato(
            @PathVariable Long contratoId) {
        try {
            List<PagoServicio> pagos = pagoServicioService.obtenerPagosPorContrato(contratoId);
            List<PagoServicioResponseDTO> dtos = pagos.stream()
                    .map(PagoServicioResponseDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        }
    }

    /**
     * Obtiene un pago de servicio por su ID
     *
     * @param pagoId ID del pago
     * @return Pago encontrado
     */
    @GetMapping("/{pagoId}")
    @Operation(summary = "Obtener un pago de servicio por ID",
               description = "Retorna un pago de servicio específico")
    public ResponseEntity<?> obtenerPagoPorId(@PathVariable Integer pagoId) {
        try {
            PagoServicio pago = pagoServicioService.obtenerPagoPorId(pagoId);
            return ResponseEntity.ok(pago);
        } catch (RuntimeException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno del servidor"));
        }
    }

    /**
     * Actualiza un pago de servicio específico
     *
     * @param pagoId ID del pago a actualizar
     * @param request Datos a actualizar
     * @return Pago actualizado
     */
    @PutMapping("/{pagoId}")
    @Operation(summary = "Actualizar un pago de servicio",
               description = "Actualiza los datos de un pago de servicio específico. " +
                           "Solo actualiza los campos que se envíen en la solicitud (los nulos se ignoran).")
    public ResponseEntity<?> actualizarPagoServicio(
            @PathVariable Integer pagoId,
            @Valid @RequestBody ActualizarPagoServicioRequest request) {
        try {
            PagoServicio pagoActualizado = pagoServicioService.actualizarPagoServicio(pagoId, request);
            return ResponseEntity.ok(pagoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Error interno del servidor",
                    "mensaje", e.getMessage()
                ));
        }
    }
}
