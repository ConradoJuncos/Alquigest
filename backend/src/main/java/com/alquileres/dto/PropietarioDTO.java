package com.alquileres.dto;

import com.alquileres.model.Propietario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PropietarioDTO {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 50, message = "El apellido no puede exceder 50 caracteres")
    private String apellido;

    @Size(max = 20, message = "El CUIL no puede exceder 20 caracteres")
    private String cuil;

    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefono;

    @Email(message = "El email debe tener un formato válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String email;

    @Size(max = 100, message = "La dirección no puede exceder 100 caracteres")
    private String direccion;

    @Size(max = 100, message = "El barrio no puede exceder 100 caracteres")
    private String barrio;

    private Boolean esActivo;

    // Constructor por defecto
    public PropietarioDTO() {
    }

    // Constructor desde entidad
    public PropietarioDTO(Propietario propietario) {
        this.id = propietario.getId();
        this.nombre = propietario.getNombre();
        this.apellido = propietario.getApellido();
        this.cuil = propietario.getCuil();
        this.telefono = propietario.getTelefono();
        this.email = propietario.getEmail();
        this.direccion = propietario.getDireccion();
        this.barrio = propietario.getBarrio();
        this.esActivo = propietario.getEsActivo();
    }

    // Método para convertir a entidad
    public Propietario toEntity() {
        Propietario propietario = new Propietario();
        propietario.setId(this.id);
        propietario.setNombre(this.nombre);
        propietario.setApellido(this.apellido);
        propietario.setCuil(this.cuil);
        propietario.setTelefono(this.telefono);
        propietario.setEmail(this.email);
        propietario.setDireccion(this.direccion);
        propietario.setBarrio(this.barrio);
        propietario.setEsActivo(this.esActivo != null ? this.esActivo : true);
        return propietario;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
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
}
