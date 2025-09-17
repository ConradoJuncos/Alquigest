package com.alquileres.controller;

import com.alquileres.dto.InmuebleDTO;
import com.alquileres.service.InmuebleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/inmuebles")
@Tag(name = "Inmuebles", description = "API para gestión de inmuebles")
@CrossOrigin(origins = "http://localhost:3000")
public class InmuebleController {

    @Autowired
    private InmuebleService inmuebleService;

    @GetMapping
    @Operation(summary = "Obtener todos los inmuebles", description = "Retorna una lista de todos los inmuebles registrados")
    public ResponseEntity<List<InmuebleDTO>> obtenerTodosLosInmuebles() {
        List<InmuebleDTO> inmuebles = inmuebleService.obtenerTodosLosInmuebles();
        return ResponseEntity.ok(inmuebles);
    }

    @GetMapping("/activos")
    @Operation(summary = "Obtener inmuebles activos", description = "Retorna solo los inmuebles que están activos")
    public ResponseEntity<List<InmuebleDTO>> obtenerInmueblesActivos() {
        List<InmuebleDTO> inmuebles = inmuebleService.obtenerInmueblesActivos();
        return ResponseEntity.ok(inmuebles);
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Obtener inmuebles disponibles", description = "Retorna inmuebles activos y no alquilados")
    public ResponseEntity<List<InmuebleDTO>> obtenerInmueblesDisponibles() {
        List<InmuebleDTO> inmuebles = inmuebleService.obtenerInmueblesDisponibles();
        return ResponseEntity.ok(inmuebles);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener inmueble por ID", description = "Retorna un inmueble específico por su ID")
    public ResponseEntity<InmuebleDTO> obtenerInmueblePorId(
            @Parameter(description = "ID del inmueble") @PathVariable Long id) {
        Optional<InmuebleDTO> inmueble = inmuebleService.obtenerInmueblePorId(id);
        return inmueble.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear nuevo inmueble", description = "Crea un nuevo inmueble en el sistema")
    public ResponseEntity<InmuebleDTO> crearInmueble(@Valid @RequestBody InmuebleDTO inmuebleDTO) {
        InmuebleDTO nuevoInmueble = inmuebleService.crearInmueble(inmuebleDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoInmueble);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar inmueble", description = "Actualiza un inmueble existente")
    public ResponseEntity<InmuebleDTO> actualizarInmueble(
            @Parameter(description = "ID del inmueble") @PathVariable Long id,
            @Valid @RequestBody InmuebleDTO inmuebleDTO) {
        Optional<InmuebleDTO> inmuebleActualizado = inmuebleService.actualizarInmueble(id, inmuebleDTO);
        return inmuebleActualizado.map(ResponseEntity::ok)
                                 .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar inmueble", description = "Elimina un inmueble del sistema")
    public ResponseEntity<Void> eliminarInmueble(
            @Parameter(description = "ID del inmueble") @PathVariable Long id) {
        boolean eliminado = inmuebleService.eliminarInmueble(id);
        return eliminado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar inmuebles por texto", description = "Busca inmuebles que contengan el texto en dirección o tipo")
    public ResponseEntity<List<InmuebleDTO>> buscarInmueblesPorTexto(
            @Parameter(description = "Texto a buscar") @RequestParam String texto) {
        List<InmuebleDTO> inmuebles = inmuebleService.buscarInmueblesPorTexto(texto);
        return ResponseEntity.ok(inmuebles);
    }

    @GetMapping("/propietario/{propietarioId}")
    @Operation(summary = "Buscar inmuebles por propietario", description = "Busca inmuebles de un propietario específico")
    public ResponseEntity<List<InmuebleDTO>> buscarInmueblesPorPropietario(
            @Parameter(description = "ID del propietario") @PathVariable Long propietarioId) {
        List<InmuebleDTO> inmuebles = inmuebleService.buscarInmueblesPorPropietario(propietarioId);
        return ResponseEntity.ok(inmuebles);
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Buscar inmuebles por estado", description = "Busca inmuebles por estado específico")
    public ResponseEntity<List<InmuebleDTO>> buscarInmueblesPorEstado(
            @Parameter(description = "Estado del inmueble") @PathVariable Integer estado) {
        List<InmuebleDTO> inmuebles = inmuebleService.buscarInmueblesPorEstado(estado);
        return ResponseEntity.ok(inmuebles);
    }

    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Buscar inmuebles por tipo", description = "Busca inmuebles por tipo específico")
    public ResponseEntity<List<InmuebleDTO>> buscarInmueblesPorTipo(
            @Parameter(description = "Tipo de inmueble") @PathVariable String tipo) {
        List<InmuebleDTO> inmuebles = inmuebleService.buscarInmueblesPorTipo(tipo);
        return ResponseEntity.ok(inmuebles);
    }

    @GetMapping("/superficie")
    @Operation(summary = "Buscar por rango de superficie", description = "Busca inmuebles dentro de un rango de superficies")
    public ResponseEntity<List<InmuebleDTO>> buscarPorRangoSuperficie(
            @Parameter(description = "Superficie mínima") @RequestParam BigDecimal superficieMin,
            @Parameter(description = "Superficie máxima") @RequestParam BigDecimal superficieMax) {
        List<InmuebleDTO> inmuebles = inmuebleService.buscarInmueblesPorSuperficie(superficieMin, superficieMax);
        return ResponseEntity.ok(inmuebles);
    }

    @PatchMapping("/{id}/alquiler")
    @Operation(summary = "Cambiar estado de alquiler", description = "Cambia el estado de alquiler de un inmueble")
    public ResponseEntity<InmuebleDTO> cambiarEstadoAlquiler(
            @Parameter(description = "ID del inmueble") @PathVariable Long id,
            @Parameter(description = "Estado de alquiler") @RequestParam boolean esAlquilado) {
        Optional<InmuebleDTO> inmuebleActualizado = inmuebleService.cambiarEstadoAlquiler(id, esAlquilado);
        return inmuebleActualizado.map(ResponseEntity::ok)
                                 .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/activo")
    @Operation(summary = "Cambiar estado activo", description = "Activa o desactiva un inmueble")
    public ResponseEntity<InmuebleDTO> cambiarEstadoActivo(
            @Parameter(description = "ID del inmueble") @PathVariable Long id,
            @Parameter(description = "Estado activo") @RequestParam boolean esActivo) {
        Optional<InmuebleDTO> inmuebleActualizado = inmuebleService.cambiarEstadoActivo(id, esActivo);
        return inmuebleActualizado.map(ResponseEntity::ok)
                                 .orElse(ResponseEntity.notFound().build());
    }
}
