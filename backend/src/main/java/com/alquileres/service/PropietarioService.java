package com.alquileres.service;

import com.alquileres.dto.PropietarioDTO;
import com.alquileres.model.Propietario;
import com.alquileres.repository.PropietarioRepository;
import com.alquileres.repository.InmuebleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.alquileres.exception.BusinessException;
import com.alquileres.exception.ErrorCodes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PropietarioService {

    @Autowired
    private PropietarioRepository propietarioRepository;

    @Autowired
    private InmuebleRepository inmuebleRepository;

    // Obtener todos los propietarios
    public List<PropietarioDTO> obtenerTodosLosPropietarios() {
        List<Propietario> propietarios = propietarioRepository.findAll();
        return propietarios.stream()
                .map(PropietarioDTO::new)
                .collect(Collectors.toList());
    }

    // Obtener solo propietarios activos
    public List<PropietarioDTO> obtenerPropietariosActivos() {
        List<Propietario> propietarios = propietarioRepository.findByEsActivoTrue();
        return propietarios.stream()
                .map(PropietarioDTO::new)
                .collect(Collectors.toList());
    }

    // Obtener propietario por ID
    public PropietarioDTO obtenerPropietarioPorId(Long id) {
        Optional<Propietario> propietario = propietarioRepository.findById(id);
        if (propietario.isPresent()) {
            return new PropietarioDTO(propietario.get());
        } else {
            throw new BusinessException(
                ErrorCodes.PROPIETARIO_NO_ENCONTRADO,
                "No se encontró el propietario con ID: " + id,
                HttpStatus.NOT_FOUND
            );
        }
    }

    // Buscar propietario por DNI
    public PropietarioDTO buscarPorDni(String dni) {
        Optional<Propietario> propietario = propietarioRepository.findByDni(dni);
        if (propietario.isPresent()) {
            return new PropietarioDTO(propietario.get());
        } else {
            throw new BusinessException(
                ErrorCodes.PROPIETARIO_NO_ENCONTRADO,
                "No se encontró el propietario con DNI: " + dni,
                HttpStatus.NOT_FOUND
            );
        }
    }

    // Buscar propietarios por nombre y apellido
    public List<PropietarioDTO> buscarPorNombreYApellido(String nombre, String apellido) {
        List<Propietario> propietarios = propietarioRepository
                .findByNombreContainingIgnoreCaseAndApellidoContainingIgnoreCase(
                        nombre != null ? nombre : "",
                        apellido != null ? apellido : ""
                );
        return propietarios.stream()
                .map(PropietarioDTO::new)
                .collect(Collectors.toList());
    }

    // Crear nuevo propietario
    public PropietarioDTO crearPropietario(PropietarioDTO propietarioDTO) {
        // Validar DNI único
        if (propietarioRepository.findByDni(propietarioDTO.getDni()).isPresent()) {
            throw new BusinessException(
                ErrorCodes.DNI_DUPLICADO,
                "El DNI ya se encuentra registrado"
            );
        }

        // Validar email único si se proporciona
        if (propietarioDTO.getEmail() != null && !propietarioDTO.getEmail().trim().isEmpty()) {
            if (propietarioRepository.findByEmail(propietarioDTO.getEmail()).isPresent()) {
                throw new BusinessException(
                    ErrorCodes.EMAIL_DUPLICADO,
                    "El email ya se encuentra registrado"
                );
            }
        }

        Propietario propietario = propietarioDTO.toEntity();
        Propietario propietarioGuardado = propietarioRepository.save(propietario);
        return new PropietarioDTO(propietarioGuardado);
    }

    // Actualizar propietario
    public PropietarioDTO actualizarPropietario(Long id, PropietarioDTO propietarioDTO) {
        Optional<Propietario> propietarioExistente = propietarioRepository.findById(id);

        if (!propietarioExistente.isPresent()) {
            throw new BusinessException(
                ErrorCodes.PROPIETARIO_NO_ENCONTRADO,
                "No se encontró el propietario con ID: " + id,
                HttpStatus.NOT_FOUND
            );
        }

        // Validar DNI único (excluyendo el propietario actual)
        if (propietarioRepository.existsByDniAndIdNot(propietarioDTO.getDni(), id)) {
            throw new BusinessException(
                ErrorCodes.DNI_DUPLICADO,
                "Ya existe otro propietario con ese DNI"
            );
        }

        // Validar email único si se proporciona (excluyendo el propietario actual)
        if (propietarioDTO.getEmail() != null && !propietarioDTO.getEmail().trim().isEmpty()) {
            if (propietarioRepository.existsByEmailAndIdNot(propietarioDTO.getEmail(), id)) {
                throw new BusinessException(
                    ErrorCodes.EMAIL_DUPLICADO,
                    "Ya existe otro propietario con ese email"
                );
            }
        }

        Propietario propietario = propietarioExistente.get();

        // Actualizar campos
        propietario.setNombre(propietarioDTO.getNombre());
        propietario.setApellido(propietarioDTO.getApellido());
        propietario.setDni(propietarioDTO.getDni());
        propietario.setTelefono(propietarioDTO.getTelefono());
        propietario.setEmail(propietarioDTO.getEmail());
        propietario.setDireccion(propietarioDTO.getDireccion());

        if (propietarioDTO.getEsActivo() != null) {
            propietario.setEsActivo(propietarioDTO.getEsActivo());
        }

        Propietario propietarioActualizado = propietarioRepository.save(propietario);
        return new PropietarioDTO(propietarioActualizado);
    }

    // Eliminar propietario (eliminación lógica)
    @Transactional
    public void desactivarPropietario(Long id) {
        Optional<Propietario> propietario = propietarioRepository.findById(id);

        if (!propietario.isPresent()) {
            throw new BusinessException(
                ErrorCodes.PROPIETARIO_NO_ENCONTRADO,
                "No se encontró el propietario con ID: " + id,
                HttpStatus.NOT_FOUND
            );
        }

        Propietario prop = propietario.get();
        prop.setEsActivo(false);
        propietarioRepository.save(prop);

        // Desactivar todos los inmuebles relacionados
        inmuebleRepository.desactivarInmueblesPorPropietario(id);
    }

    // Eliminar propietario físicamente
    public void eliminarPropietario(Long id) {
        Optional<Propietario> propietario = propietarioRepository.findById(id);

        if (!propietario.isPresent()) {
            throw new BusinessException(
                ErrorCodes.PROPIETARIO_NO_ENCONTRADO,
                "No se encontró el propietario con ID: " + id,
                HttpStatus.NOT_FOUND
            );
        }

        propietarioRepository.deleteById(id);
    }
}
