package com.alquileres.service;

import com.alquileres.dto.CancelacionContratoDTO;
import com.alquileres.dto.EstadoContratoUpdateDTO;
import com.alquileres.exception.BusinessException;
import com.alquileres.exception.ErrorCodes;
import com.alquileres.model.CancelacionContrato;
import com.alquileres.model.Contrato;
import com.alquileres.model.MotivoCancelacion;
import com.alquileres.repository.CancelacionContratoRepository;
import com.alquileres.repository.MotivoCancelacionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CancelacionContratoService {

    private final CancelacionContratoRepository cancelacionContratoRepository;
    private final MotivoCancelacionRepository motivoCancelacionRepository;
    private final ClockService clockService;

    public CancelacionContratoService(
            CancelacionContratoRepository cancelacionContratoRepository,
            MotivoCancelacionRepository motivoCancelacionRepository,
            ClockService clockService) {
        this.cancelacionContratoRepository = cancelacionContratoRepository;
        this.motivoCancelacionRepository = motivoCancelacionRepository;
        this.clockService = clockService;
    }

    private CancelacionContratoDTO convertirADTO(CancelacionContrato cancelacion) {
        return new CancelacionContratoDTO(
            cancelacion.getId(),
            cancelacion.getContrato().getId(),
            cancelacion.getFechaCancelacion(),
            cancelacion.getMotivoCancelacion().getNombre(),
            cancelacion.getObservaciones()
        );
    }

    @Transactional(readOnly = true)
    public List<CancelacionContratoDTO> obtenerTodasLasCancelaciones() {
        return cancelacionContratoRepository.findAllWithRelations()
            .stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<CancelacionContratoDTO> obtenerCancelacionPorId(Long id) {
        return cancelacionContratoRepository.findByIdWithRelations(id)
            .map(this::convertirADTO);
    }

    @Transactional(readOnly = true)
    public Optional<CancelacionContratoDTO> obtenerCancelacionPorContratoId(Long contratoId) {
        return cancelacionContratoRepository.findByContratoIdWithRelations(contratoId)
            .map(this::convertirADTO);
    }

    @Transactional(readOnly = true)
    public List<CancelacionContratoDTO> obtenerCancelacionesPorMotivo(Integer motivoId) {
        return cancelacionContratoRepository.findByMotivoCancelacionIdWithRelations(motivoId)
            .stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }

    /**
     * Crea el registro de cancelación de un contrato si aún no existe.
     * Llamado cuando un contrato vigente pasa a estado Cancelado.
     */
    @Transactional
    public void crearCancelacionSiNoExiste(Contrato contrato, EstadoContratoUpdateDTO dto) {
        if (cancelacionContratoRepository.existsByContratoId(contrato.getId())) {
            return;
        }

        MotivoCancelacion motivo = resolverMotivo(dto.getMotivoCancelacionId());

        CancelacionContrato cancelacion = new CancelacionContrato();
        cancelacion.setContrato(contrato);
        cancelacion.setFechaCancelacion(
            clockService.getCurrentDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
        cancelacion.setMotivoCancelacion(motivo);

        if (dto.getObservaciones() != null && !dto.getObservaciones().isBlank()) {
            cancelacion.setObservaciones(dto.getObservaciones());
        }

        cancelacionContratoRepository.save(cancelacion);
    }

    private MotivoCancelacion resolverMotivo(Integer motivoCancelacionId) {
        if (motivoCancelacionId != null) {
            return motivoCancelacionRepository.findById(motivoCancelacionId)
                .orElseThrow(() -> new BusinessException(
                    ErrorCodes.MOTIVO_CANCELACION_NO_ENCONTRADO,
                    "No existe el motivo de cancelación indicado",
                    HttpStatus.BAD_REQUEST
                ));
        }
        return motivoCancelacionRepository.findByNombre("Otro")
            .orElseThrow(() -> new BusinessException(
                ErrorCodes.MOTIVO_CANCELACION_NO_ENCONTRADO,
                "No se encontró el motivo de cancelación por defecto",
                HttpStatus.INTERNAL_SERVER_ERROR
            ));
    }
}
