package com.alquileres.service;

import com.alquileres.dto.EstadoContratoDTO;
import com.alquileres.model.EstadoContrato;
import com.alquileres.repository.EstadoContratoRepository;
import com.alquileres.exception.BusinessException;
import com.alquileres.exception.ErrorCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EstadoContratoService {

    @Autowired
    private EstadoContratoRepository estadoContratoRepository;

    // Obtener todos los estados de contrato
    public List<EstadoContratoDTO> obtenerTodosLosEstadosContrato() {
        List<EstadoContrato> estados = estadoContratoRepository.findAll();
        return estados.stream()
                .map(EstadoContratoDTO::new)
                .collect(Collectors.toList());
    }

    // Obtener estado de contrato por ID
    public EstadoContratoDTO obtenerEstadoContratoPorId(Integer id) {
        Optional<EstadoContrato> estado = estadoContratoRepository.findById(id);
        if (estado.isPresent()) {
            return new EstadoContratoDTO(estado.get());
        } else {
            throw new BusinessException(ErrorCodes.ESTADO_CONTRATO_NO_ENCONTRADO, "Estado de contrato no encontrado con ID: " + id, HttpStatus.NOT_FOUND);
        }
    }

    // Obtener estado de contrato por nombre
    public EstadoContratoDTO obtenerEstadoContratoPorNombre(String nombre) {
        Optional<EstadoContrato> estado = estadoContratoRepository.findByNombre(nombre);
        if (estado.isPresent()) {
            return new EstadoContratoDTO(estado.get());
        } else {
            throw new BusinessException(ErrorCodes.ESTADO_CONTRATO_NO_ENCONTRADO, "Estado de contrato no encontrado con nombre: " + nombre, HttpStatus.NOT_FOUND);
        }
    }

    // Crear nuevo estado de contrato
    public EstadoContratoDTO crearEstadoContrato(EstadoContratoDTO estadoContratoDTO) {
        // Validar nombre único
        if (estadoContratoRepository.existsByNombre(estadoContratoDTO.getNombre())) {
            throw new BusinessException(ErrorCodes.ESTADO_CONTRATO_YA_EXISTE, "Ya existe un estado de contrato con ese nombre", HttpStatus.BAD_REQUEST);
        }

        EstadoContrato estadoContrato = estadoContratoDTO.toEntity();
        EstadoContrato estadoGuardado = estadoContratoRepository.save(estadoContrato);
        return new EstadoContratoDTO(estadoGuardado);
    }

    // Actualizar estado de contrato
    public EstadoContratoDTO actualizarEstadoContrato(Integer id, EstadoContratoDTO estadoContratoDTO) {
        Optional<EstadoContrato> estadoExistente = estadoContratoRepository.findById(id);

        if (!estadoExistente.isPresent()) {
            throw new BusinessException(ErrorCodes.ESTADO_CONTRATO_NO_ENCONTRADO, "Estado de contrato no encontrado con ID: " + id, HttpStatus.NOT_FOUND);
        }

        // Validar nombre único (excluyendo el actual)
        Optional<EstadoContrato> estadoConNombre = estadoContratoRepository.findByNombre(estadoContratoDTO.getNombre());
        if (estadoConNombre.isPresent() && !estadoConNombre.get().getId().equals(id)) {
            throw new BusinessException(ErrorCodes.ESTADO_CONTRATO_YA_EXISTE, "Ya existe otro estado de contrato con ese nombre", HttpStatus.BAD_REQUEST);
        }

        EstadoContrato estado = estadoExistente.get();
        estado.setNombre(estadoContratoDTO.getNombre());

        EstadoContrato estadoActualizado = estadoContratoRepository.save(estado);
        return new EstadoContratoDTO(estadoActualizado);
    }

    // Eliminar estado de contrato
    public void eliminarEstadoContrato(Integer id) {
        Optional<EstadoContrato> estado = estadoContratoRepository.findById(id);
        if (!estado.isPresent()) {
            throw new BusinessException(ErrorCodes.ESTADO_CONTRATO_NO_ENCONTRADO, "Estado de contrato no encontrado con ID: " + id, HttpStatus.NOT_FOUND);
        }

        // Aquí podrías agregar validaciones adicionales, como verificar si hay contratos usando este estado
        // Por ahora, permitimos eliminación directa
        estadoContratoRepository.deleteById(id);
    }

    // Verificar si existe un estado de contrato
    public boolean existeEstadoContrato(Integer id) {
        return estadoContratoRepository.existsById(id);
    }

    // Verificar si existe un estado de contrato por nombre
    public boolean existeEstadoContratoPorNombre(String nombre) {
        return estadoContratoRepository.existsByNombre(nombre);
    }
}
