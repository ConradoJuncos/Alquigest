package com.alquileres.service;

import com.alquileres.dto.InmuebleDTO;
import com.alquileres.model.Inmueble;
import com.alquileres.repository.InmuebleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InmuebleService {

    @Autowired
    private InmuebleRepository inmuebleRepository;

    // Obtener todos los inmuebles
    public List<InmuebleDTO> obtenerTodosLosInmuebles() {
        return inmuebleRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Obtener inmuebles activos
    public List<InmuebleDTO> obtenerInmueblesActivos() {
        return inmuebleRepository.findByEsActivoTrue()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Obtener inmuebles disponibles (activos y no alquilados)
    public List<InmuebleDTO> obtenerInmueblesDisponibles() {
        return inmuebleRepository.findByEsActivoTrueAndEsAlquiladoFalse()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Obtener inmueble por ID
    public Optional<InmuebleDTO> obtenerInmueblePorId(Long id) {
        return inmuebleRepository.findById(id)
                .map(this::convertirADTO);
    }

    // Crear nuevo inmueble
    public InmuebleDTO crearInmueble(InmuebleDTO inmuebleDTO) {
        Inmueble inmueble = convertirAEntidad(inmuebleDTO);
        Inmueble inmuebleGuardado = inmuebleRepository.save(inmueble);
        return convertirADTO(inmuebleGuardado);
    }

    // Actualizar inmueble existente
    public Optional<InmuebleDTO> actualizarInmueble(Long id, InmuebleDTO inmuebleDTO) {
        return inmuebleRepository.findById(id)
                .map(inmuebleExistente -> {
                    inmuebleExistente.setPropietarioId(inmuebleDTO.getPropietarioId());
                    inmuebleExistente.setDireccion(inmuebleDTO.getDireccion());
                    inmuebleExistente.setTipoInmuebleId(inmuebleDTO.getTipoInmuebleId());
                    inmuebleExistente.setTipo(inmuebleDTO.getTipo());
                    inmuebleExistente.setEstado(inmuebleDTO.getEstado());
                    inmuebleExistente.setSuperficie(inmuebleDTO.getSuperficie());
                    if (inmuebleDTO.getEsAlquilado() != null) {
                        inmuebleExistente.setEsAlquilado(inmuebleDTO.getEsAlquilado());
                    }
                    if (inmuebleDTO.getEsActivo() != null) {
                        inmuebleExistente.setEsActivo(inmuebleDTO.getEsActivo());
                    }
                    return convertirADTO(inmuebleRepository.save(inmuebleExistente));
                });
    }

    // Eliminar inmueble
    public boolean eliminarInmueble(Long id) {
        if (inmuebleRepository.existsById(id)) {
            inmuebleRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Buscar inmuebles por propietario
    public List<InmuebleDTO> buscarInmueblesPorPropietario(Long propietarioId) {
        return inmuebleRepository.findByPropietarioId(propietarioId)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Buscar inmuebles por estado
    public List<InmuebleDTO> buscarInmueblesPorEstado(Integer estado) {
        return inmuebleRepository.findByEstado(estado)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Buscar inmuebles por tipo
    public List<InmuebleDTO> buscarInmueblesPorTipo(String tipo) {
        return inmuebleRepository.findByTipo(tipo)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Buscar inmuebles por texto
    public List<InmuebleDTO> buscarInmueblesPorTexto(String texto) {
        return inmuebleRepository.buscarPorTexto(texto)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Buscar inmuebles por rango de superficie
    public List<InmuebleDTO> buscarInmueblesPorSuperficie(BigDecimal superficieMin, BigDecimal superficieMax) {
        return inmuebleRepository.findBySuperficieBetween(superficieMin, superficieMax)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Cambiar estado de alquiler
    public Optional<InmuebleDTO> cambiarEstadoAlquiler(Long id, boolean esAlquilado) {
        return inmuebleRepository.findById(id)
                .map(inmueble -> {
                    inmueble.setEsAlquilado(esAlquilado);
                    return convertirADTO(inmuebleRepository.save(inmueble));
                });
    }

    // Activar/desactivar inmueble
    public Optional<InmuebleDTO> cambiarEstadoActivo(Long id, boolean esActivo) {
        return inmuebleRepository.findById(id)
                .map(inmueble -> {
                    inmueble.setEsActivo(esActivo);
                    return convertirADTO(inmuebleRepository.save(inmueble));
                });
    }

    // Método para convertir entidad a DTO
    private InmuebleDTO convertirADTO(Inmueble inmueble) {
        return new InmuebleDTO(
                inmueble.getId(),
                inmueble.getPropietarioId(),
                inmueble.getDireccion(),
                inmueble.getTipoInmuebleId(),
                inmueble.getTipo(),
                inmueble.getEstado(),
                inmueble.getSuperficie(),
                inmueble.getEsAlquilado(),
                inmueble.getEsActivo(),
                inmueble.getCreatedAt(),
                inmueble.getUpdatedAt()
        );
    }

    // Método para convertir DTO a entidad
    private Inmueble convertirAEntidad(InmuebleDTO dto) {
        Inmueble inmueble = new Inmueble();
        inmueble.setPropietarioId(dto.getPropietarioId());
        inmueble.setDireccion(dto.getDireccion());
        inmueble.setTipoInmuebleId(dto.getTipoInmuebleId());
        inmueble.setTipo(dto.getTipo());
        inmueble.setEstado(dto.getEstado());
        inmueble.setSuperficie(dto.getSuperficie());
        if (dto.getEsAlquilado() != null) {
            inmueble.setEsAlquilado(dto.getEsAlquilado());
        }
        if (dto.getEsActivo() != null) {
            inmueble.setEsActivo(dto.getEsActivo());
        }
        return inmueble;
    }
}
