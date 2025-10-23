package com.alquileres.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "pdfs")
public class PDF {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "ambito", nullable = false)
    private String ambito; // "CONTRATO", "PAGO_SERVICIO", "PAGO_ALQUILER"

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "file", nullable = false)
    private byte[] file;

    @Column(name = "nombre_archivo")
    private String nombreArchivo;

    @Column(name = "created_at")
    private String createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public PDF() {
    }

    public PDF(String ambito, byte[] file, String nombreArchivo) {
        this.ambito = ambito;
        this.file = file;
        this.nombreArchivo = nombreArchivo;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAmbito() {
        return ambito;
    }

    public void setAmbito(String ambito) {
        this.ambito = ambito;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

