package com.alquileres.controller;

import com.alquileres.dto.PropietarioDTO;
import com.alquileres.service.PropietarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropietarioControllerTest {

    @Mock
    private PropietarioService propietarioService;

    @InjectMocks
    private PropietarioController propietarioController;

    @Test
    void obtenerTodosLosPropietarios_returnsListOfPropietarios_whenPropietariosExist() {
        List<PropietarioDTO> propietarios = List.of(
            createPropietarioDTO(1L, "John", "Doe", "12345678"),
            createPropietarioDTO(2L, "Jane", "Smith", "87654321")
        );
        when(propietarioService.obtenerTodosLosPropietarios()).thenReturn(propietarios);

        ResponseEntity<List<PropietarioDTO>> response = propietarioController.obtenerTodosLosPropietarios();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(propietarios, response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void obtenerTodosLosPropietarios_returnsEmptyList_whenNoPropietariosExist() {
        when(propietarioService.obtenerTodosLosPropietarios()).thenReturn(Collections.emptyList());

        ResponseEntity<List<PropietarioDTO>> response = propietarioController.obtenerTodosLosPropietarios();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void obtenerTodosLosPropietarios_throwsException_whenServiceFails() {
        when(propietarioService.obtenerTodosLosPropietarios()).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> propietarioController.obtenerTodosLosPropietarios());
    }

    @Test
    void obtenerPropietariosActivos_returnsActiveProprietarios_whenActiveProprietariosExist() {
        List<PropietarioDTO> propietarios = List.of(createPropietarioDTO(1L, "John", "Doe", "12345678"));
        when(propietarioService.obtenerPropietariosActivos()).thenReturn(propietarios);

        ResponseEntity<List<PropietarioDTO>> response = propietarioController.obtenerPropietariosActivos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(propietarios, response.getBody());
        verify(propietarioService).obtenerPropietariosActivos();
    }

    @Test
    void obtenerPropietariosInactivos_returnsInactiveProprietarios_whenInactiveProprietariosExist() {
        List<PropietarioDTO> propietarios = List.of(createPropietarioDTO(1L, "John", "Doe", "12345678"));
        when(propietarioService.obtenerPropietariosInactivos()).thenReturn(propietarios);

        ResponseEntity<List<PropietarioDTO>> response = propietarioController.obtenerPropietariosInactivos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(propietarios, response.getBody());
        verify(propietarioService).obtenerPropietariosInactivos();
    }

    @Test
    void obtenerPropietarioPorId_returnsPropietario_whenValidIdProvided() {
        Long id = 1L;
        PropietarioDTO propietario = createPropietarioDTO(id, "John", "Doe", "12345678");
        when(propietarioService.obtenerPropietarioPorId(id)).thenReturn(propietario);

        ResponseEntity<PropietarioDTO> response = propietarioController.obtenerPropietarioPorId(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(propietario, response.getBody());
        assertEquals(id, response.getBody().getId());
    }

    @Test
    void obtenerPropietarioPorId_throwsException_whenPropietarioNotFound() {
        Long id = 999L;
        when(propietarioService.obtenerPropietarioPorId(id))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Propietario no encontrado"));

        assertThrows(ResponseStatusException.class, () -> propietarioController.obtenerPropietarioPorId(id));
    }

    @Test
    void buscarPorDni_returnsPropietario_whenValidDniProvided() {
        String dni = "12345678";
        PropietarioDTO propietario = createPropietarioDTO(1L, "John", "Doe", dni);
        when(propietarioService.buscarPorDni(dni)).thenReturn(propietario);

        ResponseEntity<PropietarioDTO> response = propietarioController.buscarPorDni(dni);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(propietario, response.getBody());
        assertEquals(dni, response.getBody().getDni());
    }

    @Test
    void buscarPorDni_throwsException_whenDniNotFound() {
        String dni = "99999999";
        when(propietarioService.buscarPorDni(dni))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Propietario no encontrado"));

        assertThrows(ResponseStatusException.class, () -> propietarioController.buscarPorDni(dni));
    }

    @Test
    void buscarPorNombreYApellido_returnsPropietarios_whenBothParametersProvided() {
        String nombre = "John";
        String apellido = "Doe";
        List<PropietarioDTO> propietarios = List.of(createPropietarioDTO(1L, nombre, apellido, "12345678"));
        when(propietarioService.buscarPorNombreYApellido(nombre, apellido)).thenReturn(propietarios);

        ResponseEntity<List<PropietarioDTO>> response = propietarioController.buscarPorNombreYApellido(nombre, apellido);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(propietarios, response.getBody());
        verify(propietarioService).buscarPorNombreYApellido(nombre, apellido);
    }

    @Test
    void buscarPorNombreYApellido_returnsPropietarios_whenOnlyNombreProvided() {
        String nombre = "John";
        List<PropietarioDTO> propietarios = List.of(createPropietarioDTO(1L, nombre, "Doe", "12345678"));
        when(propietarioService.buscarPorNombreYApellido(nombre, null)).thenReturn(propietarios);

        ResponseEntity<List<PropietarioDTO>> response = propietarioController.buscarPorNombreYApellido(nombre, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(propietarios, response.getBody());
        verify(propietarioService).buscarPorNombreYApellido(nombre, null);
    }

    @Test
    void buscarPorNombreYApellido_returnsPropietarios_whenOnlyApellidoProvided() {
        String apellido = "Doe";
        List<PropietarioDTO> propietarios = List.of(createPropietarioDTO(1L, "John", apellido, "12345678"));
        when(propietarioService.buscarPorNombreYApellido(null, apellido)).thenReturn(propietarios);

        ResponseEntity<List<PropietarioDTO>> response = propietarioController.buscarPorNombreYApellido(null, apellido);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(propietarios, response.getBody());
        verify(propietarioService).buscarPorNombreYApellido(null, apellido);
    }

    @Test
    void buscarPorNombreYApellido_returnsEmptyList_whenNoParametersProvided() {
        when(propietarioService.buscarPorNombreYApellido(null, null)).thenReturn(Collections.emptyList());

        ResponseEntity<List<PropietarioDTO>> response = propietarioController.buscarPorNombreYApellido(null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void crearPropietario_returnsCreatedPropietario_whenValidDataProvided() {
        PropietarioDTO inputDTO = createPropietarioDTO(null, "John", "Doe", "12345678");
        PropietarioDTO createdDTO = createPropietarioDTO(1L, "John", "Doe", "12345678");
        when(propietarioService.crearPropietario(inputDTO)).thenReturn(createdDTO);

        ResponseEntity<PropietarioDTO> response = propietarioController.crearPropietario(inputDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdDTO, response.getBody());
        assertNotNull(response.getBody().getId());
    }

    @Test
    void crearPropietario_throwsException_whenDniAlreadyExists() {
        PropietarioDTO inputDTO = createPropietarioDTO(null, "John", "Doe", "12345678");
        when(propietarioService.crearPropietario(inputDTO))
            .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe un propietario con ese DNI"));

        assertThrows(ResponseStatusException.class, () -> propietarioController.crearPropietario(inputDTO));
    }

    @Test
    void actualizarPropietario_returnsUpdatedPropietario_whenValidDataProvided() {
        Long id = 1L;
        PropietarioDTO inputDTO = createPropietarioDTO(id, "John", "Smith", "12345678");
        PropietarioDTO updatedDTO = createPropietarioDTO(id, "John", "Smith", "12345678");
        when(propietarioService.actualizarPropietario(id, inputDTO)).thenReturn(updatedDTO);

        ResponseEntity<PropietarioDTO> response = propietarioController.actualizarPropietario(id, inputDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedDTO, response.getBody());
        assertEquals("Smith", response.getBody().getApellido());
    }

    @Test
    void actualizarPropietario_throwsException_whenPropietarioNotFound() {
        Long id = 999L;
        PropietarioDTO inputDTO = createPropietarioDTO(id, "John", "Smith", "12345678");
        when(propietarioService.actualizarPropietario(id, inputDTO))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Propietario no encontrado"));

        assertThrows(ResponseStatusException.class, () -> propietarioController.actualizarPropietario(id, inputDTO));
    }

    @Test
    void desactivarPropietario_returnsNoContent_whenValidIdProvided() {
        Long id = 1L;
        doNothing().when(propietarioService).desactivarPropietario(id);

        ResponseEntity<Void> response = propietarioController.desactivarPropietario(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(propietarioService).desactivarPropietario(id);
    }

    @Test
    void desactivarPropietario_throwsException_whenPropietarioNotFound() {
        Long id = 999L;
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Propietario no encontrado"))
            .when(propietarioService).desactivarPropietario(id);

        assertThrows(ResponseStatusException.class, () -> propietarioController.desactivarPropietario(id));
    }

    @Test
    void eliminarPropietario_returnsNoContent_whenValidIdProvided() {
        Long id = 1L;
        doNothing().when(propietarioService).eliminarPropietario(id);

        ResponseEntity<Void> response = propietarioController.eliminarPropietario(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(propietarioService).eliminarPropietario(id);
    }

    @Test
    void eliminarPropietario_throwsException_whenPropietarioNotFound() {
        Long id = 999L;
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Propietario no encontrado"))
            .when(propietarioService).eliminarPropietario(id);

        assertThrows(ResponseStatusException.class, () -> propietarioController.eliminarPropietario(id));
    }

    @Test
    void eliminarPropietario_throwsException_whenPropietarioHasRelatedData() {
        Long id = 1L;
        doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "No se puede eliminar el propietario porque tiene inmuebles asociados"))
            .when(propietarioService).eliminarPropietario(id);

        assertThrows(ResponseStatusException.class, () -> propietarioController.eliminarPropietario(id));
    }

    private PropietarioDTO createPropietarioDTO(Long id, String nombre, String apellido, String dni) {
        PropietarioDTO dto = new PropietarioDTO();
        dto.setId(id);
        dto.setNombre(nombre);
        dto.setApellido(apellido);
        dto.setDni(dni);
        dto.setEsActivo(true);
        return dto;
    }
}
