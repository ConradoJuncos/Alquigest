package com.alquileres.service;

import com.alquileres.dto.*;
import com.alquileres.model.*;
import com.alquileres.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la generación de informes del sistema
 */
@Service
public class InformeService {

    private static final Logger logger = LoggerFactory.getLogger(InformeService.class);

    private final AlquilerRepository alquilerRepository;
    private final PagoServicioRepository pagoServicioRepository;
    private final AumentoAlquilerRepository aumentoAlquilerRepository;
    private final PropietarioService propietarioService;
    private final ClockService clockService;

    public InformeService(
            AlquilerRepository alquilerRepository,
            PagoServicioRepository pagoServicioRepository,
            AumentoAlquilerRepository aumentoAlquilerRepository,
            PropietarioService propietarioService,
            ClockService clockService) {
        this.alquilerRepository = alquilerRepository;
        this.pagoServicioRepository = pagoServicioRepository;
        this.aumentoAlquilerRepository = aumentoAlquilerRepository;
        this.propietarioService = propietarioService;
        this.clockService = clockService;
    }

    /**
     * Genera el Informe 1: Honorarios por inmueble del mes actual
     *
     * @return InformeHonorariosDTO con honorarios por inmueble y total
     */
    @Transactional(readOnly = true)
    public InformeHonorariosDTO generarInformeHonorarios() {
        logger.info("Generando informe de honorarios para el mes actual");

        LocalDate fechaActual = clockService.getCurrentDate();
        int mes = fechaActual.getMonthValue();
        int anio = fechaActual.getYear();
        String periodo = String.format("%02d/%d", mes, anio);

        // Obtener todos los alquileres pagados del mes actual de contratos vigentes
        List<Alquiler> alquileresPagados = alquilerRepository
                .findAlquileresPagadosPorMesYAnio(mes, anio);

        List<InformeHonorariosDTO.HonorarioPorInmuebleDTO> honorarios = new ArrayList<>();
        BigDecimal totalHonorarios = BigDecimal.ZERO;

        for (Alquiler alquiler : alquileresPagados) {
            Contrato contrato = alquiler.getContrato();
            Inmueble inmueble = contrato.getInmueble();
            Inquilino inquilino = contrato.getInquilino();

            // Obtener propietario
            Propietario propietario = propietarioService.obtenerEntidadONull(inmueble.getPropietarioId());
            if (propietario == null) continue;

            // Calcular honorario (10% del monto del alquiler)
            BigDecimal honorario = alquiler.getMonto()
                    .multiply(BigDecimal.valueOf(0.10))
                    .setScale(2, RoundingMode.HALF_UP);

            InformeHonorariosDTO.HonorarioPorInmuebleDTO dto = new InformeHonorariosDTO.HonorarioPorInmuebleDTO();
            dto.setInmuebleId(inmueble.getId());
            dto.setDireccionInmueble(inmueble.getDireccion());
            dto.setContratoId(contrato.getId());
            dto.setNombrePropietario(propietario.getNombre());
            dto.setApellidoPropietario(propietario.getApellido());
            dto.setNombreInquilino(inquilino.getNombre());
            dto.setApellidoInquilino(inquilino.getApellido());
            dto.setMontoAlquiler(alquiler.getMonto());
            dto.setHonorario(honorario);

            honorarios.add(dto);
            totalHonorarios = totalHonorarios.add(honorario);
        }

        logger.info("Informe de honorarios generado: {} inmuebles, total: {}",
                   honorarios.size(), totalHonorarios);

        return new InformeHonorariosDTO(periodo, honorarios, totalHonorarios);
    }

