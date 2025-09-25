package com.alquileres.dto;

import com.alquileres.model.Usuario;
import com.alquileres.model.Rol;
import java.util.Set;
import java.util.stream.Collectors;

public class UsuarioDTO {

    private Long id;
    private String username;
    private String email;
    private Boolean esActivo;
    private Set<RolDTO> roles;

    // Constructors
    public UsuarioDTO() {}

    public UsuarioDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.username = usuario.getUsername();
        this.email = usuario.getEmail();
        this.esActivo = usuario.getEsActivo();
        this.roles = usuario.getRoles().stream()
                .map(RolDTO::new)
                .collect(Collectors.toSet());
    }

    public UsuarioDTO(Long id, String username, String email, Boolean esActivo) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.esActivo = esActivo;
    }

    // Convert to Entity (without password for security)
    public Usuario toEntity() {
        Usuario usuario = new Usuario();
        usuario.setId(this.id);
        usuario.setUsername(this.username);
        usuario.setEmail(this.email);
        usuario.setEsActivo(this.esActivo);
        // Note: roles and password should be set separately for security
        return usuario;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getEsActivo() {
        return esActivo;
    }

    public void setEsActivo(Boolean esActivo) {
        this.esActivo = esActivo;
    }

    public Set<RolDTO> getRoles() {
        return roles;
    }

    public void setRoles(Set<RolDTO> roles) {
        this.roles = roles;
    }
}
