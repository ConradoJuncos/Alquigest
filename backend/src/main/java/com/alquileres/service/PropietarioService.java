package com.alquileres.service;

import com.alquileres.dto.PropietarioDTO;
import com.alquileres.model.Propietario;
import com.alquileres.repository.PropietarioRepository;
import com.alquileres.repository.InmuebleRepository;
import com.alquileres.repository.ContratoRepository;
import com.alquileres.security.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.alquileres.exception.BusinessException;
import com.alquileres.exception.ErrorCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PropietarioService {

    private static final Logger logger = LoggerFactory.getLogger(PropietarioService.class);

    @Autowired
    private PropietarioRepository propietarioRepository;

    @Autowired
    private InmuebleRepository inmuebleRepository;

    @Autowired
    private ContratoRepository contratoRepository;

    @Autowired
    private EncryptionService encryptionService;

    // Obtener todos los propietarios
    public List<PropietarioDTO> obtenerTodosLosPropietarios() {
        List<Propietario> propietarios = propietarioRepository.findAll();
        return propietarios.stream()
                .map(p -> {
                    PropietarioDTO dto = new PropietarioDTO(p);
                    desencriptarClaveFiscal(dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Obtener solo propietarios activos
    public List<PropietarioDTO> obtenerPropietariosActivos() {
        List<Propietario> propietarios = propietarioRepository.findByEsActivoTrue();
        return propietarios.stream()
                .map(p -> {
                    PropietarioDTO dto = new PropietarioDTO(p);
                    desencriptarClaveFiscal(dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Obtener solo los propietarios inactivos
    public List<PropietarioDTO> obtenerPropietariosInactivos() {
        List<Propietario> propietarios = propietarioRepository.findByEsActivoFalse();
        return propietarios.stream()
                .map(p -> {
                    PropietarioDTO dto = new PropietarioDTO(p);
                    desencriptarClaveFiscal(dto);
                    return dto;
                })
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
            PropietarioDTO dto = new PropietarioDTO(propietario.get());
            // Desencriptar clave fiscal
            desencriptarClaveFiscal(dto);
            return dto;
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
            PropietarioDTO dto = new PropietarioDTO(propietario.get());
            desencriptarClaveFiscal(dto);
            return dto;
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
                .map(p -> {
                    PropietarioDTO dto = new PropietarioDTO(p);
                    desencriptarClaveFiscal(dto);
                    return dto;
                })
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

        // Encriptar clave fiscal si se proporciona
        if (propietario.getClaveFiscal() != null && !propietario.getClaveFiscal().trim().isEmpty()) {
            try {
                propietario.setClaveFiscal(encryptionService.encriptar(propietario.getClaveFiscal()));
            } catch (Exception e) {
                logger.error("Error encriptando clave fiscal", e);
                throw new BusinessException(
                    ErrorCodes.ERROR_INTERNO,
                    "Error al procesar la clave fiscal"
                );
            }
        }

        Propietario propietarioGuardado = propietarioRepository.save(propietario);

        // Desencriptar clave fiscal para devolver en DTO
        PropietarioDTO dto = new PropietarioDTO(propietarioGuardado);
        if (dto.getClaveFiscal() != null && !dto.getClaveFiscal().trim().isEmpty()) {
            try {
                dto.setClaveFiscal(encryptionService.desencriptar(dto.getClaveFiscal()));
            } catch (Exception e) {
                logger.error("Error desencriptando clave fiscal", e);
                dto.setClaveFiscal(null);
            }
        }

        return dto;
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
        propietario.setBarrio(propietarioDTO.getBarrio());

        // Encriptar clave fiscal si se proporciona
        if (propietarioDTO.getClaveFiscal() != null && !propietarioDTO.getClaveFiscal().trim().isEmpty()) {
            try {
                propietario.setClaveFiscal(encryptionService.encriptar(propietarioDTO.getClaveFiscal()));
            } catch (Exception e) {
                logger.error("Error encriptando clave fiscal", e);
                throw new BusinessException(
                    ErrorCodes.ERROR_INTERNO,
                    "Error al procesar la clave fiscal"
                );
            }
        } else {
            propietario.setClaveFiscal(null);
        }

        if (propietarioDTO.getEsActivo() != null) {
            propietario.setEsActivo(propietarioDTO.getEsActivo());
        }

        Propietario propietarioActualizado = propietarioRepository.save(propietario);
        PropietarioDTO dto = new PropietarioDTO(propietarioActualizado);
        desencriptarClaveFiscal(dto);
        return dto;
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

    /**
     * Método auxiliar para desencriptar la clave fiscal en un DTO
     * Si ocurre un error, registra el error pero no lanza excepción
     *
     * @param dto El DTO de propietario cuya clave fiscal será desencriptada
     */
    private void desencriptarClaveFiscal(PropietarioDTO dto) {
        if (dto != null && dto.getClaveFiscal() != null && !dto.getClaveFiscal().trim().isEmpty()) {
            try {
                dto.setClaveFiscal(encryptionService.desencriptar(dto.getClaveFiscal()));
            } catch (Exception e) {
                logger.error("Error desencriptando clave fiscal para propietario ID: {}", dto.getId(), e);
                dto.setClaveFiscal(null);
            }
        }
    }
}
