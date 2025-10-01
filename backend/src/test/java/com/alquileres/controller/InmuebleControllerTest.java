package com.alquileres.controller;

import com.alquileres.dto.InmuebleDTO;
import com.alquileres.service.InmuebleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InmuebleControllerTest {

    @Mock
    private InmuebleService inmuebleService;

    @InjectMocks
    private InmuebleController inmuebleController;

    @Test
    void obtenerTodosLosInmuebles_returnsListOfInmuebles_whenInmueblesExist() {
        List<InmuebleDTO> inmuebles = List.of(
            createInmuebleDTO(1L, "Av. Corrientes 1234", 1L, "Departamento"),
            createInmuebleDTO(2L, "Av. Santa Fe 5678", 2L, "Casa")
        );
        when(inmuebleService.obtenerTodosLosInmuebles()).thenReturn(inmuebles);

        ResponseEntity<List<InmuebleDTO>> response = inmuebleController.obtenerTodosLosInmuebles();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(inmuebles, response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void obtenerTodosLosInmuebles_returnsEmptyList_whenNoInmueblesExist() {
        when(inmuebleService.obtenerTodosLosInmuebles()).thenReturn(Collections.emptyList());

        ResponseEntity<List<InmuebleDTO>> response = inmuebleController.obtenerTodosLosInmuebles();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void obtenerTodosLosInmuebles_throwsException_whenServiceFails() {
        when(inmuebleService.obtenerTodosLosInmuebles()).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> inmuebleController.obtenerTodosLosInmuebles());
    }

    @Test
    void obtenerInmueblesActivos_returnsActiveInmuebles_whenActiveInmueblesExist() {
        List<InmuebleDTO> inmuebles = List.of(createInmuebleDTO(1L, "Av. Corrientes 1234", 1L, "Departamento"));
        when(inmuebleService.obtenerInmueblesActivos()).thenReturn(inmuebles);

        ResponseEntity<List<InmuebleDTO>> response = inmuebleController.obtenerInmueblesActivos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(inmuebles, response.getBody());
        verify(inmuebleService).obtenerInmueblesActivos();
    }

    @Test
    void obtenerInmueblesInactivos_returnsInactiveInmuebles_whenInactiveInmueblesExist() {
        List<InmuebleDTO> inmuebles = List.of(createInmuebleDTO(1L, "Av. Corrientes 1234", 1L, "Departamento"));
        when(inmuebleService.obtenerInmueblesInactivos()).thenReturn(inmuebles);

        ResponseEntity<List<InmuebleDTO>> response = inmuebleController.obtenerInmueblesInactivos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(inmuebles, response.getBody());
        verify(inmuebleService).obtenerInmueblesInactivos();
    }

    @Test
    void obtenerInmueblesDisponibles_returnsAvailableInmuebles_whenAvailableInmueblesExist() {
        List<InmuebleDTO> inmuebles = List.of(createInmuebleDTO(1L, "Av. Corrientes 1234", 1L, "Departamento"));
        when(inmuebleService.obtenerInmueblesDisponibles()).thenReturn(inmuebles);

        ResponseEntity<List<InmuebleDTO>> response = inmuebleController.obtenerInmueblesDisponibles();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(inmuebles, response.getBody());
        verify(inmuebleService).obtenerInmueblesDisponibles();
    }

    @Test
    void obtenerInmueblePorId_returnsInmueble_whenValidIdProvided() {
        Long id = 1L;
        InmuebleDTO inmueble = createInmuebleDTO(id, "Av. Corrientes 1234", 1L, "Departamento");
        when(inmuebleService.obtenerInmueblePorId(id)).thenReturn(inmueble);

        ResponseEntity<InmuebleDTO> response = inmuebleController.obtenerInmueblePorId(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(inmueble, response.getBody());
        assertEquals(id, response.getBody().getId());
    }

    @Test
    void obtenerInmueblePorId_throwsException_whenInmuebleNotFound() {
        Long id = 999L;
        when(inmuebleService.obtenerInmueblePorId(id))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Inmueble no encontrado"));

        assertThrows(ResponseStatusException.class, () -> inmuebleController.obtenerInmueblePorId(id));
    }

    @Test
    void buscarPorPropietario_returnsInmuebles_whenValidPropietarioIdProvided() {
        Long propietarioId = 1L;
        List<InmuebleDTO> inmuebles = List.of(
            createInmuebleDTO(1L, "Av. Corrientes 1234", propietarioId, "Departamento"),
            createInmuebleDTO(2L, "Av. Santa Fe 5678", propietarioId, "Casa")
        );
        when(inmuebleService.buscarPorPropietario(propietarioId)).thenReturn(inmuebles);

        ResponseEntity<List<InmuebleDTO>> response = inmuebleController.buscarPorPropietario(propietarioId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(inmuebles, response.getBody());
        assertEquals(2, response.getBody().size());
        verify(inmuebleService).buscarPorPropietario(propietarioId);
    }

    @Test
    void buscarPorPropietario_returnsEmptyList_whenPropietarioHasNoInmuebles() {
        Long propietarioId = 999L;
        when(inmuebleService.buscarPorPropietario(propietarioId)).thenReturn(Collections.emptyList());

        ResponseEntity<List<InmuebleDTO>> response = inmuebleController.buscarPorPropietario(propietarioId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void buscarPorDireccion_returnsInmuebles_whenValidDireccionProvided() {
        String direccion = "Corrientes";
        List<InmuebleDTO> inmuebles = List.of(createInmuebleDTO(1L, "Av. Corrientes 1234", 1L, "Departamento"));
        when(inmuebleService.buscarPorDireccion(direccion)).thenReturn(inmuebles);

        ResponseEntity<List<InmuebleDTO>> response = inmuebleController.buscarPorDireccion(direccion);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(inmuebles, response.getBody());
        verify(inmuebleService).buscarPorDireccion(direccion);
    }

    @Test
    void buscarPorDireccion_returnsEmptyList_whenNoDireccionMatches() {
        String direccion = "Inexistente";
        when(inmuebleService.buscarPorDireccion(direccion)).thenReturn(Collections.emptyList());

        ResponseEntity<List<InmuebleDTO>> response = inmuebleController.buscarPorDireccion(direccion);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void crearInmueble_returnsCreatedInmueble_whenValidDataProvided() {
        InmuebleDTO inputDTO = createInmuebleDTO(null, "Av. Corrientes 1234", 1L, "Departamento");
        InmuebleDTO createdDTO = createInmuebleDTO(1L, "Av. Corrientes 1234", 1L, "Departamento");
        when(inmuebleService.crearInmueble(inputDTO)).thenReturn(createdDTO);

        ResponseEntity<InmuebleDTO> response = inmuebleController.crearInmueble(inputDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdDTO, response.getBody());
        assertNotNull(response.getBody().getId());
    }

    @Test
    void crearInmueble_throwsException_whenPropietarioNotExists() {
        InmuebleDTO inputDTO = createInmuebleDTO(null, "Av. Corrientes 1234", 999L, "Departamento");
        when(inmuebleService.crearInmueble(inputDTO))
            .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "No existe el propietario indicado"));

        assertThrows(ResponseStatusException.class, () -> inmuebleController.crearInmueble(inputDTO));
    }

    @Test
    void crearInmueble_throwsException_whenInvalidDataProvided() {
        InmuebleDTO inputDTO = createInmuebleDTO(null, "", 1L, ""); // datos inválidos
        when(inmuebleService.crearInmueble(inputDTO))
            .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datos inválidos"));

        assertThrows(ResponseStatusException.class, () -> inmuebleController.crearInmueble(inputDTO));
    }

    @Test
    void actualizarInmueble_returnsUpdatedInmueble_whenValidDataProvided() {
        Long id = 1L;
        InmuebleDTO inputDTO = createInmuebleDTO(id, "Av. Santa Fe 5678", 1L, "Casa");
        InmuebleDTO updatedDTO = createInmuebleDTO(id, "Av. Santa Fe 5678", 1L, "Casa");
        when(inmuebleService.actualizarInmueble(id, inputDTO)).thenReturn(updatedDTO);

        ResponseEntity<InmuebleDTO> response = inmuebleController.actualizarInmueble(id, inputDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedDTO, response.getBody());
        assertEquals("Av. Santa Fe 5678", response.getBody().getDireccion());
    }

    @Test
    void actualizarInmueble_throwsException_whenInmuebleNotFound() {
        Long id = 999L;
        InmuebleDTO inputDTO = createInmuebleDTO(id, "Av. Santa Fe 5678", 1L, "Casa");
        when(inmuebleService.actualizarInmueble(id, inputDTO))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Inmueble no encontrado"));

        assertThrows(ResponseStatusException.class, () -> inmuebleController.actualizarInmueble(id, inputDTO));
    }

    @Test
    void cambiarEstadoAlquiler_returnsUpdatedInmueble_whenValidDataProvided() {
        Long id = 1L;
        Boolean esAlquilado = true;
        InmuebleDTO updatedDTO = createInmuebleDTO(id, "Av. Corrientes 1234", 1L, "Departamento");
        updatedDTO.setEsAlquilado(esAlquilado);
        when(inmuebleService.cambiarEstadoAlquiler(id, esAlquilado)).thenReturn(updatedDTO);

        ResponseEntity<InmuebleDTO> response = inmuebleController.cambiarEstadoAlquiler(id, esAlquilado);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedDTO, response.getBody());
        assertEquals(esAlquilado, response.getBody().getEsAlquilado());
        verify(inmuebleService).cambiarEstadoAlquiler(id, esAlquilado);
    }

    @Test
    void cambiarEstadoAlquiler_throwsException_whenInmuebleNotFound() {
        Long id = 999L;
        Boolean esAlquilado = true;
        when(inmuebleService.cambiarEstadoAlquiler(id, esAlquilado))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Inmueble no encontrado"));

        assertThrows(ResponseStatusException.class, () -> inmuebleController.cambiarEstadoAlquiler(id, esAlquilado));
    }

    @Test
    void desactivarInmueble_returnsNoContent_whenValidIdProvided() {
        Long id = 1L;
        doNothing().when(inmuebleService).desactivarInmueble(id);

        ResponseEntity<Void> response = inmuebleController.desactivarInmueble(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(inmuebleService).desactivarInmueble(id);
    }

    @Test
    void desactivarInmueble_throwsException_whenInmuebleNotFound() {
        Long id = 999L;
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Inmueble no encontrado"))
            .when(inmuebleService).desactivarInmueble(id);

        assertThrows(ResponseStatusException.class, () -> inmuebleController.desactivarInmueble(id));
    }

    private InmuebleDTO createInmuebleDTO(Long id, String direccion, Long propietarioId, String tipo) {
        InmuebleDTO dto = new InmuebleDTO();
        dto.setId(id);
        dto.setDireccion(direccion);
        dto.setPropietarioId(propietarioId);
        dto.setTipoInmuebleId(1L);
        dto.setEstado(1);
        dto.setSuperficie(BigDecimal.valueOf(100.0));
        dto.setEsAlquilado(false);
        dto.setEsActivo(true);
        return dto;
    }
}
