package com.alquileres.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "cancelacion_contrato")
public class CancelacionContrato {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull(message = "El contrato es obligatorio")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrato_id", nullable = false, unique = true)
    private Contrato contrato;

    @NotNull(message = "La fecha de cancelación es obligatoria")
    @Column(name = "fecha_cancelacion", nullable = false)
    private String fechaCancelacion;

    @NotNull(message = "El motivo de cancelación es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "motivo_cancelacion_id", nullable = false)
    private MotivoCancelacion motivoCancelacion;

    @Column(name = "observaciones", length = 1000)
    private String observaciones;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;

    // Constructor por defecto
    public CancelacionContrato() {
    }

    // Constructor con parámetros principales
    public CancelacionContrato(Contrato contrato, String fechaCancelacion, MotivoCancelacion motivoCancelacion) {
        this.contrato = contrato;
        this.fechaCancelacion = fechaCancelacion;
        this.motivoCancelacion = motivoCancelacion;
    }

    // Constructor completo
    public CancelacionContrato(Contrato contrato, String fechaCancelacion,
                              MotivoCancelacion motivoCancelacion, String observaciones) {
        this.contrato = contrato;
        this.fechaCancelacion = fechaCancelacion;
        this.motivoCancelacion = motivoCancelacion;
        this.observaciones = observaciones;
    }

    @PrePersist
    protected void onCreate() {
        String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public String getFechaCancelacion() {
        return fechaCancelacion;
    }

    public void setFechaCancelacion(String fechaCancelacion) {
        this.fechaCancelacion = fechaCancelacion;
    }

    public MotivoCancelacion getMotivoCancelacion() {
        return motivoCancelacion;
    }

    public void setMotivoCancelacion(MotivoCancelacion motivoCancelacion) {
        this.motivoCancelacion = motivoCancelacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "CancelacionContrato{" +
                "id=" + id +
                ", fechaCancelacion='" + fechaCancelacion + '\'' +
                ", motivoCancelacion=" + (motivoCancelacion != null ? motivoCancelacion.getNombre() : "null") +
                ", observaciones='" + observaciones + '\'' +
                '}';
    }
}
