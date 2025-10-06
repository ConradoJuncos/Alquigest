package com.alquileres.service;

import com.alquileres.model.Contrato;
import com.alquileres.model.EstadoContrato;
import com.alquileres.repository.ContratoRepository;
import com.alquileres.repository.EstadoContratoRepository;
import com.alquileres.util.FechaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Servicio para actualizar automáticamente el estado de contratos vencidos
 */
@Service
public class ContratoActualizacionService {

    private static final Logger logger = LoggerFactory.getLogger(ContratoActualizacionService.class);
    private static final DateTimeFormatter FORMATO_ISO_DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Autowired
    private ContratoRepository contratoRepository;

    @Autowired
    private EstadoContratoRepository estadoContratoRepository;

    /**
     * Actualiza todos los contratos vigentes que ya vencieron a estado "No Vigente"
     *
     * @return Cantidad de contratos actualizados
     */
    @Transactional
    public int actualizarContratosVencidos() {
        try {
            logger.info("Iniciando actualización de contratos vencidos");

            // Obtener la fecha actual en formato ISO
            String fechaActual = LocalDateTime.now().format(FORMATO_ISO_DATETIME);

            // Buscar todos los contratos vigentes que ya vencieron
            List<Contrato> contratosVencidos = contratoRepository.findContratosVigentesVencidos(fechaActual);

            if (contratosVencidos.isEmpty()) {
                logger.info("No se encontraron contratos vencidos para actualizar");
                return 0;
            }

            // Obtener el estado "No Vigente"
            EstadoContrato estadoNoVigente = estadoContratoRepository.findByNombre("No Vigente")
                    .orElseThrow(() -> new RuntimeException("Estado 'No Vigente' no encontrado en la base de datos"));

            // Actualizar cada contrato
            int contratosActualizados = 0;
            for (Contrato contrato : contratosVencidos) {
                contrato.setEstadoContrato(estadoNoVigente);
                contratoRepository.save(contrato);
                contratosActualizados++;
                logger.debug("Contrato ID {} actualizado a 'No Vigente'. Fecha fin: {}",
                            contrato.getId(), contrato.getFechaFin());
            }

            logger.info("Se actualizaron {} contratos de 'Vigente' a 'No Vigente'", contratosActualizados);
            return contratosActualizados;

        } catch (Exception e) {
            logger.error("Error al actualizar contratos vencidos: {}", e.getMessage(), e);
            // No lanzamos la excepción para que no afecte el login del usuario
            return 0;
        }
    }

    /**
     * Actualiza la fechaAumento de todos los contratos vigentes cuya fechaAumento ya pasó
     * Calcula la nueva fechaAumento sumando el periodoAumento a la fechaAumento actual
     * Si la nueva fechaAumento supera la fechaFin, se establece como "Sin Aumento"
     *
     * @return Cantidad de contratos actualizados
     */
    @Transactional
    public int actualizarFechasAumento() {
        try {
            logger.info("Iniciando actualización de fechas de aumento");

            // Obtener la fecha actual en formato ISO
            String fechaActual = LocalDateTime.now().format(FORMATO_ISO_DATETIME);

            // Buscar todos los contratos vigentes cuya fechaAumento ya pasó
            List<Contrato> contratosParaActualizar = contratoRepository.findContratosConFechaAumentoVencida(fechaActual);

            if (contratosParaActualizar.isEmpty()) {
                logger.info("No se encontraron contratos con fechaAumento vencida para actualizar");
                return 0;
            }

            int contratosActualizados = 0;
            for (Contrato contrato : contratosParaActualizar) {
                try {
                    // Validar que el contrato tenga periodoAumento configurado
                    if (contrato.getPeriodoAumento() == null || contrato.getPeriodoAumento() <= 0 || contrato.getPeriodoAumento() > 12) {
                        logger.warn("Contrato ID {} no tiene periodoAumento válido. Saltando actualización.", contrato.getId());
                        continue;
                    }

                    // Parsear la fechaAumento actual
                    LocalDateTime fechaAumentoActual = LocalDateTime.parse(contrato.getFechaAumento(), FORMATO_ISO_DATETIME);

                    // Calcular la nueva fechaAumento sumando el periodoAumento (en meses)
                    LocalDateTime nuevaFechaAumento = fechaAumentoActual.plusMonths(contrato.getPeriodoAumento());

                    // Parsear la fechaFin para comparar
                    LocalDateTime fechaFin = LocalDateTime.parse(contrato.getFechaFin(), FORMATO_ISO_DATETIME);

                    // Si la nueva fechaAumento es mayor a la fechaFin, establecer "Sin Aumento"
                    if (nuevaFechaAumento.isAfter(fechaFin)) {
                        contrato.setFechaAumento("Sin Aumento");
                        logger.debug("Contrato ID {} - Nueva fechaAumento supera fechaFin. Establecido como 'Sin Aumento'",
                                    contrato.getId());
                    } else {
                        // Establecer la nueva fechaAumento
                        String nuevaFechaAumentoStr = nuevaFechaAumento.format(FORMATO_ISO_DATETIME);
                        contrato.setFechaAumento(nuevaFechaAumentoStr);
                        logger.debug("Contrato ID {} - FechaAumento actualizada de {} a {}",
                                    contrato.getId(), contrato.getFechaAumento(), nuevaFechaAumentoStr);
                    }

                    contratoRepository.save(contrato);
                    contratosActualizados++;

                } catch (Exception e) {
                    logger.error("Error al actualizar fechaAumento del contrato ID {}: {}",
                                contrato.getId(), e.getMessage(), e);
                    // Continuar con el siguiente contrato
                }
            }

            logger.info("Se actualizaron {} fechas de aumento", contratosActualizados);
            return contratosActualizados;

        } catch (Exception e) {
            logger.error("Error al actualizar fechas de aumento: {}", e.getMessage(), e);
            // No lanzamos la excepción para que no afecte el login del usuario
            return 0;
        }
    }
}
