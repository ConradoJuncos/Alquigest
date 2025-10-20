package com.alquileres.service;

import com.alquileres.model.Alquiler;
import com.alquileres.model.Contrato;
import com.alquileres.model.ConfiguracionSistema;
import com.alquileres.repository.AlquilerRepository;
import com.alquileres.repository.ContratoRepository;
import com.alquileres.repository.ConfiguracionSistemaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para la actualización automática de alquileres
 * Genera automáticamente los alquileres mensuales para contratos vigentes
 */
@Service
public class AlquilerActualizacionService {

    private static final Logger logger = LoggerFactory.getLogger(AlquilerActualizacionService.class);
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter FORMATO_PERIODO = DateTimeFormatter.ofPattern("MM/yyyy");

    /**
     * Clave para almacenar el último mes procesado en la base de datos
     */
    private static final String CLAVE_ULTIMO_MES_PROCESADO = "ULTIMO_MES_PROCESADO_ALQUILERES";

    @Autowired
    private AlquilerRepository alquilerRepository;

    @Autowired
    private ContratoRepository contratoRepository;

    @Autowired
    private ConfiguracionSistemaRepository configuracionSistemaRepository;

    /**
     * Procesa la creación de alquileres pendientes
     * Solo procesa si el mes actual es diferente al último mes procesado
     *
     * @return Cantidad de alquileres creados
     */
    @Transactional
    public int procesarAlquileresPendientes() {
        try {
            String mesActual = LocalDate.now().format(FORMATO_PERIODO);
            String ultimoMesProcesado = obtenerUltimoMesProcesado();

            logger.info("Verificando procesamiento de alquileres. Mes actual: {}, Último procesado: {}",
                       mesActual, ultimoMesProcesado);

            // Si ya procesamos este mes, no hacer nada
            if (mesActual.equals(ultimoMesProcesado)) {
                logger.info("Los alquileres del mes {} ya fueron procesados. No se requiere acción.", mesActual);
                return 0;
            }

            logger.info("Iniciando procesamiento de alquileres para el mes {}", mesActual);

            // Crear alquileres para todos los contratos vigentes
            int alquileresCreados = crearAlquileresParaContratosVigentes();

            // Actualizar el último mes procesado
            actualizarUltimoMesProcesado(mesActual);

            logger.info("Procesamiento de alquileres completado. Total creados: {}", alquileresCreados);
            return alquileresCreados;

        } catch (Exception e) {
            logger.error("Error en procesamiento de alquileres: {}", e.getMessage(), e);
            return 0;
        }
    }

