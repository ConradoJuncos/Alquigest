package com.alquileres.dto;

import com.alquileres.model.TipoInmueble;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TipoInmuebleDTO {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede tener más de 50 caracteres")
    private String nombre;

    private String createdAt;
    private String updatedAt;

    // Constructor por defecto
    public TipoInmuebleDTO() {
    }

    // Constructor desde entidad
    public TipoInmuebleDTO(TipoInmueble tipoInmueble) {
        this.id = tipoInmueble.getId();
        this.nombre = tipoInmueble.getNombre();
    }

    // Constructor con parámetros
    public TipoInmuebleDTO(String nombre) {
        this.nombre = nombre;
    }

    // Método para convertir a entidad
    public TipoInmueble toEntity() {
        TipoInmueble tipoInmueble = new TipoInmueble();
        tipoInmueble.setId(this.id);
        tipoInmueble.setNombre(this.nombre);
        return tipoInmueble;
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

    @Override
    public String toString() {
        return "TipoInmuebleDTO{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}
