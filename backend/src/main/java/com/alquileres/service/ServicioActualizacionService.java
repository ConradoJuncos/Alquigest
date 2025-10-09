package com.alquileres.service;

import com.alquileres.model.ConfiguracionPagoServicio;
import com.alquileres.model.PagoServicio;
import com.alquileres.model.ServicioXInmueble;
import com.alquileres.repository.ConfiguracionPagoServicioRepository;
import com.alquileres.repository.PagoServicioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Servicio para la actualización automática de configuraciones de pago de servicios
 * Genera automáticamente las facturas mensuales (PagoServicio)
 */
@Service
public class ServicioActualizacionService {

    private static final Logger logger = LoggerFactory.getLogger(ServicioActualizacionService.class);
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter FORMATO_PERIODO = DateTimeFormatter.ofPattern("MM/yyyy");

    @Autowired
    private ConfiguracionPagoServicioRepository configuracionPagoServicioRepository;

    @Autowired
    private PagoServicioRepository pagoServicioRepository;

    @Autowired
    private ConfiguracionPagoServicioService configuracionPagoServicioService;

    /**
     * Procesa todas las configuraciones activas que tienen pagos pendientes de generar
     * Se ejecuta al iniciar sesión y diariamente a las 00:01
     *
     * @return Cantidad de facturas generadas
     */
    @Transactional
    public int procesarPagosPendientes() {
        try {
            logger.info("Iniciando procesamiento de facturas de servicios pendientes");

            // Obtener la fecha actual en formato ISO
            String fechaActual = LocalDate.now().format(FORMATO_FECHA);

            // Buscar todas las configuraciones con pagos pendientes
            List<ConfiguracionPagoServicio> configuracionesPendientes =
                configuracionPagoServicioRepository.findConfiguracionesConPagosPendientes(fechaActual);

            if (configuracionesPendientes.isEmpty()) {
                logger.info("No se encontraron facturas de servicios pendientes para generar");
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

            logger.info("Se generaron {} nuevas facturas para pagos pendientes", facturasGeneradas);
            return facturasGeneradas;

        } catch (Exception e) {
            logger.error("Error al procesar facturas de servicios pendientes: {}", e.getMessage(), e);
            // No lanzamos la excepción para que no afecte el login del usuario
            return 0;
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
            ServicioXInmueble servicio = configuracion.getServicioXInmueble();

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
            if (pagoServicioRepository.existsByServicioXInmuebleIdAndPeriodo(servicio.getId(), periodo)) {
                logger.debug("Ya existe una factura para el período {} del servicio ID: {}",
                            periodo, servicio.getId());

                // Actualizar la configuración aunque ya exista la factura
                configuracionPagoServicioService.actualizarDespuesDeGenerarPago(
                    configuracion, configuracion.getProximoPago());

                return false;
            }

            // Crear la nueva factura (PagoServicio) para este período
            PagoServicio nuevaFactura = new PagoServicio();
            nuevaFactura.setServicioXInmueble(servicio);
            nuevaFactura.setPeriodo(periodo);

            // Calcular la fecha de vencimiento (por ejemplo, el día 10 del mes siguiente)
            String fechaVencimiento = calcularFechaVencimiento(configuracion.getProximoPago());
            nuevaFactura.setFechaVencimiento(fechaVencimiento);

            // La factura se crea sin pagar
            nuevaFactura.setEstaPagado(false);
            nuevaFactura.setEstaVencido(false);

            // Guardar la nueva factura
            pagoServicioRepository.save(nuevaFactura);
            logger.info("Nueva factura generada - Período: {}, Servicio: {}, Inmueble ID: {}",
                       periodo, servicio.getTipoServicio().getNombre(),
                       servicio.getInmueble().getId());

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
}