    /**
     * Obtiene el último mes procesado desde la base de datos
     *
     * @return El último mes procesado en formato MM/yyyy, o null si nunca se procesó
     */
    private String obtenerUltimoMesProcesado() {
        try {
            Optional<ConfiguracionSistema> config =
                configuracionSistemaRepository.findByClave(CLAVE_ULTIMO_MES_PROCESADO);

            if (config.isPresent()) {
                String valor = config.get().getValor();
                logger.debug("Último mes procesado obtenido de BD: {}", valor);
                return valor;
            } else {
                logger.debug("No se encontró registro de último mes procesado en BD");
                return null;
            }
        } catch (Exception e) {
            logger.error("Error al obtener último mes procesado: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Actualiza el último mes procesado en la base de datos
     *
     * @param mesActual Mes actual en formato MM/yyyy
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void actualizarUltimoMesProcesado(String mesActual) {
        try {
            Optional<ConfiguracionSistema> configOpt =
                configuracionSistemaRepository.findByClave(CLAVE_ULTIMO_MES_PROCESADO);

            if (configOpt.isPresent()) {
                // Actualizar valor existente
                ConfiguracionSistema config = configOpt.get();
                config.setValor(mesActual);
                configuracionSistemaRepository.save(config);
                logger.info("Último mes procesado actualizado en BD: {}", mesActual);
            } else {
                // Crear nuevo registro
                ConfiguracionSistema config = new ConfiguracionSistema(
                    CLAVE_ULTIMO_MES_PROCESADO,
                    mesActual,
                    "Último mes en que se procesaron los alquileres automáticamente"
                );
                configuracionSistemaRepository.save(config);
                logger.info("Registro de último mes procesado creado en BD: {}", mesActual);
            }
        } catch (Exception e) {
            logger.error("Error al actualizar último mes procesado: {}", e.getMessage(), e);
            // No lanzamos la excepción para evitar que falle todo el proceso
        }
    }

    /**
     * Crea alquileres para todos los contratos vigentes que no tengan alquileres pendientes
     * Usa transacciones independientes para evitar bloqueos en SQLite
     *
     * @return Cantidad de alquileres creados
     */
    public int crearAlquileresParaContratosVigentes() {
        try {
            logger.info("Iniciando creación automática de alquileres para contratos vigentes");

            // Obtener todos los contratos vigentes
            List<Contrato> contratosVigentes = contratoRepository.findAll().stream()
                    .filter(c -> c.getEstadoContrato() != null && "Vigente".equals(c.getEstadoContrato().getNombre()))
                    .collect(Collectors.toList());

            logger.info("Encontrados {} contratos vigentes para verificar alquileres", contratosVigentes.size());

            int alquileresCreados = 0;

            for (Contrato contrato : contratosVigentes) {
                try {
                    boolean creado = crearAlquilerParaContrato(contrato);
                    if (creado) {
                        alquileresCreados++;

                        // Delay para evitar bloqueos de SQLite
                        Thread.sleep(100);
                    }
                } catch (Exception e) {
                    logger.error("Error al crear alquiler para contrato ID {}: {}",
                               contrato.getId(), e.getMessage());
                }
            }

            logger.info("Creación automática de alquileres completada. Total: {}", alquileresCreados);
            return alquileresCreados;

        } catch (Exception e) {
            logger.error("Error en creación automática de alquileres: {}", e.getMessage(), e);
            return 0;
        }
    }

    /**
     * Crea un alquiler para un contrato específico si no tiene alquileres pendientes
     * Usa una transacción independiente para evitar bloqueos
     *
     * @param contrato El contrato para el cual crear el alquiler
     * @return true si se creó el alquiler, false en caso contrario
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected boolean crearAlquilerParaContrato(Contrato contrato) {
        try {
            // Verificar si el contrato ya tiene alquileres pendientes
            List<Alquiler> alquileresPendientes =
                alquilerRepository.findAlquileresPendientesByContratoId(contrato.getId());

            if (!alquileresPendientes.isEmpty()) {
                logger.debug("Contrato ID {} ya tiene alquileres pendientes, se omite creación",
                           contrato.getId());
                return false;
            }

            // Crear nuevo alquiler con vencimiento el día 10 del mes actual
            LocalDate fechaActual = LocalDate.now();
            LocalDate fechaVencimiento = LocalDate.of(fechaActual.getYear(), fechaActual.getMonth(), 10);
            String fechaVencimientoISO = fechaVencimiento.format(FORMATO_FECHA);

            Alquiler nuevoAlquiler = new Alquiler(contrato, fechaVencimientoISO, contrato.getMonto());
            alquilerRepository.save(nuevoAlquiler);

            logger.info("Alquiler creado automáticamente para contrato ID: {} - Monto: {}",
                       contrato.getId(), contrato.getMonto());
            return true;

        } catch (Exception e) {
            logger.error("Error al crear alquiler para contrato ID {}: {}",
                        contrato.getId(), e.getMessage(), e);
            return false;
        }
    }

    /**
     * Fuerza la creación de un alquiler para un contrato recién creado
     * Este método se ejecuta al crear un nuevo contrato para asegurar que tenga su primer alquiler
     *
     * @param contratoId ID del contrato recién creado
     * @return true si se creó el alquiler, false en caso contrario
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean generarAlquilerParaNuevoContrato(Long contratoId) {
        try {
            logger.info("Forzando generación de alquiler para nuevo contrato ID: {}", contratoId);

            Optional<Contrato> contratoOpt = contratoRepository.findById(contratoId);
            if (!contratoOpt.isPresent()) {
                logger.warn("Contrato no encontrado con ID: {}", contratoId);
                return false;
            }

            Contrato contrato = contratoOpt.get();

            // Verificar que el contrato esté vigente
            if (!"Vigente".equals(contrato.getEstadoContrato().getNombre())) {
                logger.debug("Contrato ID {} no está vigente, se omite creación de alquiler", contratoId);
                return false;
            }

            // Verificar si ya tiene alquileres pendientes
            List<Alquiler> alquileresPendientes =
                alquilerRepository.findAlquileresPendientesByContratoId(contratoId);

            if (!alquileresPendientes.isEmpty()) {
                logger.debug("Contrato ID {} ya tiene alquileres pendientes", contratoId);
                return false;
            }

            // Crear nuevo alquiler con vencimiento el día 10 del mes actual
            LocalDate fechaActual = LocalDate.now();
            LocalDate fechaVencimiento = LocalDate.of(fechaActual.getYear(), fechaActual.getMonth(), 10);
            String fechaVencimientoISO = fechaVencimiento.format(FORMATO_FECHA);

            Alquiler nuevoAlquiler = new Alquiler(contrato, fechaVencimientoISO, contrato.getMonto());
            alquilerRepository.save(nuevoAlquiler);

            logger.info("Alquiler forzado creado para nuevo contrato ID: {} - Monto: {}",
                       contratoId, contrato.getMonto());
            return true;

        } catch (Exception e) {
            logger.error("Error al generar alquiler para nuevo contrato ID {}: {}",
                        contratoId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Obtiene el último mes procesado (método público para consulta)
     *
     * @return El último mes procesado en formato MM/yyyy
     */
    public String getUltimoMesProcesado() {
        return obtenerUltimoMesProcesado();
    }
}

