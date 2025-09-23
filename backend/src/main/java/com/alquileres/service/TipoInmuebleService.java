package com.alquileres.service;

import com.alquileres.dto.TipoInmuebleDTO;
import com.alquileres.model.TipoInmueble;
import com.alquileres.repository.TipoInmuebleRepository;
import com.alquileres.exception.BusinessException;
import com.alquileres.exception.ErrorCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TipoInmuebleService {

    @Autowired
    private TipoInmuebleRepository tipoInmuebleRepository;

    // Obtener todos los tipos de inmueble
    public List<TipoInmuebleDTO> obtenerTodosLosTiposInmueble() {
        return tipoInmuebleRepository.findAll().stream()
                .map(TipoInmuebleDTO::new)
                .collect(Collectors.toList());
    }

    // Obtener tipo de inmueble por ID
    public TipoInmuebleDTO obtenerTipoInmueblePorId(Long id) {
        Optional<TipoInmueble> tipoInmueble = tipoInmuebleRepository.findById(id);
        if (tipoInmueble.isPresent()) {
            return new TipoInmuebleDTO(tipoInmueble.get());
        } else {
            throw new BusinessException(ErrorCodes.TIPO_INMUEBLE_NO_ENCONTRADO, "Tipo de inmueble no encontrado", HttpStatus.NOT_FOUND);
        }
    }

    // Crear nuevo tipo de inmueble
    public TipoInmuebleDTO crearTipoInmueble(TipoInmuebleDTO tipoInmuebleDTO) {
        // Validar nombre único
        if (tipoInmuebleRepository.existsByNombre(tipoInmuebleDTO.getNombre())) {
            throw new BusinessException(ErrorCodes.TIPO_INMUEBLE_DUPLICADO, "Ya existe un tipo de inmueble con ese nombre", HttpStatus.BAD_REQUEST);
        }

        TipoInmueble tipoInmueble = tipoInmuebleDTO.toEntity();
        TipoInmueble tipoInmuebleGuardado = tipoInmuebleRepository.save(tipoInmueble);
        return new TipoInmuebleDTO(tipoInmuebleGuardado);
    }

    // Actualizar tipo de inmueble
    public TipoInmuebleDTO actualizarTipoInmueble(Long id, TipoInmuebleDTO tipoInmuebleDTO) {
        Optional<TipoInmueble> tipoInmuebleExistente = tipoInmuebleRepository.findById(id);

        if (!tipoInmuebleExistente.isPresent()) {
            throw new BusinessException(ErrorCodes.TIPO_INMUEBLE_NO_ENCONTRADO, "Tipo de inmueble no encontrado", HttpStatus.NOT_FOUND);
        }

        // Validar nombre único (excluyendo el actual)
        Optional<TipoInmueble> tipoConMismoNombre = tipoInmuebleRepository.findByNombre(tipoInmuebleDTO.getNombre());
        if (tipoConMismoNombre.isPresent() && !tipoConMismoNombre.get().getId().equals(id)) {
            throw new BusinessException(ErrorCodes.TIPO_INMUEBLE_DUPLICADO, "Ya existe un tipo de inmueble con ese nombre", HttpStatus.BAD_REQUEST);
        }

        TipoInmueble tipoInmueble = tipoInmuebleExistente.get();
        tipoInmueble.setNombre(tipoInmuebleDTO.getNombre());

        TipoInmueble tipoInmuebleActualizado = tipoInmuebleRepository.save(tipoInmueble);
        return new TipoInmuebleDTO(tipoInmuebleActualizado);
    }

    // Eliminar tipo de inmueble
    public void eliminarTipoInmueble(Long id) {
        if (!tipoInmuebleRepository.existsById(id)) {
            throw new BusinessException(ErrorCodes.TIPO_INMUEBLE_NO_ENCONTRADO, "Tipo de inmueble no encontrado", HttpStatus.NOT_FOUND);
        }
        tipoInmuebleRepository.deleteById(id);
    }
}
