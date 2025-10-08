package com.alquileres.scheduler;

import com.alquileres.service.ContratoActualizacionService;
import com.alquileres.service.ServicioActualizacionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler para tareas automáticas relacionadas con contratos y servicios
 */
@Component
public class ContratoScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ContratoScheduler.class);

    @Autowired
    private ContratoActualizacionService contratoActualizacionService;

    @Autowired
    private ServicioActualizacionService servicioActualizacionService;

    /**
     * Actualiza automáticamente los contratos vencidos todos los días a las 00:01
     * Utiliza el mismo método que se ejecuta al iniciar sesión
     */
    @Scheduled(cron = "0 1 0 * * *")
    public void actualizarContratosVencidosProgramado() {
        logger.info("Ejecutando tarea programada: actualización de contratos vencidos");

        int contratosActualizados = contratoActualizacionService.actualizarContratosVencidos();

        logger.info("Tarea programada finalizada. Contratos actualizados: {}", contratosActualizados);
    }

    /**
     * Actualiza automáticamente las fechas de aumento de contratos todos los días a las 00:01
     * Utiliza el mismo método que se ejecuta al iniciar sesión
     */
    @Scheduled(cron = "0 1 0 * * *")
    public void actualizarFechasAumentoProgramado() {
        logger.info("Ejecutando tarea programada: actualización de fechas de aumento");

        int contratosActualizados = contratoActualizacionService.actualizarFechasAumento();

        logger.info("Tarea programada finalizada. Fechas de aumento actualizadas: {}", contratosActualizados);
    }

    /**
     * Procesa los pagos de servicios pendientes todos los días a las 00:01
     * Genera nuevos ServicioXInmueble para períodos que deben ser creados
     */
    @Scheduled(cron = "0 1 0 * * *")
    public void procesarPagosServiciosProgramado() {
        logger.info("Ejecutando tarea programada: procesamiento de pagos de servicios");

        int serviciosGenerados = servicioActualizacionService.procesarPagosPendientes();

        logger.info("Tarea programada finalizada. Servicios generados: {}", serviciosGenerados);
    }
}
