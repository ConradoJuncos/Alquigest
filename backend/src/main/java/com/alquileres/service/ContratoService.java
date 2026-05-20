package com.alquileres.service;

import com.alquileres.config.CacheNames;
import com.alquileres.dto.ContratoCreateDTO;
import com.alquileres.dto.ContratoDTO;
import com.alquileres.dto.EstadoContratoUpdateDTO;
import com.alquileres.exception.BusinessException;
import com.alquileres.exception.ErrorCodes;
import com.alquileres.model.*;
import com.alquileres.repository.ContratoRepository;
import com.alquileres.repository.EstadoContratoRepository;
import com.alquileres.util.FechaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = {CacheNames.CONTRATOS, CacheNames.CONTRATOS_VIGENTES,
                            CacheNames.CONTRATOS_NO_VIGENTES, CacheNames.CONTRATOS_PROXIMOS_VENCER,
                            CacheNames.CONTRATOS_POR_INMUEBLE, CacheNames.CONTRATOS_POR_INQUILINO})
public class ContratoService {

    private static final Logger logger = LoggerFactory.getLogger(ContratoService.class);

    private final ContratoRepository contratoRepository;
    private final EstadoContratoRepository estadoContratoRepository;
    private final InmuebleService inmuebleService;
    private final InquilinoService inquilinoService;
    private final PropietarioService propietarioService;
    private final AlquilerService alquilerService;
    private final CancelacionContratoService cancelacionContratoService;
    private final ServicioContratoService servicioContratoService;
    private final PDFService pdfService;
    private final ClockService clockService;

    public ContratoService(
            ContratoRepository contratoRepository,
            EstadoContratoRepository estadoContratoRepository,
            InmuebleService inmuebleService,
            InquilinoService inquilinoService,
            PropietarioService propietarioService,
            AlquilerService alquilerService,
            CancelacionContratoService cancelacionContratoService,
            ServicioContratoService servicioContratoService,
            PDFService pdfService,
            ClockService clockService) {
        this.contratoRepository = contratoRepository;
        this.estadoContratoRepository = estadoContratoRepository;
        this.inmuebleService = inmuebleService;
        this.inquilinoService = inquilinoService;
        this.propietarioService = propietarioService;
        this.alquilerService = alquilerService;
        this.cancelacionContratoService = cancelacionContratoService;
        this.servicioContratoService = servicioContratoService;
        this.pdfService = pdfService;
        this.clockService = clockService;
    }

    // -------------------------------------------------------------------------
    // Enriquecimiento de DTO
    // -------------------------------------------------------------------------

    private ContratoDTO enrichContratoDTO(Contrato contrato) {
        ContratoDTO dto = new ContratoDTO(contrato);
        convertirFechasParaRespuesta(contrato, dto);
        agregarInformacionPropietario(contrato, dto);
        agregarTipoInmueble(contrato, dto);
        agregarMontoUltimoAlquiler(contrato, dto);
        return dto;
    }

    private void convertirFechasParaRespuesta(Contrato contrato, ContratoDTO dto) {
        if (contrato.getFechaInicio() != null) dto.setFechaInicio(FechaUtil.convertirFechaISOToUsuario(contrato.getFechaInicio()));
        if (contrato.getFechaFin() != null) dto.setFechaFin(FechaUtil.convertirFechaISOToUsuario(contrato.getFechaFin()));
        if (contrato.getFechaAumento() != null) dto.setFechaAumento(FechaUtil.convertirFechaISOToUsuario(contrato.getFechaAumento()));
    }

    private void agregarInformacionPropietario(Contrato contrato, ContratoDTO dto) {
        if (contrato.getInmueble() == null || contrato.getInmueble().getPropietarioId() == null) return;
        Long propietarioId = contrato.getInmueble().getPropietarioId();
        try {
            var propietario = propietarioService.obtenerPropietarioPorId(propietarioId);
            dto.setNombrePropietario(propietario.getNombre());
            dto.setApellidoPropietario(propietario.getApellido());
            dto.setDniPropietario(propietario.getCuil());
            dto.setTelefonoPropietario(propietario.getTelefono());
            dto.setEmailPropietario(propietario.getEmail());
            dto.setDireccionPropietario(propietario.getDireccion());
            dto.setClaveFiscalPropietario(propietarioService.obtenerClaveFiscalONull(propietarioId));
        } catch (Exception e) {
            logger.error("Error al obtener datos del propietario ID: {}", propietarioId, e);
        }
    }

