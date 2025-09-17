package com.alquileres.controller;

import com.alquileres.dto.PropiedadDTO;
import com.alquileres.service.PropiedadService;
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
@RequestMapping("/api/propiedades")
@Tag(name = "Propiedades", description = "API para gestión de propiedades en alquiler")
@CrossOrigin(origins = "http://localhost:3000")
public class PropiedadController {

    @Autowired
    private PropiedadService propiedadService;

    @GetMapping
    @Operation(summary = "Obtener todas las propiedades", description = "Retorna una lista de todas las propiedades registradas")
    public ResponseEntity<List<PropiedadDTO>> obtenerTodasLasPropiedades() {
        List<PropiedadDTO> propiedades = propiedadService.obtenerTodasLasPropiedades();
        return ResponseEntity.ok(propiedades);
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Obtener propiedades disponibles", description = "Retorna solo las propiedades que están disponibles para alquilar")
    public ResponseEntity<List<PropiedadDTO>> obtenerPropiedadesDisponibles() {
        List<PropiedadDTO> propiedades = propiedadService.obtenerPropiedadesDisponibles();
        return ResponseEntity.ok(propiedades);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener propiedad por ID", description = "Retorna una propiedad específica por su ID")
    public ResponseEntity<PropiedadDTO> obtenerPropiedadPorId(
            @Parameter(description = "ID de la propiedad") @PathVariable Long id) {
        Optional<PropiedadDTO> propiedad = propiedadService.obtenerPropiedadPorId(id);
        return propiedad.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear nueva propiedad", description = "Crea una nueva propiedad en el sistema")
    public ResponseEntity<PropiedadDTO> crearPropiedad(@Valid @RequestBody PropiedadDTO propiedadDTO) {
        PropiedadDTO nuevaPropiedad = propiedadService.crearPropiedad(propiedadDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaPropiedad);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar propiedad", description = "Actualiza una propiedad existente")
    public ResponseEntity<PropiedadDTO> actualizarPropiedad(
            @Parameter(description = "ID de la propiedad") @PathVariable Long id,
            @Valid @RequestBody PropiedadDTO propiedadDTO) {
        Optional<PropiedadDTO> propiedadActualizada = propiedadService.actualizarPropiedad(id, propiedadDTO);
        return propiedadActualizada.map(ResponseEntity::ok)
                                  .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar propiedad", description = "Elimina una propiedad del sistema")
    public ResponseEntity<Void> eliminarPropiedad(
            @Parameter(description = "ID de la propiedad") @PathVariable Long id) {
        boolean eliminada = propiedadService.eliminarPropiedad(id);
        return eliminada ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar propiedades por texto", description = "Busca propiedades que contengan el texto en título o descripción")
    public ResponseEntity<List<PropiedadDTO>> buscarPropiedadesPorTexto(
            @Parameter(description = "Texto a buscar") @RequestParam String texto) {
        List<PropiedadDTO> propiedades = propiedadService.buscarPropiedadesPorTexto(texto);
        return ResponseEntity.ok(propiedades);
    }

    @GetMapping("/precio")
    @Operation(summary = "Buscar por rango de precio", description = "Busca propiedades dentro de un rango de precios")
    public ResponseEntity<List<PropiedadDTO>> buscarPorRangoPrecio(
            @Parameter(description = "Precio mínimo") @RequestParam BigDecimal precioMin,
            @Parameter(description = "Precio máximo") @RequestParam BigDecimal precioMax) {
        List<PropiedadDTO> propiedades = propiedadService.buscarPropiedadesPorRangoPrecio(precioMin, precioMax);
        return ResponseEntity.ok(propiedades);
    }

    @PatchMapping("/{id}/disponibilidad")
    @Operation(summary = "Cambiar disponibilidad", description = "Cambia el estado de disponibilidad de una propiedad")
    public ResponseEntity<PropiedadDTO> cambiarDisponibilidad(
            @Parameter(description = "ID de la propiedad") @PathVariable Long id,
            @Parameter(description = "Nueva disponibilidad") @RequestParam boolean disponible) {
        Optional<PropiedadDTO> propiedadActualizada = propiedadService.cambiarDisponibilidad(id, disponible);
        return propiedadActualizada.map(ResponseEntity::ok)
                                  .orElse(ResponseEntity.notFound().build());
    }
}
