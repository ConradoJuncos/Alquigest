package com.alquileres.controller;

import com.alquileres.dto.TipoInmuebleDTO;
import com.alquileres.service.TipoInmuebleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-inmueble")
@Tag(name = "Tipos de Inmueble", description = "API para gestión de tipos de inmueble")
public class TipoInmuebleController {

    @Autowired
    private TipoInmuebleService tipoInmuebleService;

    @GetMapping
    @Operation(summary = "Obtener todos los tipos de inmueble")
    public ResponseEntity<List<TipoInmuebleDTO>> obtenerTodosLosTiposInmueble() {
        List<TipoInmuebleDTO> tipos = tipoInmuebleService.obtenerTodosLosTiposInmueble();
        return ResponseEntity.ok(tipos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener tipo de inmueble por ID")
    public ResponseEntity<TipoInmuebleDTO> obtenerTipoInmueblePorId(@PathVariable Long id) {
        TipoInmuebleDTO tipo = tipoInmuebleService.obtenerTipoInmueblePorId(id);
        return ResponseEntity.ok(tipo);
    }

    @PostMapping
    @Operation(summary = "Crear nuevo tipo de inmueble")
    public ResponseEntity<TipoInmuebleDTO> crearTipoInmueble(@Valid @RequestBody TipoInmuebleDTO tipoInmuebleDTO) {
        TipoInmuebleDTO nuevoTipo = tipoInmuebleService.crearTipoInmueble(tipoInmuebleDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoTipo);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar tipo de inmueble")
    public ResponseEntity<TipoInmuebleDTO> actualizarTipoInmueble(@PathVariable Long id, @Valid @RequestBody TipoInmuebleDTO tipoInmuebleDTO) {
        TipoInmuebleDTO tipoActualizado = tipoInmuebleService.actualizarTipoInmueble(id, tipoInmuebleDTO);
        return ResponseEntity.ok(tipoActualizado);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar tipo de inmueble")
    public ResponseEntity<Void> eliminarTipoInmueble(@PathVariable Long id) {
        tipoInmuebleService.eliminarTipoInmueble(id);
        return ResponseEntity.noContent().build();
    }
}
