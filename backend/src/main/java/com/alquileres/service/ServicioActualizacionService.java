package com.alquileres.service;

import com.alquileres.model.ConfiguracionPagoServicio;
import com.alquileres.model.ConfiguracionSistema;
import com.alquileres.model.PagoServicio;
import com.alquileres.model.ServicioXContrato;
import com.alquileres.repository.ConfiguracionPagoServicioRepository;
import com.alquileres.repository.ConfiguracionSistemaRepository;
import com.alquileres.repository.PagoServicioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para la actualización automática de configuraciones de pago de servicios
 * Genera automáticamente las facturas mensuales (PagoServicio)
 */
@Service
public class ServicioActualizacionService {

    private static final Logger logger = LoggerFactory.getLogger(ServicioActualizacionService.class);
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter FORMATO_PERIODO = DateTimeFormatter.ofPattern("MM/yyyy");

    /**
     * Clave para almacenar el último mes procesado en la base de datos
     */
    private static final String CLAVE_ULTIMO_MES_PROCESADO = "ULTIMO_MES_PROCESADO_PAGOS_SERVICIOS";

    @Autowired
    private ConfiguracionPagoServicioRepository configuracionPagoServicioRepository;

    @Autowired
    private PagoServicioRepository pagoServicioRepository;

    @Autowired
    private ConfiguracionPagoServicioService configuracionPagoServicioService;

    @Autowired
    private ConfiguracionSistemaRepository configuracionSistemaRepository;

    /**
     * Procesa todas las configuraciones activas que tienen pagos pendientes de generar
     * Se ejecuta al iniciar sesión y diariamente a las 00:01
     * Solo procesa si el mes actual es diferente al último mes procesado (guardado en BD)
     *
     * @return Cantidad de facturas generadas
     */
    @Transactional
    public int procesarPagosPendientes() {
        try {
            // Obtener el mes/año actual
            String mesActual = YearMonth.now().format(DateTimeFormatter.ofPattern("MM/yyyy"));

            // Obtener el último mes procesado desde la base de datos
            String ultimoMesProcesado = obtenerUltimoMesProcesado();

            // Verificar si ya se procesó en este mes
            if (mesActual.equals(ultimoMesProcesado)) {
                logger.debug("Los pagos ya fueron procesados en el mes actual ({}). No se procesarán nuevamente.", mesActual);
                return 0;
            }

            logger.info("Iniciando procesamiento de facturas de servicios pendientes para el mes {}", mesActual);

            // Obtener la fecha actual en formato ISO
            String fechaActual = LocalDate.now().format(FORMATO_FECHA);

            // Buscar todas las configuraciones con pagos pendientes
            List<ConfiguracionPagoServicio> configuracionesPendientes =
                configuracionPagoServicioRepository.findConfiguracionesConPagosPendientes(fechaActual);

            if (configuracionesPendientes.isEmpty()) {
                logger.info("No se encontraron facturas de servicios pendientes para generar");
                // Actualizar el último mes procesado aunque no haya facturas
                actualizarUltimoMesProcesado(mesActual);
                return 0;
            }

            int facturasGeneradas = 0;
            for (ConfiguracionPagoServicio configuracion : configuracionesPendientes) {
                try {
                    // Generar la nueva factura para este período
                    boolean generado = generarFacturaParaPeriodo(configuracion, fechaActual);

                    if (generado) {
                        facturasGeneradas++;
                        logger.debug("Factura generada para configuración ID: {}", configuracion.getId());
                    }

                } catch (Exception e) {
                    logger.error("Error al procesar configuración ID {}: {}",
                                configuracion.getId(), e.getMessage(), e);
                    // Continuar con la siguiente configuración
                }
            }

            // Actualizar el último mes procesado en la base de datos
            actualizarUltimoMesProcesado(mesActual);
            logger.info("Se generaron {} nuevas facturas para pagos pendientes. Último mes procesado actualizado a: {}",
                       facturasGeneradas, mesActual);

            return facturasGeneradas;

        } catch (Exception e) {
            logger.error("Error al procesar facturas de servicios pendientes: {}", e.getMessage(), e);
            // No lanzamos la excepción para que no afecte el login del usuario
            return 0;
        }
    }

    /**
     * Obtiene el último mes procesado desde la base de datos
     *
     * @return El último mes procesado en formato MM/yyyy, o null si nunca se procesó
     */
    private String obtenerUltimoMesProcesado() {
        Optional<ConfiguracionSistema> config = configuracionSistemaRepository.findByClave(CLAVE_ULTIMO_MES_PROCESADO);
        return config.map(ConfiguracionSistema::getValor).orElse(null);
    }

    /**
     * Actualiza el último mes procesado en la base de datos
     *
     * @param mesActual El mes actual en formato MM/yyyy
     */
    private void actualizarUltimoMesProcesado(String mesActual) {
        Optional<ConfiguracionSistema> configOpt = configuracionSistemaRepository.findByClave(CLAVE_ULTIMO_MES_PROCESADO);

        if (configOpt.isPresent()) {
            // Actualizar el valor existente
            ConfiguracionSistema config = configOpt.get();
            config.setValor(mesActual);
            configuracionSistemaRepository.save(config);
            logger.debug("Último mes procesado actualizado en BD: {}", mesActual);
        } else {
            // Crear nuevo registro
            ConfiguracionSistema config = new ConfiguracionSistema(
                CLAVE_ULTIMO_MES_PROCESADO,
                mesActual,
                "Último mes en que se procesaron los pagos de servicios automáticamente"
            );
            configuracionSistemaRepository.save(config);
            logger.debug("Registro de último mes procesado creado en BD: {}", mesActual);
        }
    }

