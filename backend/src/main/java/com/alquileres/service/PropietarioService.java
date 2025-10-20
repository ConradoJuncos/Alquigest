package com.alquileres.service;

import com.alquileres.dto.PropietarioDTO;
import com.alquileres.model.Propietario;
import com.alquileres.repository.PropietarioRepository;
import com.alquileres.repository.InmuebleRepository;
import com.alquileres.repository.ContratoRepository;
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

    @Autowired
    private ContratoRepository contratoRepository;

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

    // Obtener solo los propietarios inactivos
    public List<PropietarioDTO> obtenerPropietariosInactivos() {
        List<Propietario> propietarios = propietarioRepository.findByEsActivoFalse();
        return propietarios.stream()
                .map(PropietarioDTO::new)
                .collect(Collectors.toList());
    }

    // Contar propietarios activos
    public Long contarPropietariosActivos() {
        return propietarioRepository.countByEsActivoTrue();
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

    // Buscar propietario por CUIL
    public PropietarioDTO buscarPorCuil(String cuil) {
        Optional<Propietario> propietario = propietarioRepository.findByCuil(cuil);
        if (propietario.isPresent()) {
            return new PropietarioDTO(propietario.get());
        } else {
            throw new BusinessException(
                ErrorCodes.PROPIETARIO_NO_ENCONTRADO,
                "No se encontró el propietario con CUIL: " + cuil,
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
        // Validar CUIL único
        if (propietarioRepository.findByCuil(propietarioDTO.getCuil()).isPresent()) {
            throw new BusinessException(
                ErrorCodes.DNI_DUPLICADO,
                "El CUIL ya se encuentra registrado"
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

        // Validar CUIL único (excluyendo el propietario actual)
        if (propietarioRepository.existsByCuilAndIdNot(propietarioDTO.getCuil(), id)) {
            throw new BusinessException(
                ErrorCodes.DNI_DUPLICADO,
                "Ya existe otro propietario con ese CUIL"
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
        propietario.setCuil(propietarioDTO.getCuil());
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

        // Validar que el propietario no tenga contratos vigentes en sus inmuebles
        boolean tieneContratosVigentes = contratoRepository.existsContratoVigenteByPropietario(id);
        if (tieneContratosVigentes) {
            throw new BusinessException(
                ErrorCodes.PROPIETARIO_TIENE_CONTRATOS_VIGENTES,
                "No se puede dar de baja al propietario porque tiene inmuebles con contratos vigentes asociados",
                HttpStatus.BAD_REQUEST
            );
        }

        Propietario prop = propietario.get();
        prop.setEsActivo(false);
        propietarioRepository.save(prop);

        // Desactivar todos los inmuebles relacionados
        inmuebleRepository.desactivarInmueblesPorPropietario(id);
    }

    // Activar propietario (reactivación)
    public void activarPropietario(Long id) {
        Optional<Propietario> propietario = propietarioRepository.findById(id);

        if (!propietario.isPresent()) {
            throw new BusinessException(
                ErrorCodes.PROPIETARIO_NO_ENCONTRADO,
                "No se encontró el propietario con ID: " + id,
                HttpStatus.NOT_FOUND
            );
        }

        Propietario prop = propietario.get();
        prop.setEsActivo(true);
        propietarioRepository.save(prop);
    }
}
