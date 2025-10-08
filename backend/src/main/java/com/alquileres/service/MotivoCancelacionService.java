package com.alquileres.service;

import com.alquileres.dto.MotivoCancelacionDTO;
import com.alquileres.model.MotivoCancelacion;
import com.alquileres.repository.MotivoCancelacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MotivoCancelacionService {

    @Autowired
    private MotivoCancelacionRepository motivoCancelacionRepository;

    public List<MotivoCancelacionDTO> obtenerTodosLosMotivos() {
        List<MotivoCancelacion> motivos = motivoCancelacionRepository.findAll();
        return motivos.stream()
                .map(MotivoCancelacionDTO::new)
                .collect(Collectors.toList());
    }
}

