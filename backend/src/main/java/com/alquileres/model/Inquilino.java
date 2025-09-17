package com.alquileres.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "inquilinos")
public class Inquilino {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inquilino_seq_gen")
    @SequenceGenerator(
        name = "inquilino_seq_gen",
        sequenceName = "inquilinos_seq",
        allocationSize = 1,
        initialValue = 1
    )
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
    @Column(nullable = false, length = 150)
    private String nombre;

    @Size(max = 50, message = "El CUIL no puede exceder 50 caracteres")
    @Column(unique = true, length = 50)
    private String cuil;

    @Size(max = 50, message = "El teléfono no puede exceder 50 caracteres")
    @Column(length = 50)
    private String telefono;

    @Column(name = "es_activo", nullable = false)
    private Boolean esActivo = true;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;

    // Constructor por defecto
    public Inquilino() {
    }

    // Constructor con parámetros principales
    public Inquilino(String nombre) {
        this.nombre = nombre;
        this.esActivo = true;
    }

    // Constructor completo
    public Inquilino(String nombre, String cuil, String telefono) {
        this.nombre = nombre;
        this.cuil = cuil;
        this.telefono = telefono;
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
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCuil() {
        return cuil;
    }

    public void setCuil(String cuil) {
        this.cuil = cuil;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
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
        return "Inquilino{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", cuil='" + cuil + '\'' +
                ", telefono='" + telefono + '\'' +
                ", esActivo=" + esActivo +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
