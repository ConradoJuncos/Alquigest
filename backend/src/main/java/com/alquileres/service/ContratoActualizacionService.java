package com.alquileres.service;

import com.alquileres.model.Contrato;
import com.alquileres.model.EstadoContrato;
import com.alquileres.model.Inmueble;
import com.alquileres.repository.ContratoRepository;
import com.alquileres.repository.EstadoContratoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Servicio para actualizar automáticamente el estado de contratos vencidos
 */
@Service
public class ContratoActualizacionService {

    private static final Logger logger = LoggerFactory.getLogger(ContratoActualizacionService.class);
    private static final DateTimeFormatter FORMATO_ISO_DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final ContratoRepository contratoRepository;
    private final EstadoContratoRepository estadoContratoRepository;
    private final ClockService clockService;
    private final InmuebleService inmuebleService;
    private final InquilinoService inquilinoService;

    public ContratoActualizacionService(ContratoRepository contratoRepository,
                                        EstadoContratoRepository estadoContratoRepository,
                                        ClockService clockService,
                                        InmuebleService inmuebleService,
                                        InquilinoService inquilinoService) {
        this.contratoRepository = contratoRepository;
        this.estadoContratoRepository = estadoContratoRepository;
        this.clockService = clockService;
        this.inmuebleService = inmuebleService;
        this.inquilinoService = inquilinoService;
    }

    /**
     * Actualiza todos los contratos vigentes que ya vencieron a estado "No Vigente"
     *
     * @return Cantidad de contratos actualizados
     */
    @Transactional
    public int actualizarContratosVencidos() {
        try {
            logger.info("Iniciando actualización de contratos vencidos");

            // Obtener la fecha actual en formato ISO desde clockService
            // Suma un dia porque sino los contratos vencen el dia de vencimiento (y deberian ser vigentes ese dia)
            String fechaActual = clockService.getCurrentDateTime().minusDays(1).format(FORMATO_ISO_DATETIME);

            logger.info("Fecha Actual (+1 día): {}", fechaActual);

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

                inmuebleService.actualizarEstadoInmueble(contrato.getInmueble());
                inquilinoService.marcarComoNoAlquilando(contrato.getInquilino());

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
}
