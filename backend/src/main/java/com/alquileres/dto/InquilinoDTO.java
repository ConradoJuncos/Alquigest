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

    @Size(max = 100, message = "El barrio no puede exceder 100 caracteres")
    private String barrio;

    private Boolean esActivo;

    // Constructor por defecto
    public InquilinoDTO() {
    }

    // Constructor desde entidad
    public InquilinoDTO(Inquilino inquilino) {
        this.id = inquilino.getId();
        this.nombre = inquilino.getNombre();
        this.apellido = inquilino.getApellido();
        this.cuil = inquilino.getCuil();
        this.telefono = inquilino.getTelefono();
        this.barrio = inquilino.getBarrio();
        this.esActivo = inquilino.getEsActivo();
    }

    // Constructor completo
    public InquilinoDTO(Long id, String nombre, String apellido, String cuil, String telefono, Boolean esActivo) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.cuil = cuil;
        this.telefono = telefono;
        this.esActivo = esActivo;
    }

    // Método para convertir a entidad
    public Inquilino toEntity() {
        Inquilino inquilino = new Inquilino();
        inquilino.setId(this.id);
        inquilino.setNombre(this.nombre);
        inquilino.setApellido(this.apellido);
        inquilino.setCuil(this.cuil);
        inquilino.setTelefono(this.telefono);
        inquilino.setBarrio(this.barrio);
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

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
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

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public Boolean getEsActivo() {
        return esActivo;
    }

    public void setEsActivo(Boolean esActivo) {
        this.esActivo = esActivo;
    }

    @Override
    public String toString() {
        return "InquilinoDTO{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", cuil='" + cuil + '\'' +
                ", telefono='" + telefono + '\'' +
                ", barrio='" + barrio + '\'' +
                ", esActivo=" + esActivo +
                '}';
    }
}
