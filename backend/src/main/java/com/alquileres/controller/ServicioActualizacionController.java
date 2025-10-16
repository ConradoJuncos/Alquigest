package com.alquileres.controller;

import com.alquileres.service.ServicioActualizacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/servicios-actualizacion")
@Tag(name = "Actualización de Servicios", description = "API para testing y forzar procesamiento de pagos")
public class ServicioActualizacionController {

    @Autowired
    private ServicioActualizacionService servicioActualizacionService;

    /**
     * Fuerza el procesamiento de pagos de servicios
     * Útil para testing - ignora el mes ya procesado
     */
    @PostMapping("/forzar-procesamiento")
    @Operation(summary = "Forzar procesamiento de pagos",
               description = "Fuerza el procesamiento de pagos independientemente del mes. Útil para testing.")
    public ResponseEntity<?> forzarProcesamiento() {
        try {
            int facturasGeneradas = servicioActualizacionService.forzarProcesamientoPagos();
            return ResponseEntity.ok(Map.of(
                "mensaje", "Procesamiento forzado completado",
                "facturasGeneradas", facturasGeneradas
            ));
        } catch (Exception e) {
            return ResponseEntity
                .status(500)
                .body(Map.of("error", "Error al forzar procesamiento: " + e.getMessage()));
        }
    }

    /**
     * Obtiene el último mes procesado
     */
    @GetMapping("/ultimo-mes-procesado")
    @Operation(summary = "Obtener último mes procesado",
               description = "Retorna el último mes en que se procesaron pagos automáticamente")
    public ResponseEntity<?> obtenerUltimoMesProcesado() {
        try {
            String ultimoMes = servicioActualizacionService.getUltimoMesProcesado();
            return ResponseEntity.ok(Map.of(
                "ultimoMesProcesado", ultimoMes != null ? ultimoMes : "Nunca procesado"
            ));
        } catch (Exception e) {
            return ResponseEntity
                .status(500)
                .body(Map.of("error", "Error al obtener último mes: " + e.getMessage()));
        }
    }

    /**
     * Crea servicios para contratos vigentes
     */
    @PostMapping("/crear-servicios-contratos")
    @Operation(summary = "Crear servicios para contratos vigentes",
               description = "Fuerza la creación de servicios para todos los contratos vigentes que no los tengan")
    public ResponseEntity<?> crearServiciosContratos() {
        try {
            int serviciosCreados = servicioActualizacionService.crearServiciosParaContratosVigentes();
            return ResponseEntity.ok(Map.of(
                "mensaje", "Servicios creados exitosamente",
                "serviciosCreados", serviciosCreados
            ));
        } catch (Exception e) {
            return ResponseEntity
                .status(500)
                .body(Map.of("error", "Error al crear servicios: " + e.getMessage()));
        }
    }
}

