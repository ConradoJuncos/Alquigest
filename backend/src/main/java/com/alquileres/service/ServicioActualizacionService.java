package com.alquileres.service;

import com.alquileres.model.ConfiguracionPagoServicio;
import com.alquileres.model.ServicioXInmueble;
import com.alquileres.repository.ConfiguracionPagoServicioRepository;
import com.alquileres.repository.ServicioXInmuebleRepository;
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
 */
@Service
public class ServicioActualizacionService {

    private static final Logger logger = LoggerFactory.getLogger(ServicioActualizacionService.class);
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter FORMATO_PERIODO = DateTimeFormatter.ofPattern("MM/yyyy");

    @Autowired
    private ConfiguracionPagoServicioRepository configuracionPagoServicioRepository;

    @Autowired
    private ServicioXInmuebleRepository servicioXInmuebleRepository;

    @Autowired
    private ConfiguracionPagoServicioService configuracionPagoServicioService;

    /**
     * Procesa todas las configuraciones activas que tienen pagos pendientes de generar
     * Se ejecuta al iniciar sesión y diariamente a las 00:01
     *
     * @return Cantidad de servicios generados
     */
    @Transactional
    public int procesarPagosPendientes() {
        try {
            logger.info("Iniciando procesamiento de pagos de servicios pendientes");

            // Obtener la fecha actual en formato ISO
            String fechaActual = LocalDate.now().format(FORMATO_FECHA);

            // Buscar todas las configuraciones con pagos pendientes
            List<ConfiguracionPagoServicio> configuracionesPendientes =
                configuracionPagoServicioRepository.findConfiguracionesConPagosPendientes(fechaActual);

            if (configuracionesPendientes.isEmpty()) {
                logger.info("No se encontraron pagos de servicios pendientes para generar");
                return 0;
            }

            int serviciosGenerados = 0;
            for (ConfiguracionPagoServicio configuracion : configuracionesPendientes) {
                try {
                    // Generar el nuevo servicio x inmueble para este período
                    boolean generado = generarServicioParaPeriodo(configuracion, fechaActual);

                    if (generado) {
                        serviciosGenerados++;
                        logger.debug("Servicio generado para configuración ID: {}", configuracion.getId());
                    }

                } catch (Exception e) {
                    logger.error("Error al procesar configuración ID {}: {}",
                                configuracion.getId(), e.getMessage(), e);
                    // Continuar con la siguiente configuración
                }
            }

            logger.info("Se generaron {} nuevos servicios para pagos pendientes", serviciosGenerados);
            return serviciosGenerados;

        } catch (Exception e) {
            logger.error("Error al procesar pagos de servicios pendientes: {}", e.getMessage(), e);
            // No lanzamos la excepción para que no afecte el login del usuario
            return 0;
        }
    }

    /**
     * Genera un nuevo ServicioXInmueble para el período correspondiente
     * y actualiza la configuración con el próximo pago
     *
     * @param configuracion La configuración de pago
     * @param fechaActual Fecha actual para validaciones
     * @return true si se generó el servicio, false si ya existía o hubo error
     */
    private boolean generarServicioParaPeriodo(ConfiguracionPagoServicio configuracion, String fechaActual) {
        try {
            ServicioXInmueble servicioBase = configuracion.getServicioXInmueble();

            // Verificar que el servicio base esté activo
            if (!Boolean.TRUE.equals(servicioBase.getEsActivo())) {
                logger.warn("El servicio base ID {} no está activo. Desactivando configuración.",
                           servicioBase.getId());
                configuracionPagoServicioService.desactivarConfiguracion(configuracion.getId());
                return false;
            }

            // Calcular el período en formato mm/aaaa
            String periodo = calcularPeriodo(configuracion.getProximoPago());

            // Verificar si ya existe un servicio para este inmueble, tipo de servicio y período
            if (yaExisteServicioParaPeriodo(servicioBase, periodo)) {
                logger.debug("Ya existe un servicio para el período {} del inmueble ID: {}",
                            periodo, servicioBase.getInmueble().getId());

                // Actualizar la configuración aunque ya exista el servicio
                configuracionPagoServicioService.actualizarDespuesDeGenerarPago(
                    configuracion, configuracion.getProximoPago());

                return false;
            }

            // Crear el nuevo ServicioXInmueble para este período
            ServicioXInmueble nuevoServicio = new ServicioXInmueble();
            nuevoServicio.setInmueble(servicioBase.getInmueble());
            nuevoServicio.setTipoServicio(servicioBase.getTipoServicio());
            nuevoServicio.setPeriodo(periodo);
            nuevoServicio.setNroCuenta(servicioBase.getNroCuenta());
            nuevoServicio.setNroContrato(servicioBase.getNroContrato());
            nuevoServicio.setEsDeInquilino(servicioBase.getEsDeInquilino());
            nuevoServicio.setEsAnual(servicioBase.getEsAnual());
            nuevoServicio.setEsActivo(true);

            // Guardar el nuevo servicio
            servicioXInmuebleRepository.save(nuevoServicio);
            logger.info("Nuevo servicio generado - Período: {}, Tipo: {}, Inmueble ID: {}",
                       periodo, servicioBase.getTipoServicio().getNombre(),
                       servicioBase.getInmueble().getId());

            // Actualizar la configuración con la nueva fecha de próximo pago
            configuracionPagoServicioService.actualizarDespuesDeGenerarPago(
                configuracion, configuracion.getProximoPago());

            return true;

        } catch (Exception e) {
            logger.error("Error al generar servicio para configuración ID {}: {}",
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
     * Verifica si ya existe un servicio para el inmueble, tipo de servicio y período dados
     *
     * @param servicioBase El servicio base
     * @param periodo El período a verificar (mm/aaaa)
     * @return true si ya existe, false en caso contrario
     */
    private boolean yaExisteServicioParaPeriodo(ServicioXInmueble servicioBase, String periodo) {
        List<ServicioXInmueble> serviciosExistentes =
            servicioXInmuebleRepository.findByInmuebleId(servicioBase.getInmueble().getId());

        return serviciosExistentes.stream()
            .anyMatch(s ->
                s.getTipoServicio().getId().equals(servicioBase.getTipoServicio().getId()) &&
                periodo.equals(s.getPeriodo())
            );
    }
}

