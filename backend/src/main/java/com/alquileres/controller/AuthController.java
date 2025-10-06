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
import com.alquileres.service.PermisosService;
import com.alquileres.service.ContratoActualizacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.Map;
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

    @Autowired
    com.alquileres.security.UserDetailsServiceImpl userDetailsService;

    @Autowired
    PermisosService permisosService;

    @Autowired
    ContratoActualizacionService contratoActualizacionService;

    @PostMapping("/signin")
    @Operation(summary = "Iniciar sesión")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        // Actualizar contratos vencidos antes de procesar el login
        contratoActualizacionService.actualizarContratosVencidos();

        // Actualizar fechas de aumento de contratos antes de procesar el login
        contratoActualizacionService.actualizarFechasAumento();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Obtener permisos basados en los roles del usuario
        Map<String, Boolean> permisos = obtenerPermisosUsuario(userDetails.getId());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles,
                permisos));
    }

    /**
     * Obtiene los permisos de un usuario basado en sus roles
     */
    private Map<String, Boolean> obtenerPermisosUsuario(Long userId) {
        try {
            // Obtener el usuario y sus roles
            Usuario usuario = usuarioRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Extraer los nombres de los roles
            List<RolNombre> rolesNombre = usuario.getRoles().stream()
                    .map(Rol::getNombre)
                    .collect(Collectors.toList());

            // Obtener permisos consolidados para todos los roles del usuario
            return permisosService.obtenerPermisosConsolidados(rolesNombre);

        } catch (Exception e) {
            System.err.println("Error al obtener permisos del usuario: " + e.getMessage());
            // En caso de error, devolver permisos vacíos por seguridad
            return permisosService.obtenerPermisosConsolidados(List.of());
        }
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

    @PostMapping("/signout")
    @Operation(summary = "Cerrar sesión")
    public ResponseEntity<?> logoutUser(HttpServletRequest request) {
        try {
            // Obtener el token del header Authorization
            String headerAuth = request.getHeader("Authorization");

            if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
                String jwt = headerAuth.substring(7);

                // Invalidar el token agregándolo a la blacklist
                jwtUtils.invalidateToken(jwt);
            }

            // Limpiar el contexto de seguridad
            SecurityContextHolder.clearContext();

            return ResponseEntity.ok(new MessageResponse("Sesión cerrada exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al cerrar sesión: " + e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refrescar token JWT")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        try {
            // Obtener el token del header Authorization
            String headerAuth = request.getHeader("Authorization");

            if (headerAuth == null || !headerAuth.startsWith("Bearer ")) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Token no proporcionado"));
            }

            String jwt = headerAuth.substring(7);

            // Verificar si el token es válido (no expirado y no en blacklist)
            if (!jwtUtils.validateJwtToken(jwt)) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Token inválido o expirado"));
            }

            // Obtener el usuario del token
            String username = jwtUtils.getUserNameFromJwtToken(jwt);
            UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(username);

            // Invalidar el token actual
            jwtUtils.invalidateToken(jwt);

            // Crear nueva autenticación
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            // Generar nuevo token
            String newJwt = jwtUtils.generateJwtToken(authentication);

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            // Obtener permisos actualizados
            Map<String, Boolean> permisos = obtenerPermisosUsuario(userDetails.getId());

            return ResponseEntity.ok(new JwtResponse(newJwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles,
                    permisos));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al refrescar token: " + e.getMessage()));
        }
    }
}
