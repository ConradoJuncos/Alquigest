package com.alquileres.service;

import com.alquileres.dto.*;
import com.alquileres.exception.BusinessException;
import com.alquileres.exception.ErrorCodes;
import com.alquileres.model.*;
import com.alquileres.repository.AlquilerRepository;
import com.alquileres.repository.ContratoRepository;
import com.alquileres.util.BCRAApiClient;
import com.alquileres.util.FechaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AlquilerService {

    private static final Logger logger = LoggerFactory.getLogger(AlquilerService.class);

    private final AlquilerRepository alquilerRepository;
    private final ContratoRepository contratoRepository;
    private final PropietarioService propietarioService;
    private final AumentoAlquilerService aumentoAlquilerService;
    private final AlquilerActualizacionService alquilerActualizacionService;
    private final BCRAApiClient bcraApiClient;
    private final ClockService clockService;

    public AlquilerService(
            AlquilerRepository alquilerRepository,
            ContratoRepository contratoRepository,
            PropietarioService propietarioService,
            AumentoAlquilerService aumentoAlquilerService,
            AlquilerActualizacionService alquilerActualizacionService,
            BCRAApiClient bcraApiClient,
            ClockService clockService) {
        this.alquilerRepository = alquilerRepository;
        this.contratoRepository = contratoRepository;
        this.propietarioService = propietarioService;
        this.aumentoAlquilerService = aumentoAlquilerService;
        this.alquilerActualizacionService = alquilerActualizacionService;
        this.bcraApiClient = bcraApiClient;
        this.clockService = clockService;
    }

    public List<AlquilerDTO> obtenerTodosLosAlquileres() {
        return alquilerRepository.findAll().stream()
                .map(AlquilerDTO::new)
                .collect(Collectors.toList());
    }

    public AlquilerDTO obtenerAlquilerPorId(Long id) {
        return alquilerRepository.findById(id)
                .map(AlquilerDTO::new)
                .orElseThrow(() -> new BusinessException(
                        ErrorCodes.ALQUILER_NO_ENCONTRADO, "Alquiler no encontrado con ID: " + id, HttpStatus.NOT_FOUND));
    }

    public List<AlquilerDTO> obtenerAlquileresPorContrato(Long contratoId) {
        if (contratoRepository.findById(contratoId).isEmpty()) {
            throw new BusinessException(ErrorCodes.CONTRATO_NO_ENCONTRADO, "Contrato no encontrado con ID: " + contratoId, HttpStatus.NOT_FOUND);
        }
        return alquilerRepository.findByContratoId(contratoId).stream()
                .map(AlquilerDTO::new)
                .collect(Collectors.toList());
    }

    public List<AlquilerDTO> obtenerAlquileresPendientes() {
        return alquilerRepository.findByEstaPagado(false).stream()
                .map(AlquilerDTO::new)
                .collect(Collectors.toList());
    }

    public List<AlquilerDTO> obtenerAlquileresPagados() {
        return alquilerRepository.findByEstaPagado(true).stream()
                .map(AlquilerDTO::new)
                .collect(Collectors.toList());
    }

    public List<AlquilerDTO> obtenerAlquileresPendientesPorContrato(Long contratoId) {
        if (contratoRepository.findById(contratoId).isEmpty()) {
            throw new BusinessException(ErrorCodes.CONTRATO_NO_ENCONTRADO, "Contrato no encontrado con ID: " + contratoId, HttpStatus.NOT_FOUND);
        }
        return alquilerRepository.findAlquileresPendientesByContratoId(contratoId).stream()
                .map(AlquilerDTO::new)
                .collect(Collectors.toList());
    }

    public List<AlquilerDTO> obtenerAlquileresProximosAVencer(int diasAntes) {
        String fechaActual = clockService.getCurrentDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
        String fechaLimite = clockService.getCurrentDate().plusDays(diasAntes).format(DateTimeFormatter.ISO_LOCAL_DATE);
        return alquilerRepository.findAlquileresProximosAVencer(fechaActual, fechaLimite).stream()
                .map(AlquilerDTO::new)
                .collect(Collectors.toList());
    }

    public Long contarAlquileresPendientes() {
        return alquilerRepository.countAlquileresPendientes();
    }

    public Long contarAlquileresProximosAVencer(int diasAntes) {
        String fechaActual = clockService.getCurrentDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
        String fechaLimite = clockService.getCurrentDate().plusDays(diasAntes).format(DateTimeFormatter.ISO_LOCAL_DATE);
        return alquilerRepository.countAlquileresProximosAVencer(fechaActual, fechaLimite);
    }

    public AlquilerDTO crearAlquiler(AlquilerCreateDTO alquilerDTO) {
        Contrato contrato = contratoRepository.findById(alquilerDTO.getContratoId())
                .orElseThrow(() -> new BusinessException(ErrorCodes.CONTRATO_NO_ENCONTRADO, "No existe el contrato indicado", HttpStatus.BAD_REQUEST));

        if (!"Vigente".equals(contrato.getEstadoContrato().getNombre())) {
            throw new BusinessException(ErrorCodes.CONTRATO_NO_VIGENTE, "El contrato no está vigente", HttpStatus.BAD_REQUEST);
        }

        String fechaVencimientoISO;
        if (alquilerDTO.getFechaVencimientoPago() != null && !alquilerDTO.getFechaVencimientoPago().isBlank()) {
            if (!FechaUtil.esFechaValidaUsuario(alquilerDTO.getFechaVencimientoPago())) {
                throw new BusinessException(ErrorCodes.FORMATO_FECHA_INVALIDO,
                        "Formato de fecha de vencimiento inválido. Use dd/MM/yyyy (ej: 25/12/2024)", HttpStatus.BAD_REQUEST);
            }
            try {
                fechaVencimientoISO = FechaUtil.convertirFechaUsuarioToISODate(alquilerDTO.getFechaVencimientoPago());
            } catch (IllegalArgumentException e) {
                throw new BusinessException(ErrorCodes.FORMATO_FECHA_INVALIDO, e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        } else {
            fechaVencimientoISO = clockService.getCurrentDate().withDayOfMonth(10).format(DateTimeFormatter.ISO_LOCAL_DATE);
        }

        Alquiler alquiler = new Alquiler(contrato, fechaVencimientoISO, contrato.getMonto());
        alquiler.setEsActivo(true);
        return new AlquilerDTO(alquilerRepository.save(alquiler));
    }

    public AlquilerDTO actualizarAlquiler(Long id, AlquilerCreateDTO alquilerDTO) {
        Alquiler alquiler = alquilerRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCodes.ALQUILER_NO_ENCONTRADO, "Alquiler no encontrado con ID: " + id, HttpStatus.NOT_FOUND));

        if (alquilerDTO.getFechaVencimientoPago() != null && !alquilerDTO.getFechaVencimientoPago().isBlank()) {
            if (!FechaUtil.esFechaValidaUsuario(alquilerDTO.getFechaVencimientoPago())) {
                throw new BusinessException(ErrorCodes.FORMATO_FECHA_INVALIDO,
                        "Formato de fecha de vencimiento inválido. Use dd/MM/yyyy (ej: 25/12/2024)", HttpStatus.BAD_REQUEST);
            }
            try {
                alquiler.setFechaVencimientoPago(FechaUtil.convertirFechaUsuarioToISODate(alquilerDTO.getFechaVencimientoPago()));
            } catch (IllegalArgumentException e) {
                throw new BusinessException(ErrorCodes.FORMATO_FECHA_INVALIDO, e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }

        return new AlquilerDTO(alquilerRepository.save(alquiler));
    }

    public AlquilerDTO marcarComoPagado(Long id, RegistroPagoDTO registroPagoDTO) {
        Alquiler alquiler = alquilerRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCodes.ALQUILER_NO_ENCONTRADO, "Alquiler no encontrado con ID: " + id, HttpStatus.NOT_FOUND));

        alquiler.setEstaPagado(true);

        if (registroPagoDTO.getFechaPago() != null && !registroPagoDTO.getFechaPago().isEmpty()) {
            alquiler.setFechaPago(LocalDate.parse(registroPagoDTO.getFechaPago()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        } else {
            alquiler.setFechaPago(clockService.getCurrentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }

        if (registroPagoDTO.getCuentaBanco() != null) alquiler.setCuentaBanco(registroPagoDTO.getCuentaBanco());
        if (registroPagoDTO.getTitularDePago() != null) alquiler.setTitularDePago(registroPagoDTO.getTitularDePago());
        if (registroPagoDTO.getMetodo() != null) alquiler.setMetodo(registroPagoDTO.getMetodo());

        return new AlquilerDTO(alquilerRepository.save(alquiler));
    }

    public boolean existeAlquiler(Long id) {
        return alquilerRepository.existsById(id);
    }

    public BigDecimal calcularHonorarios() {
        List<Alquiler> alquileresPagados = alquilerRepository.findAlquileresPagadosDelMes();

        BigDecimal honorariosTotales = alquileresPagados.stream()
                .map(alquiler -> {
                    if (alquiler.getMonto() == null) return BigDecimal.ZERO;
                    BigDecimal porcentaje = Optional.ofNullable(alquiler.getContrato().getPorcentajeHonorario())
                            .orElse(new BigDecimal("10"));
                    return alquiler.getMonto().multiply(porcentaje).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        logger.info("Honorarios calculados: {} ({} alquileres pagados del mes)", honorariosTotales, alquileresPagados.size());
        return honorariosTotales;
    }

    public BigDecimal calcularHonorarioAlquilerEspecifico(Long alquilerId) {
        Alquiler alquiler = alquilerRepository.findById(alquilerId)
                .orElseThrow(() -> new RuntimeException("Alquiler no encontrado"));

        if (!alquiler.getEstaPagado()) {
            logger.warn("El alquiler {} no está pagado, no se calcula honorario", alquilerId);
            return BigDecimal.ZERO;
        }

        BigDecimal porcentaje = Optional.ofNullable(alquiler.getContrato().getPorcentajeHonorario())
                .orElse(new BigDecimal("10"));
        BigDecimal honorario = alquiler.getMonto().multiply(porcentaje).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        logger.info("Honorario calculado para alquiler {}: {} ({}% de {})", alquilerId, honorario, porcentaje, alquiler.getMonto());
        return honorario;
    }

    public AlquilerDetalladoDTO obtenerAlquilerDetallado(Long alquilerId) {
        Alquiler alquiler = alquilerRepository.findById(alquilerId)
                .orElseThrow(() -> new RuntimeException("Alquiler no encontrado"));

        Contrato contrato = alquiler.getContrato();
        Inmueble inmueble = contrato.getInmueble();

        PropietarioDTO propietario = propietarioService.obtenerPropietarioPorId(inmueble.getPropietarioId());

        BigDecimal honorarios = BigDecimal.ZERO;
        if (alquiler.getEstaPagado()) {
            BigDecimal porcentaje = Optional.ofNullable(contrato.getPorcentajeHonorario()).orElse(new BigDecimal("10"));
            honorarios = alquiler.getMonto().multiply(porcentaje).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        }

        return new AlquilerDetalladoDTO(
            alquilerId,
            propietario.getApellido(),
            propietario.getNombre(),
            inmueble.getDireccion(),
            alquiler.getMonto(),
            alquiler.getEstaPagado(),
            honorarios
        );
    }

    public List<NotificacionPagoAlquilerDTO> obtenerNotificacionesPagoAlquileresMes() {
        return alquilerRepository.findAlquileresNoPagadosDelMes().stream()
                .map(alquiler -> new NotificacionPagoAlquilerDTO(
                        alquiler.getContrato().getId(),
                        alquiler.getContrato().getInmueble().getId(),
                        alquiler.getContrato().getInquilino().getId(),
                        alquiler.getContrato().getInmueble().getDireccion(),
                        alquiler.getContrato().getInquilino().getApellido(),
                        alquiler.getContrato().getInquilino().getNombre()))
                .collect(Collectors.toList());
    }

    public AlquilerDTO aplicarAumentoManual(Long alquilerId, BigDecimal indiceInicial, BigDecimal indiceFinal) {
        logger.info("Aplicando aumento manual al alquiler ID: {}", alquilerId);

        Alquiler alquiler = alquilerRepository.findById(alquilerId)
                .orElseThrow(() -> new BusinessException(
                        "Alquiler no encontrado con ID: " + alquilerId, ErrorCodes.ALQUILER_NO_ENCONTRADO, HttpStatus.NOT_FOUND));

        if (!Boolean.TRUE.equals(alquiler.getNecesitaAumentoManual())) {
            throw new BusinessException(
                    "El alquiler ID " + alquilerId + " no está marcado para aumento manual",
                    ErrorCodes.DATOS_INVALIDOS, HttpStatus.BAD_REQUEST);
        }

        if (indiceInicial == null || indiceFinal == null || indiceInicial.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Los índices ICL deben ser mayores a cero", ErrorCodes.DATOS_INVALIDOS, HttpStatus.BAD_REQUEST);
        }

        BigDecimal tasaAumento = indiceFinal.divide(indiceInicial, 10, RoundingMode.HALF_UP);
        BigDecimal montoAnterior = alquiler.getMonto();
        BigDecimal nuevoMonto = montoAnterior.multiply(tasaAumento).setScale(2, RoundingMode.HALF_UP);
        BigDecimal porcentajeAumento = tasaAumento.subtract(BigDecimal.ONE)
                .multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP);

        logger.info("Alquiler ID {}: {} -> {}, tasa={}, porcentaje={}%", alquilerId, montoAnterior, nuevoMonto, tasaAumento, porcentajeAumento);

        alquiler.setMonto(nuevoMonto);
        alquiler.setNecesitaAumentoManual(false);
        Alquiler alquilerActualizado = alquilerRepository.save(alquiler);

        try {
            aumentoAlquilerService.crearYGuardarAumento(alquiler.getContrato(), montoAnterior, nuevoMonto, porcentajeAumento);
            logger.info("Aumento manual registrado para alquiler ID: {}", alquilerId);
        } catch (Exception e) {
            logger.error("Error al registrar el aumento en el historial: {}", e.getMessage());
        }

        return new AlquilerDTO(alquilerActualizado);
    }

    public List<AlquilerDTO> obtenerAlquileresConAumentoManualPendiente() {
        List<Alquiler> alquileres = alquilerRepository.findByNecesitaAumentoManualTrueAndEsActivoTrue();
        logger.info("Encontrados {} alquileres con aumento manual pendiente. Reintentando consulta BCRA...", alquileres.size());

        List<Alquiler> pendientes = new ArrayList<>();
        int actualizados = 0;

        for (Alquiler alquiler : alquileres) {
            try {
                Contrato contrato = alquiler.getContrato();
                if (!Boolean.TRUE.equals(contrato.getAumentaConIcl())) {
                    pendientes.add(alquiler);
                    continue;
                }

                AumentoAlquilerDTO aumentoAnterior = aumentoAlquilerService.obtenerUltimoAumento(contrato.getId());
                BigDecimal tasaAumento = bcraApiClient.obtenerTasaAumentoICL(
                        aumentoAnterior.getFechaAumento(), alquiler.getFechaVencimientoPago());

                logger.info("Consulta BCRA exitosa para alquiler ID {}. Tasa: {}", alquiler.getId(), tasaAumento);

                BigDecimal montoAnterior = alquiler.getMonto();
                BigDecimal nuevoMonto = montoAnterior.multiply(tasaAumento).setScale(2, RoundingMode.HALF_UP);
                BigDecimal porcentaje = tasaAumento.subtract(BigDecimal.ONE).multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP);

                alquiler.setMonto(nuevoMonto);
                alquiler.setNecesitaAumentoManual(false);
                alquilerRepository.save(alquiler);

                alquilerActualizacionService.actualizarFechaAumentoContrato(contrato);
                aumentoAlquilerService.crearYGuardarAumento(contrato, montoAnterior, nuevoMonto, porcentaje);

                logger.info("Alquiler ID {} actualizado: {} -> {}. Porcentaje: {}%", alquiler.getId(), montoAnterior, nuevoMonto, porcentaje);
                actualizados++;

            } catch (Exception e) {
                logger.warn("Fallo al reintentar BCRA para alquiler ID {}: {}. Se mantiene pendiente.", alquiler.getId(), e.getMessage());
                pendientes.add(alquiler);
            }
        }

        logger.info("Reintento completado: {} actualizados, {} pendientes", actualizados, pendientes.size());
        return pendientes.stream().map(AlquilerDTO::new).collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // Métodos invocados desde ContratoService
    // -------------------------------------------------------------------------

    /**
     * Devuelve el monto del último alquiler activo de un contrato, o null si no existe.
     */
    public BigDecimal obtenerMontoUltimoAlquiler(Long contratoId) {
        return alquilerRepository.findUltimoAlquilerByContratoId(contratoId)
                .map(Alquiler::getMonto)
                .orElse(null);
    }

    /**
     * Genera el primer alquiler para un contrato con fecha de inicio presente o futura.
     * El vencimiento se fija al día 10 del mes actual.
     */
    public void generarPrimerAlquiler(Contrato contrato) {
        try {
            LocalDate fechaVencimiento = clockService.getCurrentDate().withDayOfMonth(10);
            Alquiler alquiler = new Alquiler(contrato, fechaVencimiento.format(DateTimeFormatter.ISO_LOCAL_DATE), contrato.getMonto());
            alquiler.setEsActivo(true);
            alquilerRepository.save(alquiler);
            logger.info("Primer alquiler generado para contrato ID: {}, monto: {}", contrato.getId(), contrato.getMonto());
        } catch (Exception e) {
            logger.error("Error al generar primer alquiler para contrato ID {}: {}", contrato.getId(), e.getMessage());
        }
    }

    /**
     * Anula (borrado lógico) todos los alquileres activos de un contrato.
     */
    public void anularAlquileresDeContrato(Long contratoId) {
        List<Alquiler> alquileres = alquilerRepository.findAllByContratoId(contratoId);
        if (alquileres == null || alquileres.isEmpty()) {
            logger.info("No hay alquileres para anular en el contrato ID: {}", contratoId);
            return;
        }
        int desactivados = 0;
        for (Alquiler alquiler : alquileres) {
            if (alquiler.getEsActivo()) {
                alquiler.setEsActivo(false);
                desactivados++;
            }
        }
        if (desactivados > 0) {
            alquilerRepository.saveAll(alquileres);
            logger.info("Se anularon {} alquileres del contrato ID: {}", desactivados, contratoId);
        }
    }

    /**
     * Crea alquileres retroactivos desde la fecha de inicio hasta el mes actual (inclusive).
     * El alquiler del mes actual queda sin pagar; los anteriores se marcan como pagados.
     * Aplica aumentos (por ICL o porcentaje fijo) según la configuración del contrato.
     *
     * @return La siguiente fechaAumento calculada (ISO o "No aumenta más"), para que
     *         ContratoService actualice el contrato. Null si no hay período de aumento.
     */
    public String crearAlquileresRetroactivos(Contrato contrato, LocalDate fechaInicio, LocalDate fechaActual) {
        logger.info("Iniciando alquileres retroactivos para contrato ID: {}", contrato.getId());

        List<Alquiler> alquileresRetroactivos = new ArrayList<>();
        List<AumentoAlquiler> aumentosRetroactivos = new ArrayList<>();

        BigDecimal montoActual = contrato.getMonto();
        LocalDate fechaProximoAumento = calcularFechaProximoAumento(fechaInicio, contrato.getPeriodoAumento());
        LocalDate fechaUltimoAumento = fechaInicio.withDayOfMonth(1);

        int mesActual = fechaActual.getMonthValue();
        int anioActual = fechaActual.getYear();

        // Primer alquiler (mes de inicio del contrato)
        boolean primerEsMesActual = fechaInicio.getMonthValue() == mesActual && fechaInicio.getYear() == anioActual;
        Alquiler primerAlquiler = new Alquiler(contrato,
                fechaInicio.withDayOfMonth(10).format(DateTimeFormatter.ISO_LOCAL_DATE), montoActual);
        primerAlquiler.setEstaPagado(!primerEsMesActual);
        primerAlquiler.setFechaPago(primerEsMesActual ? null : fechaInicio.withDayOfMonth(10).format(DateTimeFormatter.ISO_LOCAL_DATE));
        primerAlquiler.setEsActivo(true);
        alquileresRetroactivos.add(primerAlquiler);

        // Alquileres desde el mes siguiente al inicio hasta el mes actual (inclusive)
        LocalDate fechaIteracion = fechaInicio.plusMonths(1).withDayOfMonth(1);

        while (fechaIteracion.getYear() < anioActual
                || (fechaIteracion.getYear() == anioActual && fechaIteracion.getMonthValue() <= mesActual)) {

            if (fechaProximoAumento != null
                    && fechaIteracion.getMonthValue() == fechaProximoAumento.getMonthValue()
                    && fechaIteracion.getYear() == fechaProximoAumento.getYear()) {

                BigDecimal montoAnterior = montoActual;
                LocalDate fechaSiguienteAumento = fechaProximoAumento.withDayOfMonth(1);

                if (Boolean.TRUE.equals(contrato.getAumentaConIcl())) {
                    montoActual = aplicarAumentoICL(contrato, montoAnterior, fechaUltimoAumento, fechaSiguienteAumento, aumentosRetroactivos);
                } else {
                    montoActual = aplicarAumentoFijo(contrato, montoAnterior, fechaIteracion, aumentosRetroactivos);
                }

                fechaUltimoAumento = fechaSiguienteAumento;
                fechaProximoAumento = calcularFechaProximoAumento(fechaProximoAumento, contrato.getPeriodoAumento());
            }

            boolean esMesActual = fechaIteracion.getMonthValue() == mesActual && fechaIteracion.getYear() == anioActual;
            String fechaVenc = fechaIteracion.withDayOfMonth(10).format(DateTimeFormatter.ISO_LOCAL_DATE);

            Alquiler alquiler = new Alquiler(contrato, fechaVenc, montoActual);
            alquiler.setEstaPagado(!esMesActual);
            alquiler.setFechaPago(esMesActual ? null : fechaVenc);
            alquiler.setEsActivo(true);
            alquileresRetroactivos.add(alquiler);

            fechaIteracion = fechaIteracion.plusMonths(1);
        }

        if (!alquileresRetroactivos.isEmpty()) {
            alquilerRepository.saveAll(alquileresRetroactivos);
            logger.info("Guardados {} alquileres retroactivos para contrato ID: {}", alquileresRetroactivos.size(), contrato.getId());
        }
        if (!aumentosRetroactivos.isEmpty()) {
            aumentoAlquilerService.guardarAumentosEnBatch(aumentosRetroactivos);
            logger.info("Guardados {} aumentos retroactivos para contrato ID: {}", aumentosRetroactivos.size(), contrato.getId());
        }

        // Calcular y devolver la próxima fechaAumento para que ContratoService actualice el contrato
        if (fechaProximoAumento == null) return null;

        String nuevaFechaAumento = fechaProximoAumento.format(DateTimeFormatter.ISO_LOCAL_DATE);
        if (contrato.getFechaFin() != null && !contrato.getFechaFin().isBlank()) {
            LocalDate fechaFin = LocalDate.parse(contrato.getFechaFin(), DateTimeFormatter.ISO_LOCAL_DATE);
            if (fechaProximoAumento.isAfter(fechaFin)) {
                return "No aumenta más";
            }
        }
        return nuevaFechaAumento;
    }

    private LocalDate calcularFechaProximoAumento(LocalDate fechaBase, Integer periodoAumento) {
        if (periodoAumento == null || periodoAumento <= 0) return null;
        return fechaBase.plusMonths(periodoAumento);
    }

    private BigDecimal aplicarAumentoICL(Contrato contrato, BigDecimal montoAnterior,
                                          LocalDate fechaUltimoAumento, LocalDate fechaSiguienteAumento,
                                          List<AumentoAlquiler> aumentosRetroactivos) {
        try {
            String fechaInicioISO = fechaUltimoAumento.withDayOfMonth(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
            String fechaFinISO = fechaSiguienteAumento.withDayOfMonth(1).format(DateTimeFormatter.ISO_LOCAL_DATE);

            BigDecimal tasaAumento = bcraApiClient.obtenerTasaAumentoICL(fechaInicioISO, fechaFinISO);
            BigDecimal montoNuevo = montoAnterior.multiply(tasaAumento).setScale(2, RoundingMode.HALF_UP);
            BigDecimal porcentaje = tasaAumento.subtract(BigDecimal.ONE).multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP);

            AumentoAlquiler aumento = aumentoAlquilerService.crearAumentoSinGuardar(contrato, montoAnterior, montoNuevo, porcentaje);
            aumento.setFechaAumento(fechaSiguienteAumento.format(DateTimeFormatter.ISO_LOCAL_DATE));
            aumento.setDescripcion("Aumento retroactivo por ICL");
            aumentosRetroactivos.add(aumento);

            logger.info("Aumento ICL retroactivo: {} -> {}, tasa={}", montoAnterior, montoNuevo, tasaAumento);
            return montoNuevo;

        } catch (Exception e) {
            logger.error("Error al consultar ICL para aumento retroactivo: {}. Se mantiene monto sin aumento.", e.getMessage());
            return montoAnterior;
        }
    }

    private BigDecimal aplicarAumentoFijo(Contrato contrato, BigDecimal montoAnterior,
                                           LocalDate fechaAumento,
                                           List<AumentoAlquiler> aumentosRetroactivos) {
        BigDecimal porcentaje = Optional.ofNullable(contrato.getPorcentajeAumento()).orElse(BigDecimal.ZERO);
        BigDecimal tasa = BigDecimal.ONE.add(porcentaje.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP));
        BigDecimal montoNuevo = montoAnterior.multiply(tasa).setScale(2, RoundingMode.HALF_UP);

        AumentoAlquiler aumento = aumentoAlquilerService.crearAumentoSinGuardar(contrato, montoAnterior, montoNuevo, porcentaje);
        aumento.setFechaAumento(fechaAumento.format(DateTimeFormatter.ISO_LOCAL_DATE));
        aumento.setDescripcion("Aumento retroactivo por porcentaje fijo");
        aumentosRetroactivos.add(aumento);

        logger.info("Aumento fijo retroactivo: {} -> {}, {}%", montoAnterior, montoNuevo, porcentaje);
        return montoNuevo;
    }
}
