package com.alquileres.service;

import com.alquileres.dto.CancelacionContratoDTO;
import com.alquileres.model.CancelacionContrato;
import com.alquileres.repository.CancelacionContratoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de cancelaciones de contratos
 */
@Service
public class CancelacionContratoService {

    @Autowired
    private CancelacionContratoRepository cancelacionContratoRepository;

    /**
     * Convierte una entidad CancelacionContrato a DTO
     */
    private CancelacionContratoDTO convertirADTO(CancelacionContrato cancelacion) {
        return new CancelacionContratoDTO(
            cancelacion.getId(),
            cancelacion.getContrato().getId(),
            cancelacion.getFechaCancelacion(),
            cancelacion.getMotivoCancelacion().getNombre(),
            cancelacion.getObservaciones()
        );
    }

    /**
     * Obtiene todas las cancelaciones de contratos
     */
    public List<CancelacionContratoDTO> obtenerTodasLasCancelaciones() {
        return cancelacionContratoRepository.findAll()
            .stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }

    /**
     * Obtiene una cancelación por ID
     */
    public Optional<CancelacionContratoDTO> obtenerCancelacionPorId(Long id) {
        return cancelacionContratoRepository.findById(id)
            .map(this::convertirADTO);
    }

    /**
     * Obtiene la cancelación de un contrato específico
     */
    public Optional<CancelacionContratoDTO> obtenerCancelacionPorContratoId(Long contratoId) {
        return cancelacionContratoRepository.findByContratoId(contratoId)
            .map(this::convertirADTO);
    }

    /**
     * Obtiene todas las cancelaciones por motivo
     */
    public List<CancelacionContratoDTO> obtenerCancelacionesPorMotivo(Integer motivoId) {
        return cancelacionContratoRepository.findByMotivoCancelacionId(motivoId)
            .stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
}
