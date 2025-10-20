package com.alquileres.scheduler;

import com.alquileres.service.ContratoActualizacionService;
import com.alquileres.service.ServicioActualizacionService;
import com.alquileres.service.AlquilerActualizacionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler para tareas automáticas relacionadas con contratos, servicios y alquileres
 */
@Component
public class ContratoScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ContratoScheduler.class);

    @Autowired
    private ContratoActualizacionService contratoActualizacionService;

    @Autowired
    private ServicioActualizacionService servicioActualizacionService;

    @Autowired
    private AlquilerActualizacionService alquilerActualizacionService;

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
     * Genera las facturas de servicios pendientes el primer día de cada mes a las 00:01
     * Crea nuevos objetos PagoServicio para los períodos que deben ser generados
     * La lógica interna verifica que solo se procese una vez por mes
     */
    @Scheduled(cron = "0 1 0 1 * *")
    public void procesarPagosServiciosProgramado() {
        logger.info("Ejecutando tarea programada: generación de facturas de servicios (primer día del mes)");

        int facturasGeneradas = servicioActualizacionService.procesarPagosPendientes();

        logger.info("Tarea programada finalizada. Facturas generadas: {}", facturasGeneradas);
    }

    /**
     * Genera los alquileres pendientes el primer día de cada mes a las 00:02
     * Crea nuevos objetos Alquiler para contratos vigentes que no tengan alquileres pendientes
     * La lógica interna verifica que solo se procese una vez por mes
     */
    @Scheduled(cron = "0 2 0 1 * *")
    public void procesarAlquileresProgramado() {
        logger.info("Ejecutando tarea programada: generación de alquileres (primer día del mes)");

        int alquileresGenerados = alquilerActualizacionService.procesarAlquileresPendientes();

        logger.info("Tarea programada finalizada. Alquileres generados: {}", alquileresGenerados);
    }
}
