package com.alquileres.dto;

import com.alquileres.model.EstadoContrato;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EstadoContratoDTO {

    private Integer id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    private String nombre;

    // Constructor por defecto
    public EstadoContratoDTO() {
    }

    // Constructor desde entidad
    public EstadoContratoDTO(EstadoContrato estadoContrato) {
        this.id = estadoContrato.getId();
        this.nombre = estadoContrato.getNombre();
    }

    // Constructor con parámetros
    public EstadoContratoDTO(String nombre) {
        this.nombre = nombre;
    }

    // Convertir a entidad
    public EstadoContrato toEntity() {
        EstadoContrato estadoContrato = new EstadoContrato();
        estadoContrato.setId(this.id);
        estadoContrato.setNombre(this.nombre);
        return estadoContrato;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "EstadoContratoDTO{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}
