package com.alquileres.controller;

import com.alquileres.dto.JwtResponse;
import com.alquileres.dto.LoginRequest;
import com.alquileres.dto.MessageResponse;
import com.alquileres.dto.SignupRequest;
import com.alquileres.model.Rol;
import com.alquileres.model.RolNombre;
import com.alquileres.model.Usuario;
import com.alquileres.repository.RolRepository;
import com.alquileres.repository.UsuarioRepository;
import com.alquileres.security.JwtUtils;
import com.alquileres.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "API para autenticación y registro de usuarios")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    RolRepository rolRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    @Operation(summary = "Iniciar sesión")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping("/signup")
    @Operation(summary = "Registrar nuevo usuario")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (usuarioRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: El nombre de usuario ya está en uso!"));
        }

        if (usuarioRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: El email ya está en uso!"));
        }

        // Crear nueva cuenta de usuario
        Usuario usuario = new Usuario(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Rol> roles = new HashSet<>();

        if (strRoles == null) {
            Rol secretariaRole = rolRepository.findByNombre(RolNombre.ROLE_SECRETARIA)
                    .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
            roles.add(secretariaRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Rol adminRole = rolRepository.findByNombre(RolNombre.ROLE_ADMINISTRADOR)
                                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
                        roles.add(adminRole);
                        break;
                    case "abogada":
                        Rol abogadaRole = rolRepository.findByNombre(RolNombre.ROLE_ABOGADA)
                                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
                        roles.add(abogadaRole);
                        break;
                    default:
                        Rol secretariaRole = rolRepository.findByNombre(RolNombre.ROLE_SECRETARIA)
                                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
                        roles.add(secretariaRole);
                }
            });
        }

        usuario.setRoles(roles);
        usuarioRepository.save(usuario);

        return ResponseEntity.ok(new MessageResponse("Usuario registrado exitosamente!"));
    }
}
