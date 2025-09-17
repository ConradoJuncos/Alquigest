package com.alquileres.service;

import com.alquileres.dto.InquilinoDTO;
import com.alquileres.model.Inquilino;
import com.alquileres.repository.InquilinoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InquilinoService {

    @Autowired
    private InquilinoRepository inquilinoRepository;

    // Obtener todos los inquilinos
    public List<InquilinoDTO> obtenerTodosLosInquilinos() {
        return inquilinoRepository.findAll()
                .stream()
                .map(InquilinoDTO::new)
                .collect(Collectors.toList());
    }

    // Obtener inquilino por ID
    public InquilinoDTO obtenerInquilinoPorId(Long id) {
        Optional<Inquilino> inquilino = inquilinoRepository.findById(id);
        if (inquilino.isPresent()) {
            return new InquilinoDTO(inquilino.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Inquilino no encontrado con ID: " + id);
        }
    }

    // Crear nuevo inquilino
    public InquilinoDTO crearInquilino(InquilinoDTO inquilinoDTO) {
        // Validar CUIL único si se proporciona
        if (inquilinoDTO.getCuil() != null && !inquilinoDTO.getCuil().trim().isEmpty()) {
            if (inquilinoRepository.findByCuil(inquilinoDTO.getCuil()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe un inquilino con ese CUIL");
            }
        }

        // Validar teléfono único si se proporciona
        if (inquilinoDTO.getTelefono() != null && !inquilinoDTO.getTelefono().trim().isEmpty()) {
            if (inquilinoRepository.findByTelefono(inquilinoDTO.getTelefono()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe un inquilino con ese teléfono");
            }
        }

        Inquilino inquilino = inquilinoDTO.toEntity();
        Inquilino inquilinoGuardado = inquilinoRepository.save(inquilino);
        return new InquilinoDTO(inquilinoGuardado);
    }

    // Actualizar inquilino existente
    public InquilinoDTO actualizarInquilino(Long id, InquilinoDTO inquilinoDTO) {
        Optional<Inquilino> inquilinoExistente = inquilinoRepository.findById(id);

        if (!inquilinoExistente.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Inquilino no encontrado con ID: " + id);
        }

        Inquilino inquilino = inquilinoExistente.get();

        // Validar CUIL único si se va a actualizar y es diferente al actual
        if (inquilinoDTO.getCuil() != null && !inquilinoDTO.getCuil().trim().isEmpty()) {
            if (!inquilinoDTO.getCuil().equals(inquilino.getCuil())) {
                if (inquilinoRepository.findByCuil(inquilinoDTO.getCuil()).isPresent()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe un inquilino con ese CUIL");
                }
            }
        }

        // Validar teléfono único si se va a actualizar y es diferente al actual
        if (inquilinoDTO.getTelefono() != null && !inquilinoDTO.getTelefono().trim().isEmpty()) {
            if (!inquilinoDTO.getTelefono().equals(inquilino.getTelefono())) {
                if (inquilinoRepository.findByTelefono(inquilinoDTO.getTelefono()).isPresent()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe un inquilino con ese teléfono");
                }
            }
        }

        // Actualizar campos
        inquilinoDTO.updateEntity(inquilino);
        Inquilino inquilinoActualizado = inquilinoRepository.save(inquilino);
        return new InquilinoDTO(inquilinoActualizado);
    }

    // Eliminar inquilino por ID
    public void eliminarInquilino(Long id) {
        if (!inquilinoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Inquilino no encontrado con ID: " + id);
        }
        inquilinoRepository.deleteById(id);
    }

    // Buscar inquilinos por nombre
    public List<InquilinoDTO> buscarInquilinosPorNombre(String nombre) {
        return inquilinoRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(InquilinoDTO::new)
                .collect(Collectors.toList());
    }

    // Obtener inquilinos activos
    public List<InquilinoDTO> obtenerInquilinosActivos() {
        return inquilinoRepository.findByEsActivoTrue()
                .stream()
                .map(InquilinoDTO::new)
                .collect(Collectors.toList());
    }

    // Obtener inquilinos inactivos
    public List<InquilinoDTO> obtenerInquilinosInactivos() {
        return inquilinoRepository.findByEsActivoFalse()
                .stream()
                .map(InquilinoDTO::new)
                .collect(Collectors.toList());
    }

    // Activar/desactivar inquilino
    public InquilinoDTO cambiarEstadoInquilino(Long id, Boolean esActivo) {
        Optional<Inquilino> inquilinoExistente = inquilinoRepository.findById(id);

        if (!inquilinoExistente.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Inquilino no encontrado con ID: " + id);
        }

        Inquilino inquilino = inquilinoExistente.get();
        inquilino.setEsActivo(esActivo);
        Inquilino inquilinoActualizado = inquilinoRepository.save(inquilino);
        return new InquilinoDTO(inquilinoActualizado);
    }

    // Buscar inquilino por CUIL
    public InquilinoDTO buscarPorCuil(String cuil) {
        Optional<Inquilino> inquilino = inquilinoRepository.findByCuil(cuil);
        if (inquilino.isPresent()) {
            return new InquilinoDTO(inquilino.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Inquilino no encontrado con CUIL: " + cuil);
        }
    }

    // Contar inquilinos activos
    public Long contarInquilinosActivos() {
        return inquilinoRepository.countInquilinosActivos();
    }
}