    /**
     * Genera el Informe 2: Pagos de alquileres del mes actual
     *
     * @return InformeAlquileresDTO con detalle de pagos y total
     */
    @Transactional(readOnly = true)
    public InformeAlquileresDTO generarInformeAlquileres() {
        logger.info("Generando informe de alquileres para el mes actual");

        LocalDate fechaActual = clockService.getCurrentDate();
        int mes = fechaActual.getMonthValue();
        int anio = fechaActual.getYear();
        String periodo = String.format("%02d/%d", mes, anio);

        // Obtener todos los alquileres del mes actual (pagados y no pagados) de contratos vigentes
        List<Alquiler> alquileres = alquilerRepository
                .findAlquileresPorMesYAnioYEstadoContrato(mes, anio, "Vigente");

        List<InformeAlquileresDTO.PagoAlquilerDetalleDTO> pagos = new ArrayList<>();
        BigDecimal totalPagado = BigDecimal.ZERO;
        BigDecimal montoPorCobrar = BigDecimal.ZERO;

        for (Alquiler alquiler : alquileres) {
            Contrato contrato = alquiler.getContrato();
            Inmueble inmueble = contrato.getInmueble();
            Inquilino inquilino = contrato.getInquilino();

            // Obtener propietario
            Propietario propietario = propietarioService.obtenerEntidadONull(inmueble.getPropietarioId());
            if (propietario == null) continue;

            InformeAlquileresDTO.PagoAlquilerDetalleDTO dto = new InformeAlquileresDTO.PagoAlquilerDetalleDTO();
            dto.setAlquilerId(alquiler.getId());
            dto.setContratoId(contrato.getId());
            dto.setDireccionInmueble(inmueble.getDireccion());
            dto.setNombreInquilino(inquilino.getNombre());
            dto.setApellidoInquilino(inquilino.getApellido());
            dto.setNombrePropietario(propietario.getNombre());
            dto.setApellidoPropietario(propietario.getApellido());
            dto.setMonto(alquiler.getMonto());
            dto.setFechaPago(alquiler.getFechaPago());
            dto.setFechaVencimiento(alquiler.getFechaVencimientoPago());
            dto.setEstaPagado(alquiler.getEstaPagado());

            pagos.add(dto);

            if (Boolean.TRUE.equals(alquiler.getEstaPagado())) {
                totalPagado = totalPagado.add(alquiler.getMonto());
            } else {
                montoPorCobrar = montoPorCobrar.add(alquiler.getMonto());
            }
        }

        logger.info("Informe de alquileres generado: {} alquileres, total pagado: {}, por cobrar: {}",
                   pagos.size(), totalPagado, montoPorCobrar);

        return new InformeAlquileresDTO(periodo, pagos, totalPagado, montoPorCobrar);
    }

    /**
     * Genera el Informe 3: Aumentos de alquiler de los últimos N meses
     *
     * @param meses Cantidad de meses hacia atrás para el informe (por defecto 6)
     * @return InformeAumentosDTO con aumentos agrupados por contrato
     */
    @Transactional(readOnly = true)
    public InformeAumentosDTO generarInformeAumentos(Integer meses) {
        // Si no se especifica meses, usar 6 por defecto
        if (meses == null || meses <= 0) {
            meses = 6;
        }

        logger.info("Generando informe de aumentos de los últimos {} meses", meses);

        LocalDate fechaActual = clockService.getCurrentDate();
        LocalDate fechaDesde = fechaActual.minusMonths(meses);

        String fechaDesdeStr = fechaDesde.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String fechaHastaStr = fechaActual.format(DateTimeFormatter.ISO_LOCAL_DATE);

        // Obtener aumentos del rango de meses especificado
        List<AumentoAlquiler> aumentos = aumentoAlquilerRepository
                .findAumentosPorRangoFecha(fechaDesdeStr, fechaHastaStr);

        // Agrupar aumentos por contrato
        var aumentosPorContrato = aumentos.stream()
                .collect(Collectors.groupingBy(a -> a.getContrato().getId()));

        List<InformeAumentosDTO.AumentoPorContratoDTO> listaAumentos = new ArrayList<>();

        for (var entry : aumentosPorContrato.entrySet()) {
            Long contratoId = entry.getKey();
            List<AumentoAlquiler> aumentosContrato = entry.getValue();

            if (aumentosContrato.isEmpty()) continue;

            Contrato contrato = aumentosContrato.get(0).getContrato();
            Inmueble inmueble = contrato.getInmueble();
            Inquilino inquilino = contrato.getInquilino();

            // Obtener propietario
            Propietario propietario = propietarioService.obtenerEntidadONull(inmueble.getPropietarioId());
            if (propietario == null) continue;

            InformeAumentosDTO.AumentoPorContratoDTO contratoDTO = new InformeAumentosDTO.AumentoPorContratoDTO();
            contratoDTO.setContratoId(contratoId);
            contratoDTO.setDireccionInmueble(inmueble.getDireccion());
            contratoDTO.setNombreInquilino(inquilino.getNombre());
            contratoDTO.setApellidoInquilino(inquilino.getApellido());
            contratoDTO.setNombrePropietario(propietario.getNombre());
            contratoDTO.setApellidoPropietario(propietario.getApellido());

            List<InformeAumentosDTO.DetalleAumentoDTO> detalles = aumentosContrato.stream()
                    .map(aumento -> {
                        InformeAumentosDTO.DetalleAumentoDTO detalle = new InformeAumentosDTO.DetalleAumentoDTO();
                        detalle.setAumentoId(aumento.getId());
                        detalle.setFechaAumento(aumento.getFechaAumento());
                        detalle.setMontoAnterior(aumento.getMontoAnterior());
                        detalle.setMontoNuevo(aumento.getMontoNuevo());
                        detalle.setPorcentajeAumento(aumento.getPorcentajeAumento());
                        return detalle;
                    })
                    .collect(Collectors.toList());

            contratoDTO.setAumentos(detalles);
            listaAumentos.add(contratoDTO);
        }

        String periodoDesde = fechaDesde.format(DateTimeFormatter.ofPattern("MM/yyyy"));
        String periodoHasta = fechaActual.format(DateTimeFormatter.ofPattern("MM/yyyy"));

        logger.info("Informe de aumentos generado: {} contratos con aumentos", listaAumentos.size());

        return new InformeAumentosDTO(periodoDesde, periodoHasta, listaAumentos);
    }

