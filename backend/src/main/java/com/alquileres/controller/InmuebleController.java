package com.alquileres.controller;

import com.alquileres.dto.InmuebleDTO;
import com.alquileres.service.InmuebleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;

@RestController
@RequestMapping("/api/inmuebles")
@Tag(name = "Inmuebles", description = "API para gestión de inmuebles")
public class InmuebleController {

    @Autowired
    private InmuebleService inmuebleService;

    // GET /api/inmuebles - Obtener todos los inmuebles
    @GetMapping
    public ResponseEntity<List<InmuebleDTO>> obtenerTodosLosInmuebles() {
        List<InmuebleDTO> inmuebles = inmuebleService.obtenerTodosLosInmuebles();
        return ResponseEntity.ok(inmuebles);
    }

    // GET /api/inmuebles/activos - Obtener solo inmuebles activos
    @GetMapping("/activos")
    public ResponseEntity<List<InmuebleDTO>> obtenerInmueblesActivos() {
        List<InmuebleDTO> inmuebles = inmuebleService.obtenerInmueblesActivos();
        return ResponseEntity.ok(inmuebles);
    }

    // GET /api/inmuebles/inactivos - Obtener solo inmuebles inactivos
    @GetMapping("/inactivos")
    public ResponseEntity<List<InmuebleDTO>> obtenerInmueblesInactivos() {
        List<InmuebleDTO> inmuebles = inmuebleService.obtenerInmueblesInactivos();
        return ResponseEntity.ok(inmuebles);
    }

    // GET /api/inmuebles/disponibles - Obtener inmuebles disponibles
    @GetMapping("/disponibles")
    public ResponseEntity<List<InmuebleDTO>> obtenerInmueblesDisponibles() {
        List<InmuebleDTO> inmuebles = inmuebleService.obtenerInmueblesDisponibles();
        return ResponseEntity.ok(inmuebles);
    }

    // GET /api/inmuebles/{id} - Obtener inmueble por ID
    @GetMapping("/{id}")
    public ResponseEntity<InmuebleDTO> obtenerInmueblePorId(@PathVariable Long id) {
        InmuebleDTO inmueble = inmuebleService.obtenerInmueblePorId(id);
        return ResponseEntity.ok(inmueble);
    }

    // GET /api/inmuebles/propietario/{propietarioId} - Buscar por propietario
    @GetMapping("/propietario/{propietarioId}")
    public ResponseEntity<List<InmuebleDTO>> buscarPorPropietario(@PathVariable Long propietarioId) {
        List<InmuebleDTO> inmuebles = inmuebleService.buscarPorPropietario(propietarioId);
        return ResponseEntity.ok(inmuebles);
    }

    // GET /api/inmuebles/buscar-direccion - Buscar por dirección
    @GetMapping("/buscar-direccion")
    public ResponseEntity<List<InmuebleDTO>> buscarPorDireccion(@RequestParam String direccion) {
        List<InmuebleDTO> inmuebles = inmuebleService.buscarPorDireccion(direccion);
        return ResponseEntity.ok(inmuebles);
    }

    // POST /api/inmuebles - Crear nuevo inmueble
    @PostMapping
    public ResponseEntity<InmuebleDTO> crearInmueble(@Valid @RequestBody InmuebleDTO inmuebleDTO) {
        InmuebleDTO nuevoInmueble = inmuebleService.crearInmueble(inmuebleDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoInmueble);
    }

    // PUT /api/inmuebles/{id} - Actualizar inmueble
    @PutMapping("/{id}")
    public ResponseEntity<InmuebleDTO> actualizarInmueble(
            @PathVariable Long id,
            @Valid @RequestBody InmuebleDTO inmuebleDTO) {
        InmuebleDTO inmuebleActualizado = inmuebleService.actualizarInmueble(id, inmuebleDTO);
        return ResponseEntity.ok(inmuebleActualizado);
    }

    // PATCH /api/inmuebles/{id}/alquiler - Cambiar estado de alquiler
    @PatchMapping("/{id}/alquiler")
    public ResponseEntity<InmuebleDTO> cambiarEstadoAlquiler(
            @PathVariable Long id,
            @RequestParam Boolean esAlquilado) {
        InmuebleDTO inmuebleActualizado = inmuebleService.cambiarEstadoAlquiler(id, esAlquilado);
        return ResponseEntity.ok(inmuebleActualizado);
    }

    // PATCH /api/inmuebles/{id}/desactivar - Desactivar inmueble (eliminación lógica)
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivarInmueble(@PathVariable Long id) {
        inmuebleService.desactivarInmueble(id);
        return ResponseEntity.noContent().build();
    }

    // PATCH /api/inmuebles/{id}/tipo - Cambiar tipo de inmueble
    @PatchMapping("/{id}/tipo")
    @Operation(summary = "Cambiar tipo de inmueble",
               description = "Permite cambiar el tipo de un inmueble específico. No se puede cambiar si el inmueble tiene un contrato vigente.")
    public ResponseEntity<InmuebleDTO> cambiarTipoInmueble(
            @PathVariable Long id,
            @RequestParam Long tipoInmuebleId) {
        InmuebleDTO inmuebleActualizado = inmuebleService.cambiarTipoInmueble(id, tipoInmuebleId);
        return ResponseEntity.ok(inmuebleActualizado);
    }
}
