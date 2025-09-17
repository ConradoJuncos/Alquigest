package com.alquileres.controller;

import com.alquileres.dto.InquilinoDTO;
import com.alquileres.service.InquilinoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inquilinos")
@CrossOrigin(origins = "*") // CAMBIAR DESPUES
@Tag(name = "Inquilinos", description = "API para gestión de inquilinos")
public class InquilinoController {

    @Autowired
    private InquilinoService inquilinoService;

    @GetMapping
    @Operation(summary = "Obtener todos los inquilinos", description = "Retorna una lista de todos los inquilinos registrados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de inquilinos obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<InquilinoDTO>> obtenerTodosLosInquilinos() {
        List<InquilinoDTO> inquilinos = inquilinoService.obtenerTodosLosInquilinos();
        return ResponseEntity.ok(inquilinos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener inquilino por ID", description = "Retorna un inquilino específico por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inquilino encontrado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Inquilino no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<InquilinoDTO> obtenerInquilinoPorId(
            @Parameter(description = "ID del inquilino", required = true)
            @PathVariable Long id) {
        InquilinoDTO inquilino = inquilinoService.obtenerInquilinoPorId(id);
        return ResponseEntity.ok(inquilino);
    }

    @PostMapping
    @Operation(summary = "Crear nuevo inquilino", description = "Crea un nuevo inquilino en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Inquilino creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o CUIL/teléfono duplicado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<InquilinoDTO> crearInquilino(
            @Parameter(description = "Datos del inquilino a crear", required = true)
            @Valid @RequestBody InquilinoDTO inquilinoDTO) {
        InquilinoDTO inquilinoCreado = inquilinoService.crearInquilino(inquilinoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(inquilinoCreado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar inquilino", description = "Actualiza los datos de un inquilino existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inquilino actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o CUIL/teléfono duplicado"),
        @ApiResponse(responseCode = "404", description = "Inquilino no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<InquilinoDTO> actualizarInquilino(
            @Parameter(description = "ID del inquilino", required = true)
            @PathVariable Long id,
            @Parameter(description = "Datos actualizados del inquilino", required = true)
            @Valid @RequestBody InquilinoDTO inquilinoDTO) {
        InquilinoDTO inquilinoActualizado = inquilinoService.actualizarInquilino(id, inquilinoDTO);
        return ResponseEntity.ok(inquilinoActualizado);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar inquilino", description = "Elimina un inquilino del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Inquilino eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Inquilino no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> eliminarInquilino(
            @Parameter(description = "ID del inquilino", required = true)
            @PathVariable Long id) {
        inquilinoService.eliminarInquilino(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar inquilinos por nombre", description = "Busca inquilinos que contengan el nombre especificado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<InquilinoDTO>> buscarInquilinosPorNombre(
            @Parameter(description = "Nombre o parte del nombre a buscar", required = true)
            @RequestParam String nombre) {
        List<InquilinoDTO> inquilinos = inquilinoService.buscarInquilinosPorNombre(nombre);
        return ResponseEntity.ok(inquilinos);
    }

    @GetMapping("/activos")
    @Operation(summary = "Obtener inquilinos activos", description = "Retorna todos los inquilinos activos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de inquilinos activos obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<InquilinoDTO>> obtenerInquilinosActivos() {
        List<InquilinoDTO> inquilinos = inquilinoService.obtenerInquilinosActivos();
        return ResponseEntity.ok(inquilinos);
    }

    @GetMapping("/inactivos")
    @Operation(summary = "Obtener inquilinos inactivos", description = "Retorna todos los inquilinos inactivos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de inquilinos inactivos obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<InquilinoDTO>> obtenerInquilinosInactivos() {
        List<InquilinoDTO> inquilinos = inquilinoService.obtenerInquilinosInactivos();
        return ResponseEntity.ok(inquilinos);
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado del inquilino", description = "Activa o desactiva un inquilino")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado del inquilino cambiado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Inquilino no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<InquilinoDTO> cambiarEstadoInquilino(
            @Parameter(description = "ID del inquilino", required = true)
            @PathVariable Long id,
            @Parameter(description = "Nuevo estado (true=activo, false=inactivo)", required = true)
            @RequestParam Boolean esActivo) {
        InquilinoDTO inquilinoActualizado = inquilinoService.cambiarEstadoInquilino(id, esActivo);
        return ResponseEntity.ok(inquilinoActualizado);
    }

    @GetMapping("/cuil/{cuil}")
    @Operation(summary = "Buscar inquilino por CUIL", description = "Busca un inquilino específico por su CUIL")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inquilino encontrado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Inquilino no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<InquilinoDTO> buscarPorCuil(
            @Parameter(description = "CUIL del inquilino", required = true)
            @PathVariable String cuil) {
        InquilinoDTO inquilino = inquilinoService.buscarPorCuil(cuil);
        return ResponseEntity.ok(inquilino);
    }

    @GetMapping("/count/activos")
    @Operation(summary = "Contar inquilinos activos", description = "Retorna el número total de inquilinos activos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Conteo realizado exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Long> contarInquilinosActivos() {
        Long count = inquilinoService.contarInquilinosActivos();
        return ResponseEntity.ok(count);
    }
}
