package com.alquileres.service;

import com.alquileres.dto.InmuebleDTO;
import com.alquileres.model.Inmueble;
import com.alquileres.model.EstadoInmueble;
import com.alquileres.model.TipoInmueble;
import com.alquileres.repository.InmuebleRepository;
import com.alquileres.repository.PropietarioRepository;
import com.alquileres.repository.EstadoInmuebleRepository;
import com.alquileres.repository.TipoInmuebleRepository;
import com.alquileres.repository.ContratoRepository;
import com.alquileres.exception.BusinessException;
import com.alquileres.exception.ErrorCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InmuebleService {

    @Autowired
    private InmuebleRepository inmuebleRepository;

    @Autowired
    private PropietarioRepository propietarioRepository;

    @Autowired
    private EstadoInmuebleRepository estadoInmuebleRepository;

    @Autowired
    private TipoInmuebleRepository tipoInmuebleRepository;

    @Autowired
    private ContratoRepository contratoRepository;

    // Obtener todos los inmuebles
    public List<InmuebleDTO> obtenerTodosLosInmuebles() {
        List<Inmueble> inmuebles = inmuebleRepository.findAll();
        return inmuebles.stream()
                .map(InmuebleDTO::new)
                .collect(Collectors.toList());
    }

    // Obtener solo inmuebles activos
    public List<InmuebleDTO> obtenerInmueblesActivos() {
        List<Inmueble> inmuebles = inmuebleRepository.findByEsActivoTrue();
        return inmuebles.stream()
                .map(InmuebleDTO::new)
                .collect(Collectors.toList());
    }

    // Obtener solo inmuebles inactivos
    public List<InmuebleDTO> obtenerInmueblesInactivos() {
        List<Inmueble> inmuebles = inmuebleRepository.findByEsActivoFalse();
        return inmuebles.stream()
                .map(InmuebleDTO::new)
                .collect(Collectors.toList());
    }

    // Obtener inmuebles disponibles (no alquilados)
    public List<InmuebleDTO> obtenerInmueblesDisponibles() {
        List<Inmueble> inmuebles = inmuebleRepository.findByEsAlquiladoFalseAndEsActivoTrue();
        return inmuebles.stream()
                .map(InmuebleDTO::new)
                .collect(Collectors.toList());
    }

    // Obtener inmueble por ID
    public InmuebleDTO obtenerInmueblePorId(Long id) {
        Optional<Inmueble> inmueble = inmuebleRepository.findById(id);
        if (inmueble.isPresent()) {
            return new InmuebleDTO(inmueble.get());
        } else {
            throw new BusinessException(
                ErrorCodes.INMUEBLE_NO_ENCONTRADO,
                "No se encontró el inmueble con ID: " + id,
                HttpStatus.NOT_FOUND
            );
        }
    }

    // Buscar inmuebles por propietario
    public List<InmuebleDTO> buscarPorPropietario(Long propietarioId) {
        List<Inmueble> inmuebles = inmuebleRepository.findByPropietarioId(propietarioId);
        return inmuebles.stream()
                .map(InmuebleDTO::new)
                .collect(Collectors.toList());
    }

    // Buscar inmuebles por dirección
    public List<InmuebleDTO> buscarPorDireccion(String direccion) {
        List<Inmueble> inmuebles = inmuebleRepository.findByDireccionContainingIgnoreCase(direccion);
        return inmuebles.stream()
                .map(InmuebleDTO::new)
                .collect(Collectors.toList());
    }

    // Crear nuevo inmueble
    public InmuebleDTO crearInmueble(InmuebleDTO inmuebleDTO) {
        // Validar que el propietario exista
        if (inmuebleDTO.getPropietarioId() != null) {
            if (!propietarioRepository.existsById(inmuebleDTO.getPropietarioId())) {
                throw new BusinessException(
                    ErrorCodes.PROPIETARIO_NO_ENCONTRADO,
                    "No existe un propietario con ID: " + inmuebleDTO.getPropietarioId(),
                    HttpStatus.BAD_REQUEST
                );
            }
        }

        Inmueble inmueble = inmuebleDTO.toEntity();

        // Actualizar esAlquilado basado en el estado
        actualizarEsAlquiladoSegunEstado(inmueble);

        Inmueble inmuebleGuardado = inmuebleRepository.save(inmueble);
        return new InmuebleDTO(inmuebleGuardado);
    }

    // Actualizar inmueble
    public InmuebleDTO actualizarInmueble(Long id, InmuebleDTO inmuebleDTO) {
        Optional<Inmueble> inmuebleExistente = inmuebleRepository.findById(id);

        if (!inmuebleExistente.isPresent()) {
            throw new BusinessException(
                ErrorCodes.INMUEBLE_NO_ENCONTRADO,
                "No se encontró el inmueble con ID: " + id,
                HttpStatus.NOT_FOUND
            );
        }

        Inmueble inmueble = inmuebleExistente.get();
        inmueble.setPropietarioId(inmuebleDTO.getPropietarioId());
        inmueble.setDireccion(inmuebleDTO.getDireccion());
        inmueble.setTipoInmuebleId(inmuebleDTO.getTipoInmuebleId());
        inmueble.setEstado(inmuebleDTO.getEstado());
        inmueble.setSuperficie(inmuebleDTO.getSuperficie());
        inmueble.setEsActivo(inmuebleDTO.getEsActivo());

        // Actualizar esAlquilado basado en el estado
        actualizarEsAlquiladoSegunEstado(inmueble);

        Inmueble inmuebleActualizado = inmuebleRepository.save(inmueble);
        return new InmuebleDTO(inmuebleActualizado);
    }

    // Método auxiliar para actualizar esAlquilado según el estado
    private void actualizarEsAlquiladoSegunEstado(Inmueble inmueble) {
        if (inmueble.getEstado() != null) {
            Optional<EstadoInmueble> estadoInmueble = estadoInmuebleRepository.findById(inmueble.getEstado());
            if (estadoInmueble.isPresent()) {
                boolean esAlquilado = "Alquilado".equals(estadoInmueble.get().getNombre());
                inmueble.setEsAlquilado(esAlquilado);
            }
        }
    }

    // Eliminar inmueble (borrado lógico)
    public void eliminarInmueble(Long id) {
        Optional<Inmueble> inmueble = inmuebleRepository.findById(id);
        if (!inmueble.isPresent()) {
            throw new BusinessException(
                ErrorCodes.INMUEBLE_NO_ENCONTRADO,
                "No se encontró el inmueble con ID: " + id,
                HttpStatus.NOT_FOUND
            );
        }

        Inmueble i = inmueble.get();

        // Validar que el inmueble no tenga contratos vigentes
        boolean tieneContratosVigentes = contratoRepository.existsContratoVigenteByInmueble(i);
        if (tieneContratosVigentes) {
            throw new BusinessException(
                ErrorCodes.INMUEBLE_TIENE_CONTRATOS_VIGENTES,
                "No se puede eliminar el inmueble porque tiene contratos vigentes asociados",
                HttpStatus.BAD_REQUEST
            );
        }

        // Proceder con la eliminación lógica
        i.setEsActivo(false);
        inmuebleRepository.save(i);
    }

    // Marcar inmueble como alquilado
    public InmuebleDTO marcarComoAlquilado(Long id) {
        Optional<Inmueble> inmueble = inmuebleRepository.findById(id);
        if (inmueble.isPresent()) {
            Inmueble i = inmueble.get();
            if (i.getEsAlquilado()) {
                throw new BusinessException(
                    ErrorCodes.INMUEBLE_YA_ALQUILADO,
                    "El inmueble ya se encuentra alquilado"
                );
            }
            i.setEsAlquilado(true);
            Inmueble inmuebleActualizado = inmuebleRepository.save(i);
            return new InmuebleDTO(inmuebleActualizado);
        } else {
            throw new BusinessException(
                ErrorCodes.INMUEBLE_NO_ENCONTRADO,
                "No se encontró el inmueble con ID: " + id,
                HttpStatus.NOT_FOUND
            );
        }
    }

    // Marcar inmueble como disponible
    public InmuebleDTO marcarComoDisponible(Long id) {
        Optional<Inmueble> inmueble = inmuebleRepository.findById(id);
        if (inmueble.isPresent()) {
            Inmueble i = inmueble.get();
            i.setEsAlquilado(false);
            Inmueble inmuebleActualizado = inmuebleRepository.save(i);
            return new InmuebleDTO(inmuebleActualizado);
        } else {
            throw new BusinessException(
                ErrorCodes.INMUEBLE_NO_ENCONTRADO,
                "No se encontró el inmueble con ID: " + id,
                HttpStatus.NOT_FOUND
            );
        }
    }

    // Cambiar estado de alquiler (método usado por el controller)
    public InmuebleDTO cambiarEstadoAlquiler(Long id, Boolean esAlquilado) {
        if (esAlquilado) {
            return marcarComoAlquilado(id);
        } else {
            return marcarComoDisponible(id);
        }
    }

    // Cambiar tipo de inmueble
    public InmuebleDTO cambiarTipoInmueble(Long id, Long tipoInmuebleId) {
        // Verificar que existe el inmueble
        Optional<Inmueble> inmuebleExistente = inmuebleRepository.findById(id);
        if (!inmuebleExistente.isPresent()) {
            throw new BusinessException(ErrorCodes.INMUEBLE_NO_ENCONTRADO, "Inmueble no encontrado con ID: " + id, HttpStatus.NOT_FOUND);
        }

        Inmueble inmueble = inmuebleExistente.get();

        // Verificar que el inmueble no esté en un contrato vigente usando el repositorio de contratos
        boolean tieneContratoVigente = contratoRepository.existsContratoVigenteByInmueble(inmueble);
        if (tieneContratoVigente) {
            throw new BusinessException(ErrorCodes.INMUEBLE_YA_ALQUILADO,
                "No se puede cambiar el tipo del inmueble porque tiene un contrato vigente asociado", HttpStatus.BAD_REQUEST);
        }

        // Validar que existe el tipo de inmueble
        Optional<TipoInmueble> tipoInmueble = tipoInmuebleRepository.findById(tipoInmuebleId);
        if (!tipoInmueble.isPresent()) {
            throw new BusinessException(ErrorCodes.TIPO_INMUEBLE_NO_ENCONTRADO,
                "No existe el tipo de inmueble con ID: " + tipoInmuebleId, HttpStatus.BAD_REQUEST);
        }

        // Actualizar el tipo de inmueble
        inmueble.setTipoInmuebleId(tipoInmuebleId);

        Inmueble inmuebleActualizado = inmuebleRepository.save(inmueble);
        return new InmuebleDTO(inmuebleActualizado);
    }

    // Alias para desactivar inmueble (método usado por el controller)
    public void desactivarInmueble(Long id) {
        eliminarInmueble(id);
    }
}
