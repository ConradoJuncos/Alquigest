package com.alquileres.controller;

import com.alquileres.dto.CrearServicioRequest;
import com.alquileres.model.ServicioXContrato;
import com.alquileres.service.ServicioXContratoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/servicios-contrato")
@Tag(name = "Servicios por Contrato", description = "API para gestión de servicios asociados a contratos")
public class ServicioXContratoController {

    @Autowired
    private ServicioXContratoService servicioXContratoService;

    /**
     * Crea un nuevo servicio para un contrato
     * Automáticamente crea la configuración de pago y generará facturas mensuales
     *
     * @param request Datos del servicio a crear
     * @return El servicio creado
     */
    @PostMapping
    @Operation(summary = "Crear servicio para un contrato",
               description = "Crea un nuevo servicio asociado a un contrato. " +
                           "Automáticamente crea la configuración de pago que generará facturas mensuales " +
                           "con vencimiento el día 10 de cada mes.")
    public ResponseEntity<?> crearServicio(@Valid @RequestBody CrearServicioRequest request) {
        try {
            ServicioXContrato servicio = servicioXContratoService.crearServicio(
                    request.getContratoId(),
                    request.getTipoServicioId(),
                    request.getNroCuenta(),
                    request.getNroContrato(),
                    request.getEsDeInquilino(),
                    request.getEsAnual(),
                    request.getFechaInicio()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(servicio);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al crear el servicio"));
        }
    }

    /**
     * Obtiene todos los servicios de un contrato
     *
     * @param contratoId ID del contrato
     * @return Lista de servicios
     */
    @GetMapping("/contrato/{contratoId}")
    @Operation(summary = "Obtener servicios de un contrato",
               description = "Retorna todos los servicios (activos e inactivos) asociados a un contrato")
    public ResponseEntity<List<ServicioXContrato>> obtenerServiciosPorContrato(
            @PathVariable Long contratoId) {
        try {
            List<ServicioXContrato> servicios = servicioXContratoService.obtenerServiciosPorContrato(contratoId);
            return ResponseEntity.ok(servicios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene solo los servicios activos de un contrato
     *
     * @param contratoId ID del contrato
     * @return Lista de servicios activos
     */
    @GetMapping("/contrato/{contratoId}/activos")
    @Operation(summary = "Obtener servicios activos de un contrato",
               description = "Retorna únicamente los servicios activos de un contrato")
    public ResponseEntity<List<ServicioXContrato>> obtenerServiciosActivosPorContrato(
            @PathVariable Long contratoId) {
        try {
            List<ServicioXContrato> servicios = servicioXContratoService.obtenerServiciosActivosPorContrato(contratoId);
            return ResponseEntity.ok(servicios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Desactiva un servicio
     *
     * @param servicioId ID del servicio
     * @return Respuesta de éxito
     */
    @PutMapping("/{servicioId}/desactivar")
    @Operation(summary = "Desactivar servicio",
               description = "Desactiva un servicio. Ya no se generarán facturas para este servicio.")
    public ResponseEntity<?> desactivarServicio(@PathVariable Integer servicioId) {
        try {
            servicioXContratoService.desactivarServicio(servicioId);
            return ResponseEntity.ok(Map.of("mensaje", "Servicio desactivado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al desactivar el servicio"));
        }
    }

    /**
     * Reactiva un servicio
     *
     * @param servicioId ID del servicio
     * @param request Datos de reactivación (fechaInicio)
     * @return Respuesta de éxito
     */
    @PutMapping("/{servicioId}/reactivar")
    @Operation(summary = "Reactivar servicio",
               description = "Reactiva un servicio previamente desactivado. " +
                           "Se reanudará la generación de facturas.")
    public ResponseEntity<?> reactivarServicio(
            @PathVariable Integer servicioId,
            @RequestBody Map<String, String> request) {
        try {
            String fechaInicio = request.get("fechaInicio");
            servicioXContratoService.reactivarServicio(servicioId, fechaInicio);
            return ResponseEntity.ok(Map.of("mensaje", "Servicio reactivado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al reactivar el servicio"));
        }
    }

    /**
     * Obtiene un servicio por ID
     *
     * @param servicioId ID del servicio
     * @return El servicio si existe
     */
    @GetMapping("/{servicioId}")
    @Operation(summary = "Obtener servicio por ID",
               description = "Retorna los detalles de un servicio específico")
    public ResponseEntity<?> obtenerServicioPorId(@PathVariable Integer servicioId) {
        try {
            return servicioXContratoService.obtenerServicioPorId(servicioId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

