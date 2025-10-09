package com.alquileres.service;

import com.alquileres.dto.ActualizacionMontoServicioDTO;
import com.alquileres.dto.ActualizacionMontosServiciosRequest;
import com.alquileres.model.PagoServicio;
import com.alquileres.repository.ContratoRepository;
import com.alquileres.repository.PagoServicioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio para la gestión de pagos de servicios
 */
@Service
public class PagoServicioService {

    private static final Logger logger = LoggerFactory.getLogger(PagoServicioService.class);

    @Autowired
    private PagoServicioRepository pagoServicioRepository;

    @Autowired
    private ContratoRepository contratoRepository;

    /**
     * Actualiza los montos de los pagos de servicios no pagados de un contrato
     *
     * @param request Datos de actualización con contratoId y lista de actualizaciones
     * @return Mapa con resumen de actualizaciones realizadas
     */
    @Transactional
    public Map<String, Object> actualizarMontosPagosNoPagados(ActualizacionMontosServiciosRequest request) {
        logger.info("Iniciando actualización de montos para contrato ID: {}", request.getContratoId());

        // Verificar que el contrato existe
        if (!contratoRepository.existsById(request.getContratoId())) {
            throw new RuntimeException("El contrato con ID " + request.getContratoId() + " no existe");
        }

        Map<String, Object> resultado = new HashMap<>();
        int totalActualizados = 0;
        Map<Integer, Integer> detallesPorTipoServicio = new HashMap<>();
// leer
        // Procesar cada actualización de tipo de servicio
        for (ActualizacionMontoServicioDTO actualizacion : request.getActualizaciones()) {
            // Validar que el monto no sea nulo o cero
            if (actualizacion.getNuevoMonto() == null ||
                actualizacion.getNuevoMonto().compareTo(BigDecimal.ZERO) <= 0) {
                logger.warn("Monto inválido para tipo de servicio ID: {}. Se omite.",
                           actualizacion.getTipoServicioId());
                continue;
            }

            // Buscar todos los pagos no pagados para este contrato y tipo de servicio
            List<PagoServicio> pagosNoPagados = pagoServicioRepository
                .findPagosNoPagadosByContratoAndTipoServicio(
                    request.getContratoId(),
                    actualizacion.getTipoServicioId()
                );

            int actualizadosEnEsteTipo = 0;

            // Actualizar el monto de cada pago
            for (PagoServicio pago : pagosNoPagados) {
                pago.setMonto(actualizacion.getNuevoMonto());
                pagoServicioRepository.save(pago);
                actualizadosEnEsteTipo++;
                totalActualizados++;

                logger.debug("Actualizado monto del pago ID: {} a {}",
                            pago.getId(), actualizacion.getNuevoMonto());
            }

            detallesPorTipoServicio.put(actualizacion.getTipoServicioId(), actualizadosEnEsteTipo);

            logger.info("Actualizados {} pagos del tipo de servicio ID: {} con monto: {}",
                       actualizadosEnEsteTipo,
                       actualizacion.getTipoServicioId(),
                       actualizacion.getNuevoMonto());
        }

        resultado.put("contratoId", request.getContratoId());
        resultado.put("totalPagosActualizados", totalActualizados);
        resultado.put("detallesPorTipoServicio", detallesPorTipoServicio);
        resultado.put("mensaje", "Actualización completada exitosamente");

        logger.info("Actualización completada. Total de pagos actualizados: {}", totalActualizados);

        return resultado;
    }

    /**
     * Obtiene todos los pagos no pagados de un contrato
     *
     * @param contratoId ID del contrato
     * @return Lista de pagos no pagados
     */
    public List<PagoServicio> obtenerPagosNoPagadosPorContrato(Long contratoId) {
        return pagoServicioRepository.findPagosNoPagadosByContrato(contratoId);
    }

    /**
     * Obtiene todos los pagos de un contrato
     *
     * @param contratoId ID del contrato
     * @return Lista de todos los pagos del contrato
     */
    public List<PagoServicio> obtenerPagosPorContrato(Long contratoId) {
        return pagoServicioRepository.findByContratoId(contratoId);
    }
}

