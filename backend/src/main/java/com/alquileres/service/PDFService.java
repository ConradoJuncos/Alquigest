package com.alquileres.service;

import com.alquileres.model.PDF;
import com.alquileres.repository.PDFRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class PDFService {

    private final PDFRepository pdfRepository;

    public PDFService(PDFRepository pdfRepository) {
        this.pdfRepository = pdfRepository;
    }

    @Transactional
    public PDF guardarPDF(String ambito, byte[] file, String nombreArchivo) throws IOException {
        PDF pdf = new PDF(ambito, file, nombreArchivo);
        return pdfRepository.save(pdf);
    }

    @Transactional(readOnly = true)
    public Optional<PDF> obtenerPDF(Long id) {
        return pdfRepository.findById(id);
    }

    @Transactional
    public void eliminarPDF(Long id) {
        pdfRepository.deleteById(id);
    }
}

