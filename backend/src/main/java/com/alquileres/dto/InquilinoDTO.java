package com.alquileres.dto;

import com.alquileres.model.Inquilino;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class InquilinoDTO {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 50, message = "El apellido no puede exceder 50 caracteres")
    private String apellido;

    @NotBlank(message = "El CUIL es obligatorio")
    @Size(max = 20, message = "El CUIL no puede exceder 20 caracteres")
    private String cuil;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefono;

    private Boolean esActivo;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // Constructor por defecto
    public InquilinoDTO() {
    }

    // Constructor desde entidad
    public InquilinoDTO(Inquilino inquilino) {
        this.id = inquilino.getId();
        this.nombre = inquilino.getNombre();
        this.cuil = inquilino.getCuil();
        this.telefono = inquilino.getTelefono();
        this.esActivo = inquilino.getEsActivo();
        this.createdAt = inquilino.getCreatedAt();
        this.updatedAt = inquilino.getUpdatedAt();
    }

    // Método para convertir DTO a entidad
    public Inquilino toEntity() {
        Inquilino inquilino = new Inquilino();
        inquilino.setId(this.id);
        inquilino.setNombre(this.nombre);
        inquilino.setCuil(this.cuil);
        inquilino.setTelefono(this.telefono);
        inquilino.setEsActivo(this.esActivo != null ? this.esActivo : true);
        return inquilino;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "InquilinoDTO{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", cuil='" + cuil + '\'' +
                ", telefono='" + telefono + '\'' +
                ", esActivo=" + esActivo +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
