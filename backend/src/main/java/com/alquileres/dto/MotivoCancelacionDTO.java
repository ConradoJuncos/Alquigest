package com.alquileres.dto;

import com.alquileres.model.MotivoCancelacion;

public class MotivoCancelacionDTO {

    private Integer id;
    private String nombre;

    public MotivoCancelacionDTO() {
    }

    public MotivoCancelacionDTO(MotivoCancelacion motivoCancelacion) {
        this.id = motivoCancelacion.getId();
        this.nombre = motivoCancelacion.getNombre();
    }

    public MotivoCancelacionDTO(Integer id, String nombre) {
        this.id = id;
        this.nombre = nombre;
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
}