    /**
     * Genera una nueva factura (PagoServicio) para el período correspondiente
     * y actualiza la configuración con el próximo pago
     *
     * @param configuracion La configuración de pago
     * @param fechaActual Fecha actual para validaciones
     * @return true si se generó la factura, false si ya existía o hubo error
     */
    private boolean generarFacturaParaPeriodo(ConfiguracionPagoServicio configuracion, String fechaActual) {
        try {
            ServicioXContrato servicio = configuracion.getServicioXContrato();

            // Verificar que el servicio esté activo
            if (!Boolean.TRUE.equals(servicio.getEsActivo())) {
                logger.warn("El servicio ID {} no está activo. Desactivando configuración.",
                           servicio.getId());
                configuracionPagoServicioService.desactivarConfiguracion(configuracion.getId());
                return false;
            }

            // Calcular el período en formato mm/aaaa
            String periodo = calcularPeriodo(configuracion.getProximoPago());

            // Verificar si ya existe una factura para este servicio y período
            if (pagoServicioRepository.existsByServicioXContratoIdAndPeriodo(servicio.getId(), periodo)) {
                logger.debug("Ya existe una factura para el período {} del servicio ID: {}",
                            periodo, servicio.getId());

                // Actualizar la configuración aunque ya exista la factura
                configuracionPagoServicioService.actualizarDespuesDeGenerarPago(
                    configuracion, configuracion.getProximoPago());

                return false;
            }

            // Crear la nueva factura (PagoServicio) para este período
            PagoServicio nuevaFactura = new PagoServicio();
            nuevaFactura.setServicioXContrato(servicio);
            nuevaFactura.setPeriodo(periodo);

            // La factura se crea sin pagar
            nuevaFactura.setEstaPagado(false);
            nuevaFactura.setEstaVencido(false);

            // Guardar la nueva factura
            pagoServicioRepository.save(nuevaFactura);
            logger.info("Nueva factura generada - Período: {}, Servicio: {}, Contrato ID: {}",
                       periodo, servicio.getTipoServicio().getNombre(),
                       servicio.getContrato().getId());

            // Actualizar la configuración con la nueva fecha de próximo pago
            configuracionPagoServicioService.actualizarDespuesDeGenerarPago(
                configuracion, configuracion.getProximoPago());

            return true;

        } catch (Exception e) {
            logger.error("Error al generar factura para configuración ID {}: {}",
                        configuracion.getId(), e.getMessage(), e);
            return false;
        }
    }

    /**
     * Calcula el período en formato mm/aaaa a partir de una fecha ISO
     *
     * @param fechaISO Fecha en formato ISO (yyyy-MM-dd)
     * @return Período en formato mm/aaaa
     */
    private String calcularPeriodo(String fechaISO) {
        try {
            LocalDate fecha = LocalDate.parse(fechaISO, FORMATO_FECHA);
            return fecha.format(FORMATO_PERIODO);
        } catch (Exception e) {
            logger.error("Error al calcular período desde fecha: {}", fechaISO, e);
            // En caso de error, retornar formato actual
            return LocalDate.now().format(FORMATO_PERIODO);
        }
    }

    /**
     * Calcula la fecha de vencimiento para una factura
     * Por defecto, el vencimiento es el día 10 del mes de la factura
     *
     * @param fechaBase Fecha base en formato ISO
     * @return Fecha de vencimiento en formato ISO
     */
    private String calcularFechaVencimiento(String fechaBase) {
        try {
            LocalDate fecha = LocalDate.parse(fechaBase, FORMATO_FECHA);
            // Establecer el vencimiento al día 10 del mes
            LocalDate vencimiento = fecha.withDayOfMonth(10);
            return vencimiento.format(FORMATO_FECHA);
        } catch (Exception e) {
            logger.error("Error al calcular fecha de vencimiento desde: {}", fechaBase, e);
            // En caso de error, retornar la fecha base
            return fechaBase;
        }
    }

    /**
     * Fuerza el procesamiento de pagos independientemente del mes
     * Útil para testing o procesamiento manual
     *
     * @return Cantidad de facturas generadas
     */
    @Transactional
    public int forzarProcesamientoPagos() {
        // Obtener el mes anterior desde la BD
        String mesAnterior = obtenerUltimoMesProcesado();

        // Eliminar la configuración para forzar el procesamiento
        configuracionSistemaRepository.findByClave(CLAVE_ULTIMO_MES_PROCESADO)
            .ifPresent(config -> configuracionSistemaRepository.delete(config));

        logger.info("Forzando procesamiento de pagos. Último mes procesado era: {}", mesAnterior);

        int resultado = procesarPagosPendientes();

        return resultado;
    }

    /**
     * Obtiene el último mes procesado desde la base de datos
     *
     * @return El último mes procesado en formato MM/yyyy, o null si nunca se procesó
     */
    public String getUltimoMesProcesado() {
        return obtenerUltimoMesProcesado();
    }
}
