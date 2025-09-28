package com.alquileres.service;

import com.alquileres.dto.ContratoDTO;
import com.alquileres.dto.EstadoContratoUpdateDTO;
import com.alquileres.model.Contrato;
import com.alquileres.model.Inmueble;
import com.alquileres.model.Inquilino;
import com.alquileres.model.EstadoContrato;
import com.alquileres.model.Propietario;
import com.alquileres.repository.ContratoRepository;
import com.alquileres.repository.InmuebleRepository;
import com.alquileres.repository.InquilinoRepository;
import com.alquileres.repository.EstadoContratoRepository;
import com.alquileres.repository.PropietarioRepository;
import com.alquileres.exception.BusinessException;
import com.alquileres.exception.ErrorCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ContratoService {

    @Autowired
    private ContratoRepository contratoRepository;

    @Autowired
    private InmuebleRepository inmuebleRepository;

    @Autowired
    private InquilinoRepository inquilinoRepository;

    @Autowired
    private EstadoContratoRepository estadoContratoRepository;

    @Autowired
    private PropietarioRepository propietarioRepository;

    // Método helper para enriquecer ContratoDTO con información del propietario
    private ContratoDTO enrichContratoDTO(Contrato contrato) {
        ContratoDTO contratoDTO = new ContratoDTO(contrato);

        // Obtener información del propietario a través del inmueble
        if (contrato.getInmueble() != null && contrato.getInmueble().getPropietarioId() != null) {
            Optional<Propietario> propietario = propietarioRepository.findById(contrato.getInmueble().getPropietarioId());
            if (propietario.isPresent()) {
                contratoDTO.setNombrePropietario(propietario.get().getNombre());
                contratoDTO.setApellidoPropietario(propietario.get().getApellido());
            }
        }

        return contratoDTO;
    }

    // Obtener todos los contratos
    public List<ContratoDTO> obtenerTodosLosContratos() {
        List<Contrato> contratos = contratoRepository.findAll();
        return contratos.stream()
                .map(this::enrichContratoDTO)
                .collect(Collectors.toList());
    }

    // Obtener contrato por ID
    public ContratoDTO obtenerContratoPorId(Long id) {
        Optional<Contrato> contrato = contratoRepository.findById(id);
        if (contrato.isPresent()) {
            return enrichContratoDTO(contrato.get());
        } else {
            throw new BusinessException(ErrorCodes.CONTRATO_NO_ENCONTRADO, "Contrato no encontrado con ID: " + id, HttpStatus.NOT_FOUND);
        }
    }

    // Obtener contratos por inmueble
    public List<ContratoDTO> obtenerContratosPorInmueble(Long inmuebleId) {
        Optional<Inmueble> inmueble = inmuebleRepository.findById(inmuebleId);
        if (!inmueble.isPresent()) {
            throw new BusinessException(ErrorCodes.INMUEBLE_NO_ENCONTRADO, "Inmueble no encontrado con ID: " + inmuebleId, HttpStatus.NOT_FOUND);
        }

        List<Contrato> contratos = contratoRepository.findByInmueble(inmueble.get());
        return contratos.stream()
                .map(this::enrichContratoDTO)
                .collect(Collectors.toList());
    }

    // Obtener contratos por inquilino
    public List<ContratoDTO> obtenerContratosPorInquilino(Long inquilinoId) {
        Optional<Inquilino> inquilino = inquilinoRepository.findById(inquilinoId);
        if (!inquilino.isPresent()) {
            throw new BusinessException(ErrorCodes.INQUILINO_NO_ENCONTRADO, "Inquilino no encontrado con ID: " + inquilinoId, HttpStatus.NOT_FOUND);
        }

        List<Contrato> contratos = contratoRepository.findByInquilino(inquilino.get());
        return contratos.stream()
                .map(this::enrichContratoDTO)
                .collect(Collectors.toList());
    }

    // Obtener contratos vigentes
    public List<ContratoDTO> obtenerContratosVigentes() {
        List<Contrato> contratos = contratoRepository.findContratosVigentes();
        return contratos.stream()
                .map(this::enrichContratoDTO)
                .collect(Collectors.toList());
    }

    // Obtener contratos que vencen próximamente
    public List<ContratoDTO> obtenerContratosProximosAVencer(int diasAntes) {
        // Calcular fecha límite como string
        String fechaLimite = LocalDateTime.now().plusDays(diasAntes).format(DateTimeFormatter.ISO_LOCAL_DATE);
        List<Contrato> contratos = contratoRepository.findByFechaFinBefore(fechaLimite);
        return contratos.stream()
                .map(this::enrichContratoDTO)
                .collect(Collectors.toList());
    }

    // Crear nuevo contrato
    public ContratoDTO crearContrato(ContratoDTO contratoDTO) {
        // Validar que existe el inmueble
        Optional<Inmueble> inmueble = inmuebleRepository.findById(contratoDTO.getInmuebleId());
        if (!inmueble.isPresent()) {
            throw new BusinessException(ErrorCodes.INMUEBLE_NO_ENCONTRADO, "No existe el inmueble indicado", HttpStatus.BAD_REQUEST);
        }

        // Validar que existe el inquilino
        Optional<Inquilino> inquilino = inquilinoRepository.findById(contratoDTO.getInquilinoId());
        if (!inquilino.isPresent()) {
            throw new BusinessException(ErrorCodes.INQUILINO_NO_ENCONTRADO, "No existe el inquilino indicado", HttpStatus.BAD_REQUEST);
        }

        // Validar que existe el estado de contrato
        Optional<EstadoContrato> estadoContrato = estadoContratoRepository.findById(contratoDTO.getEstadoContratoId());
        if (!estadoContrato.isPresent()) {
            throw new BusinessException(ErrorCodes.ESTADO_CONTRATO_NO_ENCONTRADO, "No existe el estado de contrato indicado", HttpStatus.BAD_REQUEST);
        }

        // Validar que el inmueble no tenga un contrato vigente
        if (contratoRepository.existsContratoVigenteByInmueble(inmueble.get())) {
            throw new BusinessException(ErrorCodes.INMUEBLE_YA_ALQUILADO, "El inmueble ya tiene un contrato vigente", HttpStatus.BAD_REQUEST);
        }

        // Validar fechas lógicas (si ambas están presentes)
        if (contratoDTO.getFechaInicio() != null && contratoDTO.getFechaFin() != null) {
            if (contratoDTO.getFechaFin().compareTo(contratoDTO.getFechaInicio()) < 0) {
                throw new BusinessException(ErrorCodes.RANGO_DE_FECHAS_INVALIDO, "La fecha de fin no puede ser anterior a la fecha de inicio", HttpStatus.BAD_REQUEST);
            }
        }

        // Crear el contrato
        Contrato contrato = new Contrato();
        contrato.setInmueble(inmueble.get());
        contrato.setInquilino(inquilino.get());
        contrato.setFechaInicio(contratoDTO.getFechaInicio());
        contrato.setFechaFin(contratoDTO.getFechaFin());
        contrato.setMonto(contratoDTO.getMonto());
        contrato.setPorcentajeAumento(contratoDTO.getPorcentajeAumento());
        contrato.setEstadoContrato(estadoContrato.get());
        contrato.setAumentaConIcl(contratoDTO.getAumentaConIcl() != null ? contratoDTO.getAumentaConIcl() : false);
        contrato.setPdfPath(contratoDTO.getPdfPath());

        Contrato contratoGuardado = contratoRepository.save(contrato);
        return enrichContratoDTO(contratoGuardado);
    }

    // Actualizar contrato
    public ContratoDTO actualizarContrato(Long id, ContratoDTO contratoDTO) {
        Optional<Contrato> contratoExistente = contratoRepository.findById(id);

        if (!contratoExistente.isPresent()) {
            throw new BusinessException(ErrorCodes.CONTRATO_NO_ENCONTRADO, "Contrato no encontrado con ID: " + id, HttpStatus.NOT_FOUND);
        }

        Contrato contrato = contratoExistente.get();

        // Validar y actualizar inmueble si se proporciona
        if (contratoDTO.getInmuebleId() != null) {
            Optional<Inmueble> inmueble = inmuebleRepository.findById(contratoDTO.getInmuebleId());
            if (!inmueble.isPresent()) {
                throw new BusinessException(ErrorCodes.INMUEBLE_NO_ENCONTRADO, "No existe el inmueble indicado", HttpStatus.BAD_REQUEST);
            }
            contrato.setInmueble(inmueble.get());
        }

        // Validar y actualizar inquilino si se proporciona
        if (contratoDTO.getInquilinoId() != null) {
            Optional<Inquilino> inquilino = inquilinoRepository.findById(contratoDTO.getInquilinoId());
            if (!inquilino.isPresent()) {
                throw new BusinessException(ErrorCodes.INQUILINO_NO_ENCONTRADO, "No existe el inquilino indicado", HttpStatus.BAD_REQUEST);
            }
            contrato.setInquilino(inquilino.get());
        }

        // Validar y actualizar estado de contrato si se proporciona
        if (contratoDTO.getEstadoContratoId() != null) {
            Optional<EstadoContrato> estadoContrato = estadoContratoRepository.findById(contratoDTO.getEstadoContratoId());
            if (!estadoContrato.isPresent()) {
                throw new BusinessException(ErrorCodes.ESTADO_CONTRATO_NO_ENCONTRADO, "No existe el estado de contrato indicado", HttpStatus.BAD_REQUEST);
            }
            contrato.setEstadoContrato(estadoContrato.get());
        }

        // Actualizar otros campos
        if (contratoDTO.getFechaInicio() != null) {
            contrato.setFechaInicio(contratoDTO.getFechaInicio());
        }
        if (contratoDTO.getFechaFin() != null) {
            contrato.setFechaFin(contratoDTO.getFechaFin());
        }
        if (contratoDTO.getMonto() != null) {
            contrato.setMonto(contratoDTO.getMonto());
        }
        if (contratoDTO.getPorcentajeAumento() != null) {
            contrato.setPorcentajeAumento(contratoDTO.getPorcentajeAumento());
        }
        if (contratoDTO.getAumentaConIcl() != null) {
            contrato.setAumentaConIcl(contratoDTO.getAumentaConIcl());
        }
        if (contratoDTO.getPdfPath() != null) {
            contrato.setPdfPath(contratoDTO.getPdfPath());
        }

        // Validar fechas lógicas (si ambas están presentes)
        if (contrato.getFechaInicio() != null && contrato.getFechaFin() != null) {
            if (contrato.getFechaFin().compareTo(contrato.getFechaInicio()) < 0) {
                throw new BusinessException(ErrorCodes.RANGO_DE_FECHAS_INVALIDO, "La fecha de fin no puede ser anterior a la fecha de inicio", HttpStatus.BAD_REQUEST);
            }
        }

        Contrato contratoActualizado = contratoRepository.save(contrato);
        return enrichContratoDTO(contratoActualizado);
    }

    // Cambiar estado del contrato
    public ContratoDTO terminarContrato(Long id, EstadoContratoUpdateDTO estadoContratoUpdateDTO) {
        // Verificar que existe el contrato
        Optional<Contrato> contratoExistente = contratoRepository.findById(id);
        if (!contratoExistente.isPresent()) {
            throw new BusinessException(ErrorCodes.CONTRATO_NO_ENCONTRADO, "Contrato no encontrado con ID: " + id, HttpStatus.NOT_FOUND);
        }

        // Verificar que existe el estado de contrato
        Optional<EstadoContrato> estadoContrato = estadoContratoRepository.findById(estadoContratoUpdateDTO.getEstadoContratoId());
        if (!estadoContrato.isPresent()) {
            throw new BusinessException(ErrorCodes.ESTADO_CONTRATO_NO_ENCONTRADO, "No existe el estado de contrato indicado", HttpStatus.BAD_REQUEST);
        }

        // Actualizar solo el estado del contrato
        Contrato contrato = contratoExistente.get();
        contrato.setEstadoContrato(estadoContrato.get());
        Contrato contratoActualizado = contratoRepository.save(contrato);
        return enrichContratoDTO(contratoActualizado);
    }

    // Eliminar contrato
    public void eliminarContrato(Long id) {
        Optional<Contrato> contrato = contratoRepository.findById(id);
        if (!contrato.isPresent()) {
            throw new BusinessException(ErrorCodes.CONTRATO_NO_ENCONTRADO, "Contrato no encontrado con ID: " + id, HttpStatus.NOT_FOUND);
        }

        contratoRepository.deleteById(id);
    }

    // Verificar si existe un contrato
    public boolean existeContrato(Long id) {
        return contratoRepository.existsById(id);
    }
}
