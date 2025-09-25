package com.alquileres.controller;

import com.alquileres.dto.InquilinoDTO;
import com.alquileres.service.InquilinoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@RestController
@RequestMapping("/api/inquilinos")
@Tag(name = "Inquilinos", description = "API para gestión de inquilinos")
public class InquilinoController {

    @Autowired
    private InquilinoService inquilinoService;

    // GET /api/inquilinos - Obtener todos los inquilinos
    @GetMapping
    public ResponseEntity<List<InquilinoDTO>> obtenerTodosLosInquilinos() {
        List<InquilinoDTO> inquilinos = inquilinoService.obtenerTodosLosInquilinos();
        return ResponseEntity.ok(inquilinos);
    }

    // GET /api/inquilinos/activos - Obtener solo inquilinos activos
    @GetMapping("/activos")
    public ResponseEntity<List<InquilinoDTO>> obtenerInquilinosActivos() {
        List<InquilinoDTO> inquilinos = inquilinoService.obtenerInquilinosActivos();
        return ResponseEntity.ok(inquilinos);
    }

    // GET /api/inquilinos/inactivos - Obtener solo inquilinos inactivos
    @GetMapping("/inactivos")
    public ResponseEntity<List<InquilinoDTO>> obtenerInquilinosInactivos() {
        List<InquilinoDTO> inquilinos = inquilinoService.obtenerInquilinosInactivos();
        return ResponseEntity.ok(inquilinos);
    }

    // GET /api/inquilinos/{id} - Obtener inquilino por ID
    @GetMapping("/{id}")
    public ResponseEntity<InquilinoDTO> obtenerInquilinoPorId(@PathVariable Long id) {
        InquilinoDTO inquilino = inquilinoService.obtenerInquilinoPorId(id);
        return ResponseEntity.ok(inquilino);
    }

    // GET /api/inquilinos/buscar/cuil/{cuil} - Buscar inquilino por CUIL
    @GetMapping("/buscar/cuil/{cuil}")
    public ResponseEntity<InquilinoDTO> buscarPorCuil(@PathVariable String cuil) {
        InquilinoDTO inquilino = inquilinoService.buscarPorCuil(cuil);
        return ResponseEntity.ok(inquilino);
    }

    // GET /api/inquilinos/buscar/nombre - Buscar inquilinos por nombre
    @GetMapping("/buscar/nombre")
    public ResponseEntity<List<InquilinoDTO>> buscarPorNombre(@RequestParam String nombre) {
        List<InquilinoDTO> inquilinos = inquilinoService.buscarPorNombre(nombre);
        return ResponseEntity.ok(inquilinos);
    }

    // POST /api/inquilinos - Crear nuevo inquilino
    @PostMapping
    public ResponseEntity<InquilinoDTO> crearInquilino(@RequestBody InquilinoDTO inquilinoDTO) {
        InquilinoDTO inquilinoCreado = inquilinoService.crearInquilino(inquilinoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(inquilinoCreado);
    }

    // PUT /api/inquilinos/{id} - Actualizar inquilino completo
    @PutMapping("/{id}")
    public ResponseEntity<InquilinoDTO> actualizarInquilino(
            @PathVariable Long id,
            @RequestBody InquilinoDTO inquilinoDTO) {
        InquilinoDTO inquilinoActualizado = inquilinoService.actualizarInquilino(id, inquilinoDTO);
        return ResponseEntity.ok(inquilinoActualizado);
    }

    // PATCH /api/inquilinos/{id}/desactivar - Desactivar inquilino (eliminación lógica)
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivarInquilino(@PathVariable Long id) {
        inquilinoService.desactivarInquilino(id);
        return ResponseEntity.noContent().build();
    }

    // DELETE /api/inquilinos/{id} - Eliminar inquilino físicamente
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarInquilino(@PathVariable Long id) {
        inquilinoService.eliminarInquilino(id);
        return ResponseEntity.noContent().build();
    }
}
