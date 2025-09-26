package com.alquileres.controller;

import com.alquileres.dto.InquilinoDTO;
import com.alquileres.service.InquilinoService;
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
class InquilinoControllerTest {

    @Mock
    private InquilinoService inquilinoService;

    @InjectMocks
    private InquilinoController inquilinoController;

    @Test
    void obtenerTodosLosInquilinos_returnsListOfInquilinos_whenInquilinosExist() {
        List<InquilinoDTO> inquilinos = List.of(
            createInquilinoDTO(1L, "Juan", "Pérez", "20123456789"),
            createInquilinoDTO(2L, "María", "González", "27987654321")
        );
        when(inquilinoService.obtenerTodosLosInquilinos()).thenReturn(inquilinos);

        ResponseEntity<List<InquilinoDTO>> response = inquilinoController.obtenerTodosLosInquilinos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(inquilinos, response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void obtenerTodosLosInquilinos_returnsEmptyList_whenNoInquilinosExist() {
        when(inquilinoService.obtenerTodosLosInquilinos()).thenReturn(Collections.emptyList());

        ResponseEntity<List<InquilinoDTO>> response = inquilinoController.obtenerTodosLosInquilinos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void obtenerTodosLosInquilinos_throwsException_whenServiceFails() {
        when(inquilinoService.obtenerTodosLosInquilinos()).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> inquilinoController.obtenerTodosLosInquilinos());
    }

    @Test
    void obtenerInquilinosActivos_returnsActiveInquilinos_whenActiveInquilinosExist() {
        List<InquilinoDTO> inquilinos = List.of(createInquilinoDTO(1L, "Juan", "Pérez", "20123456789"));
        when(inquilinoService.obtenerInquilinosActivos()).thenReturn(inquilinos);

        ResponseEntity<List<InquilinoDTO>> response = inquilinoController.obtenerInquilinosActivos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(inquilinos, response.getBody());
        verify(inquilinoService).obtenerInquilinosActivos();
    }

    @Test
    void obtenerInquilinosInactivos_returnsInactiveInquilinos_whenInactiveInquilinosExist() {
        List<InquilinoDTO> inquilinos = List.of(createInquilinoDTO(1L, "Juan", "Pérez", "20123456789"));
        when(inquilinoService.obtenerInquilinosInactivos()).thenReturn(inquilinos);

        ResponseEntity<List<InquilinoDTO>> response = inquilinoController.obtenerInquilinosInactivos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(inquilinos, response.getBody());
        verify(inquilinoService).obtenerInquilinosInactivos();
    }

    @Test
    void obtenerInquilinoPorId_returnsInquilino_whenValidIdProvided() {
        Long id = 1L;
        InquilinoDTO inquilino = createInquilinoDTO(id, "Juan", "Pérez", "20123456789");
        when(inquilinoService.obtenerInquilinoPorId(id)).thenReturn(inquilino);

        ResponseEntity<InquilinoDTO> response = inquilinoController.obtenerInquilinoPorId(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(inquilino, response.getBody());
        assertEquals(id, response.getBody().getId());
    }

    @Test
    void obtenerInquilinoPorId_throwsException_whenInquilinoNotFound() {
        Long id = 999L;
        when(inquilinoService.obtenerInquilinoPorId(id))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Inquilino no encontrado"));

        assertThrows(ResponseStatusException.class, () -> inquilinoController.obtenerInquilinoPorId(id));
    }

    @Test
    void buscarPorCuil_returnsInquilino_whenValidCuilProvided() {
        String cuil = "20123456789";
        InquilinoDTO inquilino = createInquilinoDTO(1L, "Juan", "Pérez", cuil);
        when(inquilinoService.buscarPorCuil(cuil)).thenReturn(inquilino);

        ResponseEntity<InquilinoDTO> response = inquilinoController.buscarPorCuil(cuil);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(inquilino, response.getBody());
        assertEquals(cuil, response.getBody().getCuil());
    }

    @Test
    void buscarPorCuil_throwsException_whenCuilNotFound() {
        String cuil = "99999999999";
        when(inquilinoService.buscarPorCuil(cuil))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Inquilino no encontrado"));

        assertThrows(ResponseStatusException.class, () -> inquilinoController.buscarPorCuil(cuil));
    }

    @Test
    void buscarPorCuil_throwsException_whenInvalidCuilFormat() {
        String cuil = "123456";
        when(inquilinoService.buscarPorCuil(cuil))
            .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de CUIL inválido"));

        assertThrows(ResponseStatusException.class, () -> inquilinoController.buscarPorCuil(cuil));
    }

    @Test
    void buscarPorNombre_returnsInquilinos_whenValidNombreProvided() {
        String nombre = "Juan";
        List<InquilinoDTO> inquilinos = List.of(
            createInquilinoDTO(1L, nombre, "Pérez", "20123456789"),
            createInquilinoDTO(2L, nombre, "García", "20987654321")
        );
        when(inquilinoService.buscarPorNombre(nombre)).thenReturn(inquilinos);

        ResponseEntity<List<InquilinoDTO>> response = inquilinoController.buscarPorNombre(nombre);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(inquilinos, response.getBody());
        assertEquals(2, response.getBody().size());
        verify(inquilinoService).buscarPorNombre(nombre);
    }

    @Test
    void buscarPorNombre_returnsEmptyList_whenNoNombreMatches() {
        String nombre = "Inexistente";
        when(inquilinoService.buscarPorNombre(nombre)).thenReturn(Collections.emptyList());

        ResponseEntity<List<InquilinoDTO>> response = inquilinoController.buscarPorNombre(nombre);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void buscarPorNombre_returnsPartialMatches_whenPartialNameProvided() {
        String nombre = "Ju";
        List<InquilinoDTO> inquilinos = List.of(createInquilinoDTO(1L, "Juan", "Pérez", "20123456789"));
        when(inquilinoService.buscarPorNombre(nombre)).thenReturn(inquilinos);

        ResponseEntity<List<InquilinoDTO>> response = inquilinoController.buscarPorNombre(nombre);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(inquilinos, response.getBody());
        assertTrue(response.getBody().get(0).getNombre().contains("Ju"));
    }

    @Test
    void crearInquilino_returnsCreatedInquilino_whenValidDataProvided() {
        InquilinoDTO inputDTO = createInquilinoDTO(null, "Juan", "Pérez", "20123456789");
        InquilinoDTO createdDTO = createInquilinoDTO(1L, "Juan", "Pérez", "20123456789");
        when(inquilinoService.crearInquilino(inputDTO)).thenReturn(createdDTO);

        ResponseEntity<InquilinoDTO> response = inquilinoController.crearInquilino(inputDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdDTO, response.getBody());
        assertNotNull(response.getBody().getId());
    }

    @Test
    void crearInquilino_throwsException_whenCuilAlreadyExists() {
        InquilinoDTO inputDTO = createInquilinoDTO(null, "Juan", "Pérez", "20123456789");
        when(inquilinoService.crearInquilino(inputDTO))
            .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe un inquilino con ese CUIL"));

        assertThrows(ResponseStatusException.class, () -> inquilinoController.crearInquilino(inputDTO));
    }

    @Test
    void crearInquilino_throwsException_whenRequiredFieldsMissing() {
        InquilinoDTO inputDTO = createInquilinoDTO(null, "", "", ""); // campos requeridos vacíos
        when(inquilinoService.crearInquilino(inputDTO))
            .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Campos requeridos faltantes"));

        assertThrows(ResponseStatusException.class, () -> inquilinoController.crearInquilino(inputDTO));
    }

    @Test
    void crearInquilino_throwsException_whenInvalidCuilFormat() {
        InquilinoDTO inputDTO = createInquilinoDTO(null, "Juan", "Pérez", "123456");
        when(inquilinoService.crearInquilino(inputDTO))
            .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de CUIL inválido"));

        assertThrows(ResponseStatusException.class, () -> inquilinoController.crearInquilino(inputDTO));
    }

    @Test
    void actualizarInquilino_returnsUpdatedInquilino_whenValidDataProvided() {
        Long id = 1L;
        InquilinoDTO inputDTO = createInquilinoDTO(id, "Juan Carlos", "Pérez López", "20123456789");
        InquilinoDTO updatedDTO = createInquilinoDTO(id, "Juan Carlos", "Pérez López", "20123456789");
        when(inquilinoService.actualizarInquilino(id, inputDTO)).thenReturn(updatedDTO);

        ResponseEntity<InquilinoDTO> response = inquilinoController.actualizarInquilino(id, inputDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedDTO, response.getBody());
        assertEquals("Juan Carlos", response.getBody().getNombre());
        assertEquals("Pérez López", response.getBody().getApellido());
    }

    @Test
    void actualizarInquilino_throwsException_whenInquilinoNotFound() {
        Long id = 999L;
        InquilinoDTO inputDTO = createInquilinoDTO(id, "Juan", "Pérez", "20123456789");
        when(inquilinoService.actualizarInquilino(id, inputDTO))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Inquilino no encontrado"));

        assertThrows(ResponseStatusException.class, () -> inquilinoController.actualizarInquilino(id, inputDTO));
    }

    @Test
    void actualizarInquilino_throwsException_whenCuilAlreadyExistsForOtherInquilino() {
        Long id = 1L;
        InquilinoDTO inputDTO = createInquilinoDTO(id, "Juan", "Pérez", "20987654321"); // CUIL de otro inquilino
        when(inquilinoService.actualizarInquilino(id, inputDTO))
            .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe otro inquilino con ese CUIL"));

        assertThrows(ResponseStatusException.class, () -> inquilinoController.actualizarInquilino(id, inputDTO));
    }

    @Test
    void desactivarInquilino_returnsNoContent_whenValidIdProvided() {
        Long id = 1L;
        doNothing().when(inquilinoService).desactivarInquilino(id);

        ResponseEntity<Void> response = inquilinoController.desactivarInquilino(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(inquilinoService).desactivarInquilino(id);
    }

    @Test
    void desactivarInquilino_throwsException_whenInquilinoNotFound() {
        Long id = 999L;
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Inquilino no encontrado"))
            .when(inquilinoService).desactivarInquilino(id);

        assertThrows(ResponseStatusException.class, () -> inquilinoController.desactivarInquilino(id));
    }

    @Test
    void desactivarInquilino_throwsException_whenInquilinoHasActiveContracts() {
        Long id = 1L;
        doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "No se puede desactivar el inquilino porque tiene contratos activos"))
            .when(inquilinoService).desactivarInquilino(id);

        assertThrows(ResponseStatusException.class, () -> inquilinoController.desactivarInquilino(id));
    }

    @Test
    void eliminarInquilino_returnsNoContent_whenValidIdProvided() {
        Long id = 1L;
        doNothing().when(inquilinoService).eliminarInquilino(id);

        ResponseEntity<Void> response = inquilinoController.eliminarInquilino(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(inquilinoService).eliminarInquilino(id);
    }

    @Test
    void eliminarInquilino_throwsException_whenInquilinoNotFound() {
        Long id = 999L;
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Inquilino no encontrado"))
            .when(inquilinoService).eliminarInquilino(id);

        assertThrows(ResponseStatusException.class, () -> inquilinoController.eliminarInquilino(id));
    }

    @Test
    void eliminarInquilino_throwsException_whenInquilinoHasRelatedData() {
        Long id = 1L;
        doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "No se puede eliminar el inquilino porque tiene contratos asociados"))
            .when(inquilinoService).eliminarInquilino(id);

        assertThrows(ResponseStatusException.class, () -> inquilinoController.eliminarInquilino(id));
    }

    @Test
    void eliminarInquilino_throwsException_whenInquilinoHasActiveRentals() {
        Long id = 1L;
        doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "No se puede eliminar el inquilino porque tiene alquileres activos"))
            .when(inquilinoService).eliminarInquilino(id);

        assertThrows(ResponseStatusException.class, () -> inquilinoController.eliminarInquilino(id));
    }

    private InquilinoDTO createInquilinoDTO(Long id, String nombre, String apellido, String cuil) {
        InquilinoDTO dto = new InquilinoDTO();
        dto.setId(id);
        dto.setNombre(nombre);
        dto.setApellido(apellido);
        dto.setCuil(cuil);
        dto.setTelefono("1234567890");
        dto.setEsActivo(true);
        return dto;
    }
}
