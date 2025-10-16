package com.alquileres.service;

import com.alquileres.model.Contrato;
import com.alquileres.model.ServicioXContrato;
import com.alquileres.model.TipoServicio;
import com.alquileres.repository.ContratoRepository;
import com.alquileres.repository.ServicioXContratoRepository;
import com.alquileres.repository.TipoServicioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar servicios asociados a contratos
 */
@Service
public class ServicioXContratoService {

    private static final Logger logger = LoggerFactory.getLogger(ServicioXContratoService.class);
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ISO_LOCAL_DATE;

    @Autowired
    private ServicioXContratoRepository servicioXContratoRepository;

    @Autowired
    private ContratoRepository contratoRepository;

    @Autowired
    private TipoServicioRepository tipoServicioRepository;

    @Autowired
    private ConfiguracionPagoServicioService configuracionPagoServicioService;

    /**
     * Crea un nuevo servicio para un contrato
     * Automáticamente crea la configuración de pago asociada
     *
     * @param contratoId ID del contrato
     * @param tipoServicioId ID del tipo de servicio
     * @param nroCuenta Número de cuenta (opcional)
     * @param nroContrato Número de contrato con el proveedor (opcional)
     * @param esDeInquilino Si el servicio está a nombre del inquilino
     * @param esAnual Si el pago es anual (false = mensual)
     * @param fechaInicio Fecha de inicio del servicio
     * @return El servicio creado
     */
    @Transactional
    public ServicioXContrato crearServicio(Long contratoId, Integer tipoServicioId,
                                           String nroCuenta, String nroContrato,
                                           Boolean esDeInquilino, Boolean esAnual,
                                           String fechaInicio) {
        // Validar que el contrato existe
        Contrato contrato = contratoRepository.findById(contratoId)
                .orElseThrow(() -> new RuntimeException("Contrato no encontrado con ID: " + contratoId));

        // Validar que el tipo de servicio existe
        TipoServicio tipoServicio = tipoServicioRepository.findById(tipoServicioId)
                .orElseThrow(() -> new RuntimeException("Tipo de servicio no encontrado con ID: " + tipoServicioId));

        // Crear el servicio x contrato
        ServicioXContrato servicio = new ServicioXContrato();
        servicio.setContrato(contrato);
        servicio.setTipoServicio(tipoServicio);
        servicio.setNroCuenta(nroCuenta);
        servicio.setNroContrato(nroContrato);
        servicio.setEsDeInquilino(esDeInquilino != null ? esDeInquilino : false);
        servicio.setEsAnual(esAnual != null ? esAnual : false);
        servicio.setEsActivo(true);

        // Guardar el servicio
        ServicioXContrato servicioGuardado = servicioXContratoRepository.save(servicio);
        logger.info("Servicio creado - Contrato ID: {}, Tipo: {}, Mensual: {}", 
                   contratoId, tipoServicio.getNombre(), !servicio.getEsAnual());

        // Crear la configuración de pago automática
        String fechaInicioFinal = fechaInicio != null ? fechaInicio : LocalDate.now().format(FORMATO_FECHA);
        configuracionPagoServicioService.crearConfiguracion(servicioGuardado, fechaInicioFinal);
        logger.info("Configuración de pago creada para servicio ID: {}", servicioGuardado.getId());

        return servicioGuardado;
    }

    /**
     * Obtiene todos los servicios de un contrato
     *
     * @param contratoId ID del contrato
     * @return Lista de servicios
     */
    public List<ServicioXContrato> obtenerServiciosPorContrato(Long contratoId) {
        return servicioXContratoRepository.findByContratoId(contratoId);
    }

    /**
     * Obtiene solo los servicios activos de un contrato
     *
     * @param contratoId ID del contrato
     * @return Lista de servicios activos
     */
    public List<ServicioXContrato> obtenerServiciosActivosPorContrato(Long contratoId) {
        return servicioXContratoRepository.findServiciosActivosByContratoId(contratoId);
    }

    /**
     * Desactiva un servicio
     * También desactiva su configuración de pago
     *
     * @param servicioId ID del servicio
     */
    @Transactional
    public void desactivarServicio(Integer servicioId) {
        Optional<ServicioXContrato> servicioOpt = servicioXContratoRepository.findById(servicioId);
        
        if (servicioOpt.isPresent()) {
            ServicioXContrato servicio = servicioOpt.get();
            servicio.setEsActivo(false);
            servicioXContratoRepository.save(servicio);
            
            // Desactivar también la configuración de pago
            configuracionPagoServicioService.obtenerPorServicioXContrato(servicioId)
                    .ifPresent(config -> configuracionPagoServicioService.desactivarConfiguracion(config.getId()));
            
            logger.info("Servicio desactivado ID: {}", servicioId);
        }
    }

    /**
     * Reactiva un servicio
     *
     * @param servicioId ID del servicio
     * @param nuevaFechaInicio Nueva fecha de inicio para reactivar
     */
    @Transactional
    public void reactivarServicio(Integer servicioId, String nuevaFechaInicio) {
        Optional<ServicioXContrato> servicioOpt = servicioXContratoRepository.findById(servicioId);
        
        if (servicioOpt.isPresent()) {
            ServicioXContrato servicio = servicioOpt.get();
            servicio.setEsActivo(true);
            servicioXContratoRepository.save(servicio);
            
            // Verificar si existe configuración
            Optional<com.alquileres.model.ConfiguracionPagoServicio> configOpt = 
                    configuracionPagoServicioService.obtenerPorServicioXContrato(servicioId);
            
            if (configOpt.isPresent()) {
                // Reactivar configuración existente
                com.alquileres.model.ConfiguracionPagoServicio config = configOpt.get();
                config.setEsActivo(true);
                config.setFechaInicio(nuevaFechaInicio);
            } else {
                // Crear nueva configuración
                configuracionPagoServicioService.crearConfiguracion(servicio, nuevaFechaInicio);
            }
            
            logger.info("Servicio reactivado ID: {}", servicioId);
        }
    }

    /**
     * Actualiza los datos de un servicio
     *
     * @param servicioId ID del servicio
     * @param nroCuenta Nuevo número de cuenta
     * @param nroContrato Nuevo número de contrato
     * @return El servicio actualizado
     */
    @Transactional
    public ServicioXContrato actualizarServicio(Integer servicioId, String nroCuenta, String nroContrato) {
        ServicioXContrato servicio = servicioXContratoRepository.findById(servicioId)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + servicioId));
        
        servicio.setNroCuenta(nroCuenta);
        servicio.setNroContrato(nroContrato);
        
        return servicioXContratoRepository.save(servicio);
    }

    /**
     * Obtiene un servicio por ID
     *
     * @param servicioId ID del servicio
     * @return El servicio si existe
     */
    public Optional<ServicioXContrato> obtenerServicioPorId(Integer servicioId) {
        return servicioXContratoRepository.findById(servicioId);
    }
}

