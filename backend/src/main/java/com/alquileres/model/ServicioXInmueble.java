package com.alquileres.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "servicio_x_inmueble")
public class ServicioXInmueble {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotNull(message = "El inmueble es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inmueble_id", nullable = false)
    private Inmueble inmueble;

    @NotNull(message = "El tipo de servicio es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_servicio_id", nullable = false)
    private TipoServicio tipoServicio;

    @Column(name = "nro_cuenta", length = 50)
    private String nroCuenta;

    @Column(name = "nro_contrato", length = 50)
    private String nroContrato;

    @Column(name = "es_de_inquilino", nullable = false)
    private Boolean esDeInquilino = false;

    @Column(name = "es_anual", nullable = false)
    private Boolean esAnual = false;

    @Column(name = "es_activo", nullable = false)
    private Boolean esActivo = true;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;

    // Constructor por defecto
    public ServicioXInmueble() {
    }

    // Constructor con parámetros principales
    public ServicioXInmueble(Inmueble inmueble, TipoServicio tipoServicio) {
        this.inmueble = inmueble;
        this.tipoServicio = tipoServicio;
        this.esDeInquilino = false;
        this.esAnual = false;
        this.esActivo = true;
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
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Inmueble getInmueble() {
        return inmueble;
    }

    public void setInmueble(Inmueble inmueble) {
        this.inmueble = inmueble;
    }

    public TipoServicio getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(TipoServicio tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public String getNroCuenta() {
        return nroCuenta;
    }

    public void setNroCuenta(String nroCuenta) {
        this.nroCuenta = nroCuenta;
    }

    public String getNroContrato() {
        return nroContrato;
    }

    public void setNroContrato(String nroContrato) {
        this.nroContrato = nroContrato;
    }

    public Boolean getEsDeInquilino() {
        return esDeInquilino;
    }

    public void setEsDeInquilino(Boolean esDeInquilino) {
        this.esDeInquilino = esDeInquilino;
    }

    public Boolean getEsAnual() {
        return esAnual;
    }

    public void setEsAnual(Boolean esAnual) {
        this.esAnual = esAnual;
    }

    public Boolean getEsActivo() {
        return esActivo;
    }

    public void setEsActivo(Boolean esActivo) {
        this.esActivo = esActivo;
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
        return "ServicioXInmueble{" +
                "id=" + id +
                ", inmuebleId=" + (inmueble != null ? inmueble.getId() : null) +
                ", tipoServicioId=" + (tipoServicio != null ? tipoServicio.getId() : null) +
                ", nroCuenta='" + nroCuenta + '\'' +
                ", nroContrato='" + nroContrato + '\'' +
                ", esDeInquilino=" + esDeInquilino +
                ", esAnual=" + esAnual +
                ", esActivo=" + esActivo +
                '}';
    }
}
