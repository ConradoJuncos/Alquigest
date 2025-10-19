package com.alquileres.service;

import com.alquileres.dto.AlquilerDTO;
import com.alquileres.dto.AlquilerCreateDTO;
import com.alquileres.dto.RegistroPagoDTO;
import com.alquileres.model.Alquiler;
import com.alquileres.model.Contrato;
import com.alquileres.repository.AlquilerRepository;
import com.alquileres.repository.ContratoRepository;
import com.alquileres.exception.BusinessException;
import com.alquileres.exception.ErrorCodes;
import com.alquileres.util.FechaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AlquilerService {

    @Autowired
    private AlquilerRepository alquilerRepository;

    @Autowired
    private ContratoRepository contratoRepository;

    // Obtener todos los alquileres
    public List<AlquilerDTO> obtenerTodosLosAlquileres() {
        List<Alquiler> alquileres = alquilerRepository.findAll();
        return alquileres.stream()
                .map(AlquilerDTO::new)
                .collect(Collectors.toList());
    }

    // Obtener alquiler por ID
    public AlquilerDTO obtenerAlquilerPorId(Long id) {
        Optional<Alquiler> alquiler = alquilerRepository.findById(id);
        if (alquiler.isPresent()) {
            return new AlquilerDTO(alquiler.get());
        } else {
            throw new BusinessException(ErrorCodes.ALQUILER_NO_ENCONTRADO, "Alquiler no encontrado con ID: " + id, HttpStatus.NOT_FOUND);
        }
    }

    // Obtener alquileres por contrato
    public List<AlquilerDTO> obtenerAlquileresPorContrato(Long contratoId) {
        Optional<Contrato> contrato = contratoRepository.findById(contratoId);
        if (!contrato.isPresent()) {
            throw new BusinessException(ErrorCodes.CONTRATO_NO_ENCONTRADO, "Contrato no encontrado con ID: " + contratoId, HttpStatus.NOT_FOUND);
        }

        List<Alquiler> alquileres = alquilerRepository.findByContratoId(contratoId);
        return alquileres.stream()
                .map(AlquilerDTO::new)
                .collect(Collectors.toList());
    }

    // Obtener alquileres pendientes
    public List<AlquilerDTO> obtenerAlquileresPendientes() {
        List<Alquiler> alquileres = alquilerRepository.findByEstaPagado(false);
        return alquileres.stream()
                .map(AlquilerDTO::new)
                .collect(Collectors.toList());
    }

    // Obtener alquileres pagados
    public List<AlquilerDTO> obtenerAlquileresPagados() {
        List<Alquiler> alquileres = alquilerRepository.findByEstaPagado(true);
        return alquileres.stream()
                .map(AlquilerDTO::new)
                .collect(Collectors.toList());
    }

    // Obtener alquileres pendientes por contrato
    public List<AlquilerDTO> obtenerAlquileresPendientesPorContrato(Long contratoId) {
        Optional<Contrato> contrato = contratoRepository.findById(contratoId);
        if (!contrato.isPresent()) {
            throw new BusinessException(ErrorCodes.CONTRATO_NO_ENCONTRADO, "Contrato no encontrado con ID: " + contratoId, HttpStatus.NOT_FOUND);
        }

        List<Alquiler> alquileres = alquilerRepository.findAlquileresPendientesByContratoId(contratoId);
        return alquileres.stream()
                .map(AlquilerDTO::new)
                .collect(Collectors.toList());
    }

    // Obtener alquileres próximos a vencer
    public List<AlquilerDTO> obtenerAlquileresProximosAVencer(int diasAntes) {
        String fechaActual = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        String fechaLimite = LocalDate.now().plusDays(diasAntes).format(DateTimeFormatter.ISO_LOCAL_DATE);

        List<Alquiler> alquileres = alquilerRepository.findAlquileresProximosAVencer(fechaActual, fechaLimite);
        return alquileres.stream()
                .map(AlquilerDTO::new)
                .collect(Collectors.toList());
    }

    // Contar alquileres pendientes
    public Long contarAlquileresPendientes() {
        return alquilerRepository.countAlquileresPendientes();
    }

    // Contar alquileres próximos a vencer
    public Long contarAlquileresProximosAVencer(int diasAntes) {
        String fechaActual = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        String fechaLimite = LocalDate.now().plusDays(diasAntes).format(DateTimeFormatter.ISO_LOCAL_DATE);
        return alquilerRepository.countAlquileresProximosAVencer(fechaActual, fechaLimite);
    }

    // Crear nuevo alquiler
    public AlquilerDTO crearAlquiler(AlquilerCreateDTO alquilerDTO) {
        // Validar que existe el contrato
        Optional<Contrato> contrato = contratoRepository.findById(alquilerDTO.getContratoId());
        if (!contrato.isPresent()) {
            throw new BusinessException(ErrorCodes.CONTRATO_NO_ENCONTRADO, "No existe el contrato indicado", HttpStatus.BAD_REQUEST);
        }

        // Validar que el contrato esté vigente
        if (!"Vigente".equals(contrato.get().getEstadoContrato().getNombre())) {
            throw new BusinessException(ErrorCodes.CONTRATO_NO_VIGENTE, "El contrato no está vigente", HttpStatus.BAD_REQUEST);
        }

        // Validar y convertir fecha de vencimiento
        String fechaVencimientoISO = null;
        if (alquilerDTO.getFechaVencimientoPago() != null && !alquilerDTO.getFechaVencimientoPago().trim().isEmpty()) {
            // Si se proporciona una fecha, validarla y convertirla
            if (!FechaUtil.esFechaValidaUsuario(alquilerDTO.getFechaVencimientoPago())) {
                throw new BusinessException(ErrorCodes.FORMATO_FECHA_INVALIDO,
                    "Formato de fecha de vencimiento inválido. Use dd/MM/yyyy (ej: 25/12/2024)", HttpStatus.BAD_REQUEST);
            }
            try {
                fechaVencimientoISO = FechaUtil.convertirFechaUsuarioToISO(alquilerDTO.getFechaVencimientoPago());
            } catch (IllegalArgumentException e) {
                throw new BusinessException(ErrorCodes.FORMATO_FECHA_INVALIDO, e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        } else {
            // Si no se proporciona fecha, usar el día 10 del mes actual
            LocalDate fechaActual = LocalDate.now();
            LocalDate fechaConDia10 = LocalDate.of(fechaActual.getYear(), fechaActual.getMonth(), 10);
            fechaVencimientoISO = fechaConDia10.format(DateTimeFormatter.ISO_LOCAL_DATE);
        }

        // Crear el alquiler usando el constructor optimizado
        // Solo se setean: contrato, fechaVencimientoPago, monto (del contrato) y estaPagado=false
        // Los campos de pago (cuentaBanco, titularDePago, metodo) quedan null hasta que se registre el pago
        Alquiler alquiler = new Alquiler(contrato.get(), fechaVencimientoISO, contrato.get().getMonto());

        // Guardar el alquiler
        Alquiler alquilerGuardado = alquilerRepository.save(alquiler);

        return new AlquilerDTO(alquilerGuardado);
    }

    // Actualizar alquiler
    public AlquilerDTO actualizarAlquiler(Long id, AlquilerCreateDTO alquilerDTO) {
        // Verificar que existe el alquiler
        Optional<Alquiler> alquilerExistente = alquilerRepository.findById(id);
        if (!alquilerExistente.isPresent()) {
            throw new BusinessException(ErrorCodes.ALQUILER_NO_ENCONTRADO, "Alquiler no encontrado con ID: " + id, HttpStatus.NOT_FOUND);
        }

        Alquiler alquiler = alquilerExistente.get();

        // Validar y convertir fecha de vencimiento si se proporciona
        if (alquilerDTO.getFechaVencimientoPago() != null && !alquilerDTO.getFechaVencimientoPago().trim().isEmpty()) {
            if (!FechaUtil.esFechaValidaUsuario(alquilerDTO.getFechaVencimientoPago())) {
                throw new BusinessException(ErrorCodes.FORMATO_FECHA_INVALIDO,
                    "Formato de fecha de vencimiento inválido. Use dd/MM/yyyy (ej: 25/12/2024)", HttpStatus.BAD_REQUEST);
            }
            try {
                String fechaVencimientoISO = FechaUtil.convertirFechaUsuarioToISO(alquilerDTO.getFechaVencimientoPago());
                alquiler.setFechaVencimientoPago(fechaVencimientoISO);
            } catch (IllegalArgumentException e) {
                throw new BusinessException(ErrorCodes.FORMATO_FECHA_INVALIDO, e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }

        // Guardar cambios
        Alquiler alquilerActualizado = alquilerRepository.save(alquiler);

        return new AlquilerDTO(alquilerActualizado);
    }

    // Marcar alquiler como pagado
    public AlquilerDTO marcarComoPagado(Long id, RegistroPagoDTO registroPagoDTO) {
        // Verificar que existe el alquiler
        Optional<Alquiler> alquilerExistente = alquilerRepository.findById(id);
        if (!alquilerExistente.isPresent()) {
            throw new BusinessException(ErrorCodes.ALQUILER_NO_ENCONTRADO, "Alquiler no encontrado con ID: " + id, HttpStatus.NOT_FOUND);
        }

        Alquiler alquiler = alquilerExistente.get();

        // Marcar como pagado
        alquiler.setEstaPagado(true);

        // Actualizar información de pago
        if (registroPagoDTO.getCuentaBanco() != null) {
            alquiler.setCuentaBanco(registroPagoDTO.getCuentaBanco());
        }
        if (registroPagoDTO.getTitularDePago() != null) {
            alquiler.setTitularDePago(registroPagoDTO.getTitularDePago());
        }
        if (registroPagoDTO.getMetodo() != null) {
            alquiler.setMetodo(registroPagoDTO.getMetodo());
        }

        // Guardar cambios
        Alquiler alquilerActualizado = alquilerRepository.save(alquiler);

        return new AlquilerDTO(alquilerActualizado);
    }

    // Eliminar alquiler
    public void eliminarAlquiler(Long id) {
        Optional<Alquiler> alquiler = alquilerRepository.findById(id);
        if (!alquiler.isPresent()) {
            throw new BusinessException(ErrorCodes.ALQUILER_NO_ENCONTRADO, "Alquiler no encontrado con ID: " + id, HttpStatus.NOT_FOUND);
        }

        alquilerRepository.deleteById(id);
    }

    // Verificar si existe un alquiler
    public boolean existeAlquiler(Long id) {
        return alquilerRepository.existsById(id);
    }
}