    /**
     * Genera el Informe 4: Pagos de servicios del mes actual con detalle completo
     * Agrupa los pagos por contrato
     *
     * @return InformePagosServiciosDTO con detalle completo de pagos de servicios agrupados por contrato
     */
    @Transactional(readOnly = true)
    public InformePagosServiciosDTO generarInformePagosServicios() {
        logger.info("Generando informe de pagos de servicios para el mes actual");

        LocalDate fechaActual = clockService.getCurrentDate();
        int mes = fechaActual.getMonthValue();
        int anio = fechaActual.getYear();
        String periodo = String.format("%02d/%d", mes, anio);

        // Obtener todos los pagos de servicios del mes actual
        List<Object[]> resultados = pagoServicioRepository
                .findPagosServiciosDelMesActualConDetalle(periodo);

        // Agrupar por contratoId
        var pagosPorContrato = new java.util.LinkedHashMap<Long, List<Object[]>>();
        for (Object[] resultado : resultados) {
            Long contratoId = (Long) resultado[6];
            pagosPorContrato.computeIfAbsent(contratoId, k -> new ArrayList<>()).add(resultado);
        }

        List<InformePagosServiciosDTO.PagosServiciosPorContratoDTO> contratosPagosServicios = new ArrayList<>();
        BigDecimal totalPagado = BigDecimal.ZERO;

        for (var entry : pagosPorContrato.entrySet()) {
            Long contratoId = entry.getKey();
            List<Object[]> pagosDelContrato = entry.getValue();

            if (pagosDelContrato.isEmpty()) continue;

            // Usar el primer pago para obtener datos del contrato
            Object[] primerPago = pagosDelContrato.get(0);

            InformePagosServiciosDTO.PagosServiciosPorContratoDTO contratoDTO =
                    new InformePagosServiciosDTO.PagosServiciosPorContratoDTO();

            contratoDTO.setContratoId(contratoId);
            contratoDTO.setDireccionInmueble((String) primerPago[7]);

            // Obtener propietario por ID
            Long propietarioId = (Long) primerPago[8];
            Propietario propietario = propietarioService.obtenerEntidadONull(propietarioId);
            if (propietario != null) {
                contratoDTO.setNombrePropietario(propietario.getNombre());
                contratoDTO.setApellidoPropietario(propietario.getApellido());
            }

            contratoDTO.setNombreInquilino((String) primerPago[9]);
            contratoDTO.setApellidoInquilino((String) primerPago[10]);

            // Datos del alquiler relacionado (común para todos los pagos del contrato)
            if (primerPago[11] != null) {
                InformePagosServiciosDTO.AlquilerRelacionadoDTO alquilerDTO =
                        new InformePagosServiciosDTO.AlquilerRelacionadoDTO();
                alquilerDTO.setAlquilerId((Long) primerPago[11]);
                alquilerDTO.setMontoAlquiler((BigDecimal) primerPago[12]);
                alquilerDTO.setFechaVencimientoAlquiler((String) primerPago[13]);
                alquilerDTO.setAlquilerPagado((Boolean) primerPago[14]);
                contratoDTO.setAlquilerRelacionado(alquilerDTO);
            }

            // Procesar todos los pagos de servicios de este contrato
            List<InformePagosServiciosDTO.PagoServicioDetalleDTO> pagosServicios = new ArrayList<>();
            BigDecimal subtotal = BigDecimal.ZERO;

            for (Object[] pago : pagosDelContrato) {
                InformePagosServiciosDTO.PagoServicioDetalleDTO pagoDTO =
                        new InformePagosServiciosDTO.PagoServicioDetalleDTO();

                pagoDTO.setPagoServicioId((Integer) pago[0]);
                pagoDTO.setFechaPago((String) pago[1]);
                pagoDTO.setMonto((BigDecimal) pago[2]);
                pagoDTO.setPeriodoServicio((String) pago[3]);
                pagoDTO.setTipoServicio((String) pago[4]);
                pagoDTO.setEstaPagado((Boolean) pago[5]);

                pagosServicios.add(pagoDTO);

                if (Boolean.TRUE.equals(pagoDTO.getEstaPagado())) {
                    subtotal = subtotal.add(pagoDTO.getMonto());
                }
            }

            contratoDTO.setPagosServicios(pagosServicios);
            contratoDTO.setSubtotalPagado(subtotal);
            contratosPagosServicios.add(contratoDTO);
            totalPagado = totalPagado.add(subtotal);
        }

        logger.info("Informe de pagos de servicios generado: {} contratos, {} pagos totales, total pagado: {}",
                   contratosPagosServicios.size(), resultados.size(), totalPagado);

        return new InformePagosServiciosDTO(periodo, contratosPagosServicios, totalPagado);
    }
}