    private void agregarTipoInmueble(Contrato contrato, ContratoDTO dto) {
        if (contrato.getInmueble() == null) return;
        dto.setTipoInmueble(inmuebleService.obtenerNombreTipoInmueble(contrato.getInmueble().getTipoInmuebleId()));
    }

    private void agregarMontoUltimoAlquiler(Contrato contrato, ContratoDTO dto) {
        dto.setMontoUltimoAlquiler(alquilerService.obtenerMontoUltimoAlquiler(contrato.getId()));
    }

    // -------------------------------------------------------------------------
    // Queries / lectura
    // -------------------------------------------------------------------------

    @Cacheable(CacheNames.CONTRATOS)
    public List<ContratoDTO> obtenerTodosLosContratos() {
        return contratoRepository.findAll().stream()
                .map(this::enrichContratoDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = CacheNames.CONTRATO_POR_ID, key = "#id")
    public ContratoDTO obtenerContratoPorId(Long id) {
        return enrichContratoDTO(obtenerContratoEntidadPorId(id));
    }

    @Cacheable(value = CacheNames.CONTRATOS_POR_INMUEBLE, key = "#inmuebleId")
    public List<ContratoDTO> obtenerContratosPorInmueble(Long inmuebleId) {
        Inmueble inmueble = inmuebleService.obtenerEntidadPorId(inmuebleId);
        return contratoRepository.findByInmueble(inmueble).stream()
                .map(this::enrichContratoDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = CacheNames.CONTRATOS_POR_INQUILINO, key = "#inquilinoId")
    public List<ContratoDTO> obtenerContratosPorInquilino(Long inquilinoId) {
        Inquilino inquilino = inquilinoService.obtenerEntidadPorId(inquilinoId);
        return contratoRepository.findByInquilino(inquilino).stream()
                .map(this::enrichContratoDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(CacheNames.CONTRATOS_VIGENTES)
    public List<ContratoDTO> obtenerContratosVigentes() {
        return contratoRepository.findContratosVigentes().stream()
                .map(this::enrichContratoDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(CacheNames.CONTRATOS_NO_VIGENTES)
    public List<ContratoDTO> obtenerContratosNoVigentes() {
        return contratoRepository.findContratosNoVigentes().stream()
                .map(this::enrichContratoDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(CacheNames.CONTRATOS_VIGENTES_COUNT)
    public Long contarContratosVigentes() {
        return contratoRepository.countContratosVigentes();
    }

    @Cacheable(value = CacheNames.CONTRATOS_PROXIMOS_VENCER, key = "#diasAntes")
    public List<ContratoDTO> obtenerContratosProximosAVencer(int diasAntes) {
        String fechaActual = clockService.getCurrentDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
        String fechaLimite = clockService.getCurrentDate().plusDays(diasAntes).format(DateTimeFormatter.ISO_LOCAL_DATE);
        return contratoRepository.findContratosVigentesProximosAVencer(fechaActual, fechaLimite).stream()
                .map(this::enrichContratoDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = CacheNames.CONTRATOS_PROXIMOS_VENCER_COUNT, key = "#diasAntes")
    public Long contarContratosProximosAVencer(int diasAntes) {
        String fechaActual = clockService.getCurrentDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
        String fechaLimite = clockService.getCurrentDate().plusDays(diasAntes).format(DateTimeFormatter.ISO_LOCAL_DATE);
        return contratoRepository.countContratosVigentesProximosAVencer(fechaActual, fechaLimite);
    }

    @Cacheable(value = CacheNames.CONTRATO_EXISTE, key = "#id")
    public boolean existeContrato(Long id) {
        return contratoRepository.existsById(id);
    }

    @Cacheable(value = CacheNames.INMUEBLE_TIENE_CONTRATO_VIGENTE, key = "#inmuebleId")
    public boolean inmuebleTieneContratoVigente(Long inmuebleId) {
        return contratoRepository.existsContratoVigenteByInmuebleId(inmuebleId);
    }

    public Contrato obtenerContratoEntidadPorId(Long id) {
        return contratoRepository.findById(id)
            .orElseThrow(() -> new BusinessException(
                ErrorCodes.CONTRATO_NO_ENCONTRADO,
                "Contrato no encontrado con ID: " + id,
                HttpStatus.NOT_FOUND
            ));
    }

    // -------------------------------------------------------------------------
    // Creación
    // -------------------------------------------------------------------------

    @Transactional
    @CacheEvict(allEntries = true, cacheNames = {
        CacheNames.CONTRATOS, CacheNames.CONTRATOS_VIGENTES, CacheNames.CONTRATOS_VIGENTES_COUNT,
        CacheNames.CONTRATOS_PROXIMOS_VENCER, CacheNames.CONTRATOS_PROXIMOS_VENCER_COUNT,
        CacheNames.CONTRATOS_POR_INMUEBLE, CacheNames.CONTRATOS_POR_INQUILINO,
        CacheNames.INMUEBLE_TIENE_CONTRATO_VIGENTE
    })
    public ContratoDTO crearContrato(ContratoCreateDTO contratoDTO) {
        Inmueble inmueble = inmuebleService.obtenerEntidadPorId(contratoDTO.getInmuebleId());
        Inquilino inquilino = inquilinoService.obtenerEntidadPorId(contratoDTO.getInquilinoId());

        inmuebleService.validarDisponibilidadParaContrato(inmueble);

        EstadoContrato estadoContrato = resolverEstadoContrato(contratoDTO.getEstadoContratoId());
        FechasContrato fechas = procesarYValidarFechas(contratoDTO);

        Contrato contratoGuardado = crearYGuardarContrato(contratoDTO, inmueble, inquilino, estadoContrato, fechas);

        if ("Vigente".equals(estadoContrato.getNombre())) {
            inmuebleService.asignarEstadoAlquilado(inmueble);
            inquilinoService.marcarComoAlquilando(inquilino);

            LocalDate fechaInicio = LocalDate.parse(fechas.fechaInicio, DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate hoy = clockService.getCurrentDate();

            if (fechaInicio.isBefore(hoy)) {
                logger.info("Contrato ID {} tiene fecha de inicio en el pasado ({}). Generando alquileres retroactivos.", contratoGuardado.getId(), fechas.fechaInicio);
                String nuevaFechaAumento = alquilerService.crearAlquileresRetroactivos(contratoGuardado, fechaInicio, hoy);
                if (nuevaFechaAumento != null) {
                    contratoGuardado.setFechaAumento(nuevaFechaAumento);
                    contratoRepository.save(contratoGuardado);
                }
            } else {
                alquilerService.generarPrimerAlquiler(contratoGuardado);
            }
        }

        return enrichContratoDTO(contratoGuardado);
    }

    // -------------------------------------------------------------------------
    // Cambio de estado
    // -------------------------------------------------------------------------

    @Transactional
    @CacheEvict(allEntries = true, cacheNames = {
        CacheNames.CONTRATOS, CacheNames.CONTRATO_POR_ID,
        CacheNames.CONTRATOS_VIGENTES, CacheNames.CONTRATOS_VIGENTES_COUNT,
        CacheNames.CONTRATOS_NO_VIGENTES, CacheNames.CONTRATOS_PROXIMOS_VENCER,
        CacheNames.CONTRATOS_PROXIMOS_VENCER_COUNT, CacheNames.CONTRATOS_POR_INMUEBLE,
        CacheNames.CONTRATOS_POR_INQUILINO, CacheNames.INMUEBLE_TIENE_CONTRATO_VIGENTE
    })
    public ContratoDTO terminarContrato(Long id, EstadoContratoUpdateDTO estadoContratoUpdateDTO) {
        Contrato contrato = obtenerContratoEntidadPorId(id);

        EstadoContrato nuevoEstado = estadoContratoRepository.findById(estadoContratoUpdateDTO.getEstadoContratoId())
            .orElseThrow(() -> new BusinessException(
                ErrorCodes.ESTADO_CONTRATO_NO_ENCONTRADO, "No existe el estado de contrato indicado", HttpStatus.BAD_REQUEST));

        String estadoAnterior = contrato.getEstadoContrato().getNombre();
        String nombreNuevoEstado = nuevoEstado.getNombre();

        validarCambioEstadoContrato(contrato, nombreNuevoEstado);

        contrato.setEstadoContrato(nuevoEstado);
        aplicarCambiosPorNuevoEstado(contrato, estadoAnterior, nombreNuevoEstado, estadoContratoUpdateDTO);

        return enrichContratoDTO(contratoRepository.save(contrato));
    }

    private void validarCambioEstadoContrato(Contrato contrato, String nombreNuevoEstado) {
        if (!"Vigente".equals(nombreNuevoEstado)) return;
        // Si se reactiva, el inmueble debe estar disponible
        inmuebleService.validarDisponibilidadParaContrato(contrato.getInmueble());
    }

    private void aplicarCambiosPorNuevoEstado(Contrato contrato, String estadoAnterior,
                                               String nombreNuevoEstado, EstadoContratoUpdateDTO dto) {
        if ("No Vigente".equals(nombreNuevoEstado) || "Cancelado".equals(nombreNuevoEstado)) {
            finalizarContrato(contrato);
            if ("Vigente".equals(estadoAnterior) && "Cancelado".equals(nombreNuevoEstado)) {
                cancelacionContratoService.crearCancelacionSiNoExiste(contrato, dto);
            }
        } else if ("Vigente".equals(nombreNuevoEstado)) {
            inmuebleService.asignarEstadoAlquilado(contrato.getInmueble());
            inquilinoService.marcarComoAlquilando(contrato.getInquilino());
        }
    }

    public void finalizarContrato(Contrato contrato) {
        inmuebleService.asignarEstadoDisponible(contrato.getInmueble());
        inquilinoService.marcarComoNoAlquilando(contrato.getInquilino());
        alquilerService.anularAlquileresDeContrato(contrato.getId());
        desactivarServiciosDelContrato(contrato.getId());
    }

    private void desactivarServiciosDelContrato(Long contratoId) {
        try {
            List<ServicioContrato> servicios = servicioContratoService.getServiciosActivosByContrato(contratoId);
            if (servicios != null) {
                servicios.forEach(s -> servicioContratoService.desactivarServicio(s.getId()));
                if (!servicios.isEmpty()) {
                    logger.info("Se desactivaron {} servicios del contrato ID: {}", servicios.size(), contratoId);
                }
            }
        } catch (Exception e) {
            logger.error("Error al desactivar servicios del contrato ID: {}", contratoId, e);
            throw new BusinessException(ErrorCodes.ERROR_INTERNO, "Error al desactivar los servicios del contrato", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // -------------------------------------------------------------------------
    // PDF
    // -------------------------------------------------------------------------

    @CacheEvict(value = CacheNames.CONTRATO_POR_ID, key = "#id")
    public ContratoDTO guardarPdf(Long id, byte[] pdfBytes, String nombreArchivo) throws Exception {
        Contrato contrato = obtenerContratoEntidadPorId(id);
        PDF pdfGuardado = pdfService.guardarPDF("CONTRATO", pdfBytes, nombreArchivo);
        contrato.setIdPDF(pdfGuardado.getId());
        logger.info("PDF guardado para contrato ID: {}, PDF ID: {}", id, pdfGuardado.getId());
        return enrichContratoDTO(contratoRepository.save(contrato));
    }

    public byte[] obtenerPdf(Long id) {
        Contrato contrato = obtenerContratoEntidadPorId(id);
        Long idPDF = contrato.getIdPDF();
        if (idPDF == null) {
            throw new BusinessException(ErrorCodes.CONTRATO_NO_ENCONTRADO, "El contrato ID " + id + " no tiene un PDF asociado", HttpStatus.NOT_FOUND);
        }
        return pdfService.obtenerPDF(idPDF)
            .map(PDF::getFile)
            .filter(b -> b != null && b.length > 0)
            .orElseThrow(() -> new BusinessException(ErrorCodes.CONTRATO_NO_ENCONTRADO, "El PDF del contrato ID " + id + " no existe o está vacío", HttpStatus.NOT_FOUND));
    }

    // -------------------------------------------------------------------------
    // Helpers internos
    // -------------------------------------------------------------------------

    private EstadoContrato resolverEstadoContrato(Integer estadoContratoId) {
        if (estadoContratoId != null) {
            return estadoContratoRepository.findById(estadoContratoId)
                .orElseThrow(() -> new BusinessException(
                    ErrorCodes.ESTADO_CONTRATO_NO_ENCONTRADO, "No existe el estado de contrato indicado", HttpStatus.BAD_REQUEST));
        }
        return estadoContratoRepository.findByNombre("Vigente")
            .orElseThrow(() -> new BusinessException(
                ErrorCodes.ESTADO_CONTRATO_NO_ENCONTRADO, "No se pudo asignar el estado por defecto", HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private FechasContrato procesarYValidarFechas(ContratoCreateDTO dto) {
        String fechaInicioISO = convertirYValidarFecha(dto.getFechaInicio(), "fecha de inicio");
        String fechaFinISO = convertirYValidarFecha(dto.getFechaFin(), "fecha de fin");
        validarLogicaFechas(fechaInicioISO, fechaFinISO);
        String fechaAumento = calcularFechaAumento(fechaInicioISO, fechaFinISO, dto.getPeriodoAumento());
        return new FechasContrato(fechaInicioISO, fechaFinISO, fechaAumento);
    }

    private String convertirYValidarFecha(String fechaUsuario, String nombreCampo) {
        if (fechaUsuario == null) return null;
        if (!FechaUtil.esFechaValidaUsuario(fechaUsuario)) {
            throw new BusinessException(ErrorCodes.FORMATO_FECHA_INVALIDO,
                "Formato de " + nombreCampo + " inválido. Use dd/MM/yyyy (ej: 25/12/2024)", HttpStatus.BAD_REQUEST);
        }
        try {
            return FechaUtil.convertirFechaUsuarioToISODate(fechaUsuario);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCodes.FORMATO_FECHA_INVALIDO, e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private void validarLogicaFechas(String fechaInicioISO, String fechaFinISO) {
        String hoy = clockService.getCurrentDate().toString();
        if (fechaInicioISO != null && fechaFinISO != null && FechaUtil.compararFechas(fechaFinISO, fechaInicioISO) < 0) {
            throw new BusinessException(ErrorCodes.RANGO_DE_FECHAS_INVALIDO, "La fecha de fin no puede ser anterior a la fecha de inicio", HttpStatus.BAD_REQUEST);
        }
        if (fechaFinISO != null && FechaUtil.compararFechas(fechaFinISO, hoy) < 0) {
            throw new BusinessException(ErrorCodes.RANGO_DE_FECHAS_INVALIDO, "La fecha de fin no puede ser anterior a la fecha actual", HttpStatus.BAD_REQUEST);
        }
    }

    private String calcularFechaAumento(String fechaInicioISO, String fechaFinISO, Integer periodoAumento) {
        if (fechaInicioISO == null || periodoAumento == null || periodoAumento <= 0) return null;
        try {
            LocalDate fechaCalculada = LocalDate.parse(
                    FechaUtil.agregarMesesDate(fechaInicioISO, periodoAumento), DateTimeFormatter.ISO_LOCAL_DATE)
                .withDayOfMonth(1);
            String fechaAumento = fechaCalculada.format(DateTimeFormatter.ISO_LOCAL_DATE);
            if (fechaFinISO != null && FechaUtil.compararFechas(fechaAumento, fechaFinISO) > 0) {
                return "No aumenta más";
            }
            return fechaAumento;
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCodes.ERROR_CALCULO_FECHA, "Error calculando fecha de aumento: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private Contrato crearYGuardarContrato(ContratoCreateDTO dto, Inmueble inmueble, Inquilino inquilino,
                                            EstadoContrato estadoContrato, FechasContrato fechas) {
        Contrato contrato = new Contrato();
        contrato.setInmueble(inmueble);
        contrato.setInquilino(inquilino);
        contrato.setFechaInicio(fechas.fechaInicio);
        contrato.setFechaFin(fechas.fechaFin);
        contrato.setMonto(dto.getMonto());
        contrato.setPorcentajeAumento(dto.getPorcentajeAumento());
        contrato.setEstadoContrato(estadoContrato);
        contrato.setAumentaConIcl(dto.getAumentaConIcl() != null ? dto.getAumentaConIcl() : false);
        contrato.setPorcentajeHonorario(dto.getPorcentajeHonorario() != null ? dto.getPorcentajeHonorario() : new BigDecimal("10"));
        contrato.setPeriodoAumento(dto.getPeriodoAumento());
        contrato.setFechaAumento(fechas.fechaAumento);
        return contratoRepository.save(contrato);
    }

    private static class FechasContrato {
        final String fechaInicio;
        final String fechaFin;
        final String fechaAumento;

        FechasContrato(String fechaInicio, String fechaFin, String fechaAumento) {
            this.fechaInicio = fechaInicio;
            this.fechaFin = fechaFin;
            this.fechaAumento = fechaAumento;
        }
    }
}
