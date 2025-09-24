package com.alquileres.dto;

import com.alquileres.model.Rol;
import com.alquileres.model.RolNombre;

public class RolDTO {

    private Long id;
    private RolNombre nombre;

    // Constructors
    public RolDTO() {}

    public RolDTO(Rol rol) {
        this.id = rol.getId();
        this.nombre = rol.getNombre();
    }

    public RolDTO(Long id, RolNombre nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    // Convert to Entity
    public Rol toEntity() {
        Rol rol = new Rol();
        rol.setId(this.id);
        rol.setNombre(this.nombre);
        return rol;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RolNombre getNombre() {
        return nombre;
    }

    public void setNombre(RolNombre nombre) {
        this.nombre = nombre;
    }
}
