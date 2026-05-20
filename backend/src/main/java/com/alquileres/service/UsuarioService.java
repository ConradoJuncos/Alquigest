package com.alquileres.service;

import com.alquileres.model.Rol;
import com.alquileres.model.RolNombre;
import com.alquileres.model.Usuario;
import com.alquileres.repository.RolRepository;
import com.alquileres.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          RolRepository rolRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean existsByUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    @Transactional
    public Usuario registrar(String username, String email, String rawPassword, Set<String> roleNames) {
        Usuario usuario = new Usuario(username, email, passwordEncoder.encode(rawPassword));
        usuario.setRoles(resolverRoles(roleNames));
        return usuarioRepository.save(usuario);
    }

    public List<RolNombre> obtenerRolesDeUsuario(Long userId) {
        Usuario usuario = usuarioRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return usuario.getRoles().stream()
            .map(Rol::getNombre)
            .collect(Collectors.toList());
    }

    private Set<Rol> resolverRoles(Set<String> strRoles) {
        Set<Rol> roles = new HashSet<>();
        if (strRoles == null) {
            roles.add(findRol(RolNombre.ROLE_SECRETARIA));
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin" -> roles.add(findRol(RolNombre.ROLE_ADMINISTRADOR));
                    case "abogada" -> roles.add(findRol(RolNombre.ROLE_ABOGADA));
                    default -> roles.add(findRol(RolNombre.ROLE_SECRETARIA));
                }
            });
        }
        return roles;
    }

    private Rol findRol(RolNombre nombre) {
        return rolRepository.findByNombre(nombre)
            .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
    }
}
