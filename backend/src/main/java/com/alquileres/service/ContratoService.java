package com.alquileres.service;

import com.alquileres.dto.ContratoDTO;
import com.alquileres.dto.ContratoCreateDTO;
import com.alquileres.dto.EstadoContratoUpdateDTO;
import com.alquileres.model.Contrato;
import com.alquileres.model.Inmueble;
import com.alquileres.model.Inquilino;
import com.alquileres.model.EstadoContrato;
import com.alquileres.model.EstadoInmueble;
import com.alquileres.model.Propietario;
import com.alquileres.model.TipoInmueble;
import com.alquileres.repository.ContratoRepository;
import com.alquileres.repository.InmuebleRepository;
import com.alquileres.repository.InquilinoRepository;
import com.alquileres.repository.EstadoContratoRepository;
import com.alquileres.repository.EstadoInmuebleRepository;
import com.alquileres.repository.PropietarioRepository;
import com.alquileres.repository.TipoInmuebleRepository;
import com.alquileres.exception.BusinessException;
import com.alquileres.exception.ErrorCodes;
import com.alquileres.util.FechaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalDate;
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
    private EstadoInmuebleRepository estadoInmuebleRepository;

    @Autowired
    private PropietarioRepository propietarioRepository;

    @Autowired
    private TipoInmuebleRepository tipoInmuebleRepository;

    // Método helper para enriquecer ContratoDTO con información del propietario
    private ContratoDTO enrichContratoDTO(Contrato contrato) {
        ContratoDTO contratoDTO = new ContratoDTO(contrato);

        // Convertir fechas de formato ISO a formato de usuario para la respuesta
        if (contrato.getFechaInicio() != null) {
            contratoDTO.setFechaInicio(FechaUtil.convertirFechaISOToUsuario(contrato.getFechaInicio()));
        }
        if (contrato.getFechaFin() != null) {
            contratoDTO.setFechaFin(FechaUtil.convertirFechaISOToUsuario(contrato.getFechaFin()));
        }
        if (contrato.getFechaAumento() != null) {
            contratoDTO.setFechaAumento(FechaUtil.convertirFechaISOToUsuario(contrato.getFechaAumento()));
        }

        // Obtener información completa del propietario a través del inmueble
        if (contrato.getInmueble() != null && contrato.getInmueble().getPropietarioId() != null) {
            Optional<Propietario> propietario = propietarioRepository.findById(contrato.getInmueble().getPropietarioId());
            if (propietario.isPresent()) {
                Propietario prop = propietario.get();
                contratoDTO.setNombrePropietario(prop.getNombre());
                contratoDTO.setApellidoPropietario(prop.getApellido());
                contratoDTO.setDniPropietario(prop.getDni());
                contratoDTO.setTelefonoPropietario(prop.getTelefono());
                contratoDTO.setEmailPropietario(prop.getEmail());
                contratoDTO.setDireccionPropietario(prop.getDireccion());
            }
        }

        // Obtener información del tipo de inmueble
        if (contrato.getInmueble() != null && contrato.getInmueble().getTipoInmuebleId() != null) {
            Optional<TipoInmueble> tipoInmueble = tipoInmuebleRepository.findById(contrato.getInmueble().getTipoInmuebleId());
            if (tipoInmueble.isPresent()) {
                contratoDTO.setTipoInmueble(tipoInmueble.get().getNombre());
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

    // Obtener contratos no vigentes
    public List<ContratoDTO> obtenerContratosNoVigentes() {
        List<Contrato> contratos = contratoRepository.findContratosNoVigentes();
        return contratos.stream()
                .map(this::enrichContratoDTO)
                .collect(Collectors.toList());
    }

    // Contar contratos vigentes
    public Long contarContratosVigentes() {
        return contratoRepository.countContratosVigentes();
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
    public ContratoDTO crearContrato(ContratoCreateDTO contratoDTO) {
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

        // Validar o asignar estado de contrato
        EstadoContrato estadoContrato;
        if (contratoDTO.getEstadoContratoId() != null) {
            // Si se proporciona un estado, validar que existe
            Optional<EstadoContrato> estadoContratoOpt = estadoContratoRepository.findById(contratoDTO.getEstadoContratoId());
            if (!estadoContratoOpt.isPresent()) {
                throw new BusinessException(ErrorCodes.ESTADO_CONTRATO_NO_ENCONTRADO, "No existe el estado de contrato indicado", HttpStatus.BAD_REQUEST);
            }
            estadoContrato = estadoContratoOpt.get();
        } else {
            // Si no se proporciona estado, asignar "Vigente" por defecto
            Optional<EstadoContrato> estadoVigenteOpt = estadoContratoRepository.findByNombre("Vigente");
            if (!estadoVigenteOpt.isPresent()) {
                throw new BusinessException(ErrorCodes.ESTADO_CONTRATO_NO_ENCONTRADO, "No se pudo asignar el estado por defecto", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            estadoContrato = estadoVigenteOpt.get();
        }

        // Validar que el inmueble no tenga un contrato vigente
        if (contratoRepository.existsContratoVigenteByInmueble(inmueble.get())) {
            throw new BusinessException(ErrorCodes.INMUEBLE_YA_ALQUILADO, "El inmueble ya tiene un contrato vigente", HttpStatus.BAD_REQUEST);
        }

        // Validar y convertir fechas del formato del usuario (dd/MM/yyyy) al formato ISO (yyyy-MM-dd)
        String fechaInicioISO = null;
        String fechaFinISO = null;

        try {
            if (contratoDTO.getFechaInicio() != null) {
                if (!FechaUtil.esFechaValidaUsuario(contratoDTO.getFechaInicio())) {
                    throw new BusinessException(ErrorCodes.FORMATO_FECHA_INVALIDO,
                        "Formato de fecha de inicio inválido. Use dd/MM/yyyy (ej: 25/12/2024)", HttpStatus.BAD_REQUEST);
                }
                fechaInicioISO = FechaUtil.convertirFechaUsuarioToISO(contratoDTO.getFechaInicio());
            }

            if (contratoDTO.getFechaFin() != null) {
                if (!FechaUtil.esFechaValidaUsuario(contratoDTO.getFechaFin())) {
                    throw new BusinessException(ErrorCodes.FORMATO_FECHA_INVALIDO,
                        "Formato de fecha de fin inválido. Use dd/MM/yyyy (ej: 25/12/2024)", HttpStatus.BAD_REQUEST);
                }
                fechaFinISO = FechaUtil.convertirFechaUsuarioToISO(contratoDTO.getFechaFin());
            }
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCodes.FORMATO_FECHA_INVALIDO, e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        // Validar fechas lógicas usando las fechas convertidas
        if (fechaInicioISO != null && fechaFinISO != null) {
            if (FechaUtil.compararFechas(fechaFinISO, fechaInicioISO) < 0) {
                throw new BusinessException(ErrorCodes.RANGO_DE_FECHAS_INVALIDO, "La fecha de fin no puede ser anterior a la fecha de inicio", HttpStatus.BAD_REQUEST);
            }
        }

        // Calcular fecha de aumento usando las fechas en formato ISO
        String fechaAumentoCalculada = null;
        if (fechaInicioISO != null && contratoDTO.getPeriodoAumento() != null && contratoDTO.getPeriodoAumento() > 0) {
            try {
                fechaAumentoCalculada = FechaUtil.agregarMeses(fechaInicioISO, contratoDTO.getPeriodoAumento());
            } catch (IllegalArgumentException e) {
                throw new BusinessException(ErrorCodes.ERROR_CALCULO_FECHA, "Error calculando fecha de aumento: " + e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }

        // Crear el contrato con las fechas en formato ISO
        Contrato contrato = new Contrato();
        contrato.setInmueble(inmueble.get());
        contrato.setInquilino(inquilino.get());
        contrato.setFechaInicio(fechaInicioISO);
        contrato.setFechaFin(fechaFinISO);
        contrato.setMonto(contratoDTO.getMonto());
        contrato.setPorcentajeAumento(contratoDTO.getPorcentajeAumento());
        contrato.setEstadoContrato(estadoContrato);
        contrato.setAumentaConIcl(contratoDTO.getAumentaConIcl() != null ? contratoDTO.getAumentaConIcl() : false);
        contrato.setPdfPath(contratoDTO.getPdfPath());
        contrato.setPeriodoAumento(contratoDTO.getPeriodoAumento());
        contrato.setFechaAumento(fechaAumentoCalculada);

        // Guardar el contrato
        Contrato contratoGuardado = contratoRepository.save(contrato);

        // Actualizar el estado del inmueble a "Alquilado"
        Optional<EstadoInmueble> estadoAlquilado = estadoInmuebleRepository.findByNombre("Alquilado");
        if (estadoAlquilado.isPresent()) {
            Inmueble inmuebleToUpdate = inmueble.get();
            inmuebleToUpdate.setEstado(estadoAlquilado.get().getId());
            inmuebleToUpdate.setEsAlquilado(true);
            inmuebleRepository.save(inmuebleToUpdate);
        }

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
        if (contratoDTO.getPeriodoAumento() != null) {
            contrato.setPeriodoAumento(contratoDTO.getPeriodoAumento());
        }
        if (contratoDTO.getFechaAumento() != null) {
            contrato.setFechaAumento(contratoDTO.getFechaAumento());
        }

        // Validar y convertir fechas del formato del usuario (dd/MM/yyyy) al formato ISO (yyyy-MM-dd)
        String fechaInicioISO = null;
        String fechaFinISO = null;

        try {
            if (contratoDTO.getFechaInicio() != null) {
                if (!FechaUtil.esFechaValidaUsuario(contratoDTO.getFechaInicio())) {
                    throw new BusinessException(ErrorCodes.FORMATO_FECHA_INVALIDO,
                        "Formato de fecha de inicio inválido. Use dd/MM/yyyy (ej: 25/12/2024)", HttpStatus.BAD_REQUEST);
                }
                fechaInicioISO = FechaUtil.convertirFechaUsuarioToISO(contratoDTO.getFechaInicio());
            }

            if (contratoDTO.getFechaFin() != null) {
                if (!FechaUtil.esFechaValidaUsuario(contratoDTO.getFechaFin())) {
                    throw new BusinessException(ErrorCodes.FORMATO_FECHA_INVALIDO,
                        "Formato de fecha de fin inválido. Use dd/MM/yyyy (ej: 25/12/2024)", HttpStatus.BAD_REQUEST);
                }
                fechaFinISO = FechaUtil.convertirFechaUsuarioToISO(contratoDTO.getFechaFin());
            }
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCodes.FORMATO_FECHA_INVALIDO, e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        // Validar fechas lógicas usando las fechas convertidas
        if (fechaInicioISO != null && fechaFinISO != null) {
            if (FechaUtil.compararFechas(fechaFinISO, fechaInicioISO) < 0) {
                throw new BusinessException(ErrorCodes.RANGO_DE_FECHAS_INVALIDO, "La fecha de fin no puede ser anterior a la fecha de inicio", HttpStatus.BAD_REQUEST);
            }
        }

        // Calcular fecha de aumento usando las fechas en formato ISO
        String fechaAumentoCalculada = null;
        if (fechaInicioISO != null && contratoDTO.getPeriodoAumento() != null && contratoDTO.getPeriodoAumento() > 0) {
            try {
                fechaAumentoCalculada = FechaUtil.agregarMeses(fechaInicioISO, contratoDTO.getPeriodoAumento());
            } catch (IllegalArgumentException e) {
                throw new BusinessException(ErrorCodes.ERROR_CALCULO_FECHA, "Error calculando fecha de aumento: " + e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }

        // Actualizar el contrato con las fechas en formato ISO
        contrato.setFechaInicio(fechaInicioISO);
        contrato.setFechaFin(fechaFinISO);
        contrato.setFechaAumento(fechaAumentoCalculada);

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

        Contrato contrato = contratoExistente.get();
        String nombreEstadoContrato = estadoContrato.get().getNombre();

        // Validar que el inmueble esté disponible si se quiere cambiar a un estado que no sea terminar
        if ("Vigente".equals(nombreEstadoContrato)) {
            // Si se quiere poner el contrato como "Vigente", validar que el inmueble esté disponible
            Inmueble inmueble = contrato.getInmueble();
            Optional<EstadoInmueble> estadoInmuebleActual = estadoInmuebleRepository.findById(inmueble.getEstado());

            if (estadoInmuebleActual.isPresent() && !"Disponible".equals(estadoInmuebleActual.get().getNombre())) {
                throw new BusinessException(ErrorCodes.INMUEBLE_NO_DISPONIBLE,
                    "No se puede activar el contrato porque el inmueble no está disponible. Estado actual: " + estadoInmuebleActual.get().getNombre(),
                    HttpStatus.BAD_REQUEST);
            }
        }

        // Actualizar el estado del contrato
        contrato.setEstadoContrato(estadoContrato.get());

        // Verificar si el nuevo estado requiere actualizar el inmueble
        if ("No Vigente".equals(nombreEstadoContrato) || "Cancelado".equals(nombreEstadoContrato)) {
            // Actualizar el estado del inmueble a "Disponible"
            Optional<EstadoInmueble> estadoDisponible = estadoInmuebleRepository.findByNombre("Disponible");
            if (estadoDisponible.isPresent()) {
                Inmueble inmuebleToUpdate = contrato.getInmueble();
                inmuebleToUpdate.setEstado(estadoDisponible.get().getId());
                inmuebleToUpdate.setEsAlquilado(false);
                inmuebleRepository.save(inmuebleToUpdate);
            }
        } else if ("Vigente".equals(nombreEstadoContrato)) {
            // Actualizar el estado del inmueble a "Alquilado"
            Optional<EstadoInmueble> estadoAlquilado = estadoInmuebleRepository.findByNombre("Alquilado");
            if (estadoAlquilado.isPresent()) {
                Inmueble inmuebleToUpdate = contrato.getInmueble();
                inmuebleToUpdate.setEstado(estadoAlquilado.get().getId());
                inmuebleToUpdate.setEsAlquilado(true);
                inmuebleRepository.save(inmuebleToUpdate);
            }
        }

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
