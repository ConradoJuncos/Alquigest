package com.alquileres.service;

import com.alquileres.dto.InquilinoDTO;
import com.alquileres.model.Inquilino;
import com.alquileres.repository.InquilinoRepository;
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
public class InquilinoService {

    @Autowired
    private InquilinoRepository inquilinoRepository;

    @Autowired
    private ContratoRepository contratoRepository;

    // Obtener todos los inquilinos
    public List<InquilinoDTO> obtenerTodosLosInquilinos() {
        List<Inquilino> inquilinos = inquilinoRepository.findAll();
        return inquilinos.stream()
                .map(InquilinoDTO::new)
                .collect(Collectors.toList());
    }

    // Obtener solo inquilinos activos
    public List<InquilinoDTO> obtenerInquilinosActivos() {
        List<Inquilino> inquilinos = inquilinoRepository.findByEsActivoTrue();
        return inquilinos.stream()
                .map(InquilinoDTO::new)
                .collect(Collectors.toList());
    }

    // Obtener solo inquilinos inactivos
    public List<InquilinoDTO> obtenerInquilinosInactivos() {
        List<Inquilino> inquilinos = inquilinoRepository.findByEsActivoFalse();
        return inquilinos.stream()
                .map(InquilinoDTO::new)
                .collect(Collectors.toList());
    }

    // Obtener inquilino por ID
    public InquilinoDTO obtenerInquilinoPorId(Long id) {
        Optional<Inquilino> inquilino = inquilinoRepository.findById(id);
        if (inquilino.isPresent()) {
            return new InquilinoDTO(inquilino.get());
        } else {
            throw new BusinessException(
                ErrorCodes.INQUILINO_NO_ENCONTRADO,
                "No se encontró el inquilino con ID: " + id,
                HttpStatus.NOT_FOUND
            );
        }
    }

    // Buscar inquilino por CUIL
    public InquilinoDTO buscarPorCuil(String cuil) {
        Optional<Inquilino> inquilino = inquilinoRepository.findByCuil(cuil);
        if (inquilino.isPresent()) {
            return new InquilinoDTO(inquilino.get());
        } else {
            throw new BusinessException(
                ErrorCodes.INQUILINO_NO_ENCONTRADO,
                "No se encontró el inquilino con CUIL: " + cuil,
                HttpStatus.NOT_FOUND
            );
        }
    }

    // Buscar inquilinos por nombre
    public List<InquilinoDTO> buscarPorNombre(String nombre) {
        List<Inquilino> inquilinos = inquilinoRepository.findByNombreContainingIgnoreCase(nombre);
        return inquilinos.stream()
                .map(InquilinoDTO::new)
                .collect(Collectors.toList());
    }

    // Buscar inquilinos por apellido
    public List<InquilinoDTO> buscarPorApellido(String apellido) {
        List<Inquilino> inquilinos = inquilinoRepository.findByApellidoContainingIgnoreCase(apellido);
        return inquilinos.stream()
                .map(InquilinoDTO::new)
                .collect(Collectors.toList());
    }

    // Buscar inquilinos por nombre y apellido
    public List<InquilinoDTO> buscarPorNombreYApellido(String nombre, String apellido) {
        List<Inquilino> inquilinos = inquilinoRepository
                .findByNombreContainingIgnoreCaseAndApellidoContainingIgnoreCase(
                        nombre != null ? nombre : "",
                        apellido != null ? apellido : ""
                );
        return inquilinos.stream()
                .map(InquilinoDTO::new)
                .collect(Collectors.toList());
    }

    // Buscar inquilinos por nombre O apellido (búsqueda general)
    public List<InquilinoDTO> buscarPorNombreOApellido(String termino) {
        List<Inquilino> inquilinos = inquilinoRepository
                .findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(termino, termino);
        return inquilinos.stream()
                .map(InquilinoDTO::new)
                .collect(Collectors.toList());
    }

