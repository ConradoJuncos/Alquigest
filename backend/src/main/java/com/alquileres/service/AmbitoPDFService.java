package com.alquileres.service;

import com.alquileres.model.AmbitoPDF;
import com.alquileres.repository.AmbitoPDFRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class AmbitoPDFService {

    private final AmbitoPDFRepository ambitoPDFRepository;

    public AmbitoPDFService(AmbitoPDFRepository ambitoPDFRepository) {
        this.ambitoPDFRepository = ambitoPDFRepository;
    }

    @Transactional(readOnly = true)
    public List<AmbitoPDF> obtenerTodos() {
        return ambitoPDFRepository.findAll();
    }

    @Transactional
    public void eliminar(Long id) {
        ambitoPDFRepository.deleteById(id);
    }
}

