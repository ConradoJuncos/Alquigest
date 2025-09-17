package com.alquileres.service;

import com.alquileres.dto.PropiedadDTO;
import com.alquileres.model.Propiedad;
import com.alquileres.repository.PropiedadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PropiedadService {

    @Autowired
    private PropiedadRepository propiedadRepository;

    // Obtener todas las propiedades
    public List<PropiedadDTO> obtenerTodasLasPropiedades() {
        return propiedadRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Obtener propiedades disponibles
    public List<PropiedadDTO> obtenerPropiedadesDisponibles() {
        return propiedadRepository.findByDisponibleTrue()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Obtener propiedad por ID
    public Optional<PropiedadDTO> obtenerPropiedadPorId(Long id) {
        return propiedadRepository.findById(id)
                .map(this::convertirADTO);
    }

    // Crear nueva propiedad
    public PropiedadDTO crearPropiedad(PropiedadDTO propiedadDTO) {
        Propiedad propiedad = convertirAEntidad(propiedadDTO);
        Propiedad propiedadGuardada = propiedadRepository.save(propiedad);
        return convertirADTO(propiedadGuardada);
    }

    // Actualizar propiedad existente
    public Optional<PropiedadDTO> actualizarPropiedad(Long id, PropiedadDTO propiedadDTO) {
        return propiedadRepository.findById(id)
                .map(propiedadExistente -> {
                    propiedadExistente.setTitulo(propiedadDTO.getTitulo());
                    propiedadExistente.setDescripcion(propiedadDTO.getDescripcion());
                    propiedadExistente.setDireccion(propiedadDTO.getDireccion());
                    propiedadExistente.setPrecio(propiedadDTO.getPrecio());
                    if (propiedadDTO.getDisponible() != null) {
                        propiedadExistente.setDisponible(propiedadDTO.getDisponible());
                    }
                    return convertirADTO(propiedadRepository.save(propiedadExistente));
                });
    }

    // Eliminar propiedad
    public boolean eliminarPropiedad(Long id) {
        if (propiedadRepository.existsById(id)) {
            propiedadRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Buscar propiedades por texto
    public List<PropiedadDTO> buscarPropiedadesPorTexto(String texto) {
        return propiedadRepository.buscarPorTexto(texto)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Buscar propiedades por rango de precio
    public List<PropiedadDTO> buscarPropiedadesPorRangoPrecio(BigDecimal precioMin, BigDecimal precioMax) {
        return propiedadRepository.findByPrecioBetween(precioMin, precioMax)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Cambiar disponibilidad de propiedad
    public Optional<PropiedadDTO> cambiarDisponibilidad(Long id, boolean disponible) {
        return propiedadRepository.findById(id)
                .map(propiedad -> {
                    propiedad.setDisponible(disponible);
                    return convertirADTO(propiedadRepository.save(propiedad));
                });
    }

    // Método para convertir entidad a DTO
    private PropiedadDTO convertirADTO(Propiedad propiedad) {
        return new PropiedadDTO(
                propiedad.getId(),
                propiedad.getTitulo(),
                propiedad.getDescripcion(),
                propiedad.getDireccion(),
                propiedad.getPrecio(),
                propiedad.getFechaCreacion(),
                propiedad.getFechaActualizacion(),
                propiedad.getDisponible()
        );
    }

    // Método para convertir DTO a entidad
    private Propiedad convertirAEntidad(PropiedadDTO dto) {
        Propiedad propiedad = new Propiedad();
        propiedad.setTitulo(dto.getTitulo());
        propiedad.setDescripcion(dto.getDescripcion());
        propiedad.setDireccion(dto.getDireccion());
        propiedad.setPrecio(dto.getPrecio());
        if (dto.getDisponible() != null) {
            propiedad.setDisponible(dto.getDisponible());
        }
        return propiedad;
    }
}
