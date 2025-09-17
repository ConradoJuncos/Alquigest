package com.alquileres.dto;

import com.alquileres.model.Inquilino;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class InquilinoDTO {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
    private String nombre;

    @Size(max = 50, message = "El CUIL no puede exceder 50 caracteres")
    private String cuil;

    @Size(max = 50, message = "El teléfono no puede exceder 50 caracteres")
    private String telefono;

    private Boolean esActivo;
    private String createdAt;
    private String updatedAt;

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

    // Constructor con parámetros
    public InquilinoDTO(String nombre, String cuil, String telefono) {
        this.nombre = nombre;
        this.cuil = cuil;
        this.telefono = telefono;
        this.esActivo = true;
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

    // Método para actualizar entidad existente
    public void updateEntity(Inquilino inquilino) {
        if (this.nombre != null) {
            inquilino.setNombre(this.nombre);
        }
        if (this.cuil != null) {
            inquilino.setCuil(this.cuil);
        }
        if (this.telefono != null) {
            inquilino.setTelefono(this.telefono);
        }
        if (this.esActivo != null) {
            inquilino.setEsActivo(this.esActivo);
        }
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
        return "InquilinoDTO{" +
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
