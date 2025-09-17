package com.alquileres.service;

import com.alquileres.dto.InmuebleDTO;
import com.alquileres.model.Inmueble;
import com.alquileres.repository.InmuebleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InmuebleService {

    @Autowired
    private InmuebleRepository inmuebleRepository;

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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Inmueble no encontrado");
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

    // Buscar inmuebles por tipo
    public List<InmuebleDTO> buscarPorTipo(String tipo) {
        List<Inmueble> inmuebles = inmuebleRepository.findByTipoContainingIgnoreCase(tipo);
        return inmuebles.stream()
                .map(InmuebleDTO::new)
                .collect(Collectors.toList());
    }

    // Crear nuevo inmueble
    public InmuebleDTO crearInmueble(InmuebleDTO inmuebleDTO) {
        Inmueble inmueble = inmuebleDTO.toEntity();
        Inmueble inmuebleGuardado = inmuebleRepository.save(inmueble);
        return new InmuebleDTO(inmuebleGuardado);
    }

    // Actualizar inmueble
    public InmuebleDTO actualizarInmueble(Long id, InmuebleDTO inmuebleDTO) {
        Optional<Inmueble> inmuebleExistente = inmuebleRepository.findById(id);

        if (!inmuebleExistente.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Inmueble no encontrado");
        }

        Inmueble inmueble = inmuebleExistente.get();

        // Actualizar campos
        inmueble.setPropietarioId(inmuebleDTO.getPropietarioId());
        inmueble.setDireccion(inmuebleDTO.getDireccion());
        inmueble.setTipoInmuebleId(inmuebleDTO.getTipoInmuebleId());
        inmueble.setTipo(inmuebleDTO.getTipo());
        inmueble.setEstado(inmuebleDTO.getEstado());
        inmueble.setSuperficie(inmuebleDTO.getSuperficie());

        if (inmuebleDTO.getEsAlquilado() != null) {
            inmueble.setEsAlquilado(inmuebleDTO.getEsAlquilado());
        }

        if (inmuebleDTO.getEsActivo() != null) {
            inmueble.setEsActivo(inmuebleDTO.getEsActivo());
        }

        Inmueble inmuebleActualizado = inmuebleRepository.save(inmueble);
        return new InmuebleDTO(inmuebleActualizado);
    }

    // Cambiar estado de alquiler
    public InmuebleDTO cambiarEstadoAlquiler(Long id, Boolean esAlquilado) {
        Optional<Inmueble> inmuebleExistente = inmuebleRepository.findById(id);

        if (!inmuebleExistente.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Inmueble no encontrado");
        }

        Inmueble inmueble = inmuebleExistente.get();
        inmueble.setEsAlquilado(esAlquilado);

        Inmueble inmuebleActualizado = inmuebleRepository.save(inmueble);
        return new InmuebleDTO(inmuebleActualizado);
    }

    // Desactivar inmueble (eliminación lógica)
    public void desactivarInmueble(Long id) {
        Optional<Inmueble> inmueble = inmuebleRepository.findById(id);

        if (!inmueble.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Inmueble no encontrado");
        }

        Inmueble inm = inmueble.get();
        inm.setEsActivo(false);
        inmuebleRepository.save(inm);
    }

    // Eliminar inmueble físicamente
    public void eliminarInmueble(Long id) {
        Optional<Inmueble> inmueble = inmuebleRepository.findById(id);

        if (!inmueble.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Inmueble no encontrado");
        }

        inmuebleRepository.deleteById(id);
    }
}
