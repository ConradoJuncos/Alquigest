package com.alquileres.controller;

import com.alquileres.dto.PropietarioDTO;
import com.alquileres.service.PropietarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@RestController
@RequestMapping("/api/propietarios")
@Tag(name = "Propietarios", description = "API para gestión de propietarios")
public class PropietarioController {

    @Autowired
    private PropietarioService propietarioService;

    // GET /api/propietarios - Obtener todos los propietarios
    @GetMapping
    public ResponseEntity<List<PropietarioDTO>> obtenerTodosLosPropietarios() {
        List<PropietarioDTO> propietarios = propietarioService.obtenerTodosLosPropietarios();
        return ResponseEntity.ok(propietarios);
    }

    // GET /api/propietarios/activos - Obtener solo propietarios activos
    @GetMapping("/activos")
    public ResponseEntity<List<PropietarioDTO>> obtenerPropietariosActivos() {
        List<PropietarioDTO> propietarios = propietarioService.obtenerPropietariosActivos();
        return ResponseEntity.ok(propietarios);
    }

    // GET /api/propietarios/inactivos - Obtener solo propietarios inactivos
    @GetMapping("/inactivos")
    public ResponseEntity<List<PropietarioDTO>> obtenerPropietariosInactivos() {
        List<PropietarioDTO> propietarios = propietarioService.obtenerPropietariosInactivos();
        return ResponseEntity.ok(propietarios);
    }

    // GET /api/propietarios/{id} - Obtener propietario por ID
    @GetMapping("/{id}")
    public ResponseEntity<PropietarioDTO> obtenerPropietarioPorId(@PathVariable Long id) {
        PropietarioDTO propietario = propietarioService.obtenerPropietarioPorId(id);
        return ResponseEntity.ok(propietario);
    }

    // GET /api/propietarios/dni/{dni} - Buscar propietario por DNI
    @GetMapping("/dni/{dni}")
    public ResponseEntity<PropietarioDTO> buscarPorDni(@PathVariable String dni) {
        PropietarioDTO propietario = propietarioService.buscarPorDni(dni);
        return ResponseEntity.ok(propietario);
    }

    // GET /api/propietarios/buscar - Buscar por nombre y/o apellido
    @GetMapping("/buscar")
    public ResponseEntity<List<PropietarioDTO>> buscarPorNombreYApellido(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellido) {
        List<PropietarioDTO> propietarios = propietarioService.buscarPorNombreYApellido(nombre, apellido);
        return ResponseEntity.ok(propietarios);
    }

    // POST /api/propietarios - Crear nuevo propietario
    @PostMapping
    public ResponseEntity<PropietarioDTO> crearPropietario(@Valid @RequestBody PropietarioDTO propietarioDTO) {
        PropietarioDTO nuevoPropietario = propietarioService.crearPropietario(propietarioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPropietario);
    }

    // PUT /api/propietarios/{id} - Actualizar propietario
    @PutMapping("/{id}")
    public ResponseEntity<PropietarioDTO> actualizarPropietario(
            @PathVariable Long id,
            @Valid @RequestBody PropietarioDTO propietarioDTO) {
        PropietarioDTO propietarioActualizado = propietarioService.actualizarPropietario(id, propietarioDTO);
        return ResponseEntity.ok(propietarioActualizado);
    }

    // PATCH /api/propietarios/{id}/desactivar - Desactivar propietario (eliminación lógica)
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivarPropietario(@PathVariable Long id) {
        propietarioService.desactivarPropietario(id);
        return ResponseEntity.noContent().build();
    }

    // PATCH /api/propietarios/{id}/activar - Activar propietario inactivo
    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activarPropietario(@PathVariable Long id) {
        propietarioService.activarPropietario(id);
        return ResponseEntity.noContent().build();
    }
}