    // Crear nuevo inquilino
    public InquilinoDTO crearInquilino(InquilinoDTO inquilinoDTO) {
        // Validar CUIL único si se proporciona
        if (inquilinoDTO.getCuil() != null && !inquilinoDTO.getCuil().trim().isEmpty()) {
            if (inquilinoRepository.findByCuil(inquilinoDTO.getCuil()).isPresent()) {
                throw new BusinessException(
                    ErrorCodes.CUIL_DUPLICADO,
                    "El CUIL ya se encuentra registrado"
                );
            }
        }

        Inquilino inquilino = inquilinoDTO.toEntity();
        Inquilino inquilinoGuardado = inquilinoRepository.save(inquilino);
        return new InquilinoDTO(inquilinoGuardado);
    }

    // Actualizar inquilino
    public InquilinoDTO actualizarInquilino(Long id, InquilinoDTO inquilinoDTO) {
        Optional<Inquilino> inquilinoExistente = inquilinoRepository.findById(id);

        if (!inquilinoExistente.isPresent()) {
            throw new BusinessException(
                ErrorCodes.INQUILINO_NO_ENCONTRADO,
                "No se encontró el inquilino con ID: " + id,
                HttpStatus.NOT_FOUND
            );
        }

        // Validar CUIL único si se proporciona (excluyendo el inquilino actual)
        if (inquilinoDTO.getCuil() != null && !inquilinoDTO.getCuil().trim().isEmpty()) {
            if (inquilinoRepository.existsByCuilAndIdNot(inquilinoDTO.getCuil(), id)) {
                throw new BusinessException(
                    ErrorCodes.CUIL_DUPLICADO,
                    "Ya existe otro inquilino con ese CUIL"
                );
            }
        }

        Inquilino inquilino = inquilinoExistente.get();
        inquilino.setNombre(inquilinoDTO.getNombre());
        inquilino.setApellido(inquilinoDTO.getApellido());
        inquilino.setCuil(inquilinoDTO.getCuil());
        inquilino.setTelefono(inquilinoDTO.getTelefono());
        inquilino.setEsActivo(inquilinoDTO.getEsActivo());

        Inquilino inquilinoActualizado = inquilinoRepository.save(inquilino);
        return new InquilinoDTO(inquilinoActualizado);
    }

    // Eliminar inquilino (borrado lógico)
    public void eliminarInquilino(Long id) {
        Optional<Inquilino> inquilino = inquilinoRepository.findById(id);
        if (!inquilino.isPresent()) {
            throw new BusinessException(
                ErrorCodes.INQUILINO_NO_ENCONTRADO,
                "No se encontró el inquilino con ID: " + id,
                HttpStatus.NOT_FOUND
            );
        }

        Inquilino i = inquilino.get();

        // Validar que el inquilino no tenga contratos vigentes
        boolean tieneContratosVigentes = contratoRepository.existsContratoVigenteByInquilino(i);
        if (tieneContratosVigentes) {
            throw new BusinessException(
                ErrorCodes.INQUILINO_TIENE_CONTRATOS_VIGENTES,
                "No se puede eliminar el inquilino porque tiene contratos vigentes asociados",
                HttpStatus.BAD_REQUEST
            );
        }

        // Proceder con la eliminación lógica
        i.setEsActivo(false);
        inquilinoRepository.save(i);
    }

    // Alias para desactivar inquilino (método usado por el controller)
    public void desactivarInquilino(Long id) {
        eliminarInquilino(id);
    }

    // Activar inquilino (reactivación)
    public void activarInquilino(Long id) {
        Optional<Inquilino> inquilino = inquilinoRepository.findById(id);
        if (!inquilino.isPresent()) {
            throw new BusinessException(
                ErrorCodes.INQUILINO_NO_ENCONTRADO,
                "No se encontró el inquilino con ID: " + id,
                HttpStatus.NOT_FOUND
            );
        }

        Inquilino i = inquilino.get();
        i.setEsActivo(true);
        inquilinoRepository.save(i);
    }
}
