package com.alquileres.controller;

import com.alquileres.dto.JwtResponse;
import com.alquileres.dto.LoginRequest;
import com.alquileres.dto.MessageResponse;
import com.alquileres.dto.SignupRequest;
import com.alquileres.dto.RecuperarContrasenaDTO;
import com.alquileres.dto.ResetearContrasenaDTO;
import com.alquileres.model.RolNombre;
import com.alquileres.model.Usuario;
import com.alquileres.security.JwtUtils;
import com.alquileres.security.UserDetailsImpl;
import com.alquileres.service.PermisosService;
import com.alquileres.service.UsuarioService;
import com.alquileres.service.ContratoActualizacionService;
import com.alquileres.service.ServicioActualizacionService;
import com.alquileres.service.AlquilerActualizacionService;
import com.alquileres.service.LoginAttemptService;
import com.alquileres.service.PasswordResetService;
import com.alquileres.service.ResendEmailService;
import com.alquileres.service.CodigoSeguridadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "API para autenticación y registro de usuarios")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Value("${app.jwt.cookieName:accessToken}")
    private String jwtCookieName;

    @Value("${app.jwt.cookieMaxAge:3600}") // 1 hora por defecto
    private int cookieMaxAge;

    @Value("${app.cors.allowedOrigins:http://localhost:3000}")
    private String allowedOrigins;

    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;
    private final JwtUtils jwtUtils;
    private final com.alquileres.security.UserDetailsServiceImpl userDetailsService;
    private final PermisosService permisosService;
    private final ContratoActualizacionService contratoActualizacionService;
    private final ServicioActualizacionService servicioActualizacionService;
    private final AlquilerActualizacionService alquilerActualizacionService;
    private final LoginAttemptService loginAttemptService;
    private final PasswordResetService passwordResetService;
    private final ResendEmailService resendEmailService;
    private final CodigoSeguridadService codigoSeguridadService;

    public AuthController(
            AuthenticationManager authenticationManager,
            UsuarioService usuarioService,
            JwtUtils jwtUtils,
            com.alquileres.security.UserDetailsServiceImpl userDetailsService,
            PermisosService permisosService,
            ContratoActualizacionService contratoActualizacionService,
            ServicioActualizacionService servicioActualizacionService,
            AlquilerActualizacionService alquilerActualizacionService,
            LoginAttemptService loginAttemptService,
            PasswordResetService passwordResetService,
            ResendEmailService resendEmailService,
            com.alquileres.service.CodigoSeguridadService codigoSeguridadService) {
        this.authenticationManager = authenticationManager;
        this.usuarioService = usuarioService;
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
        this.permisosService = permisosService;
        this.contratoActualizacionService = contratoActualizacionService;
        this.servicioActualizacionService = servicioActualizacionService;
        this.alquilerActualizacionService = alquilerActualizacionService;
        this.loginAttemptService = loginAttemptService;
        this.passwordResetService = passwordResetService;
        this.resendEmailService = resendEmailService;
        this.codigoSeguridadService = codigoSeguridadService;
    }

    @PostMapping("/signin")
    @Operation(summary = "Iniciar sesión")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {

        try {
            // Verificar intentos previos y aplicar delay si es necesario
            loginAttemptService.checkAndApplyDelay(loginRequest.getUsername());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity
                    .status(429) // Too Many Requests
                    .body(new MessageResponse("Demasiados intentos fallidos. Por favor, espere antes de intentar nuevamente."));
        }

        // Ejecutar procesos de actualización automáticos de manera segura
        // Estos procesos NO afectarán el resultado del login si fallan
        ejecutarProcesosAutomaticos();

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            // Login exitoso - limpiar intentos fallidos
            loginAttemptService.loginSucceeded(loginRequest.getUsername());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            // Configurar cookie HttpOnly con el JWT
            ResponseCookie cookie = ResponseCookie.from(jwtCookieName, jwt)
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("None")
                    .path("/")
                    .maxAge(cookieMaxAge)
                    .build();

            // ⭐ AGREGAR LA COOKIE A LA RESPUESTA
            response.addHeader("Set-Cookie", cookie.toString());

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            // Obtener permisos basados en los roles del usuario
            Map<String, Boolean> permisos = obtenerPermisosUsuario(userDetails.getId());

            // Devolver datos del usuario sin el token (ahora está en la cookie)
            return ResponseEntity.ok(new JwtResponse(null, // No enviar token en el body
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles,
                    permisos));
        } catch (BadCredentialsException e) {
            // Login fallido - registrar intento
            loginAttemptService.loginFailed(loginRequest.getUsername());

            int attempts = loginAttemptService.getFailedAttempts(loginRequest.getUsername());
            String message;

            if (attempts == 2) {
                message = "Credenciales incorrectas. Advertencia: El próximo intento fallido tendrá un delay de 5 segundos.";
            } else if (attempts == 3) {
                message = "Credenciales incorrectas. Advertencia: Los próximos intentos fallidos tendrán un delay de 30 segundos.";
            } else if (attempts > 3) {
                message = "Credenciales incorrectas. Debe esperar 30 segundos antes de cada intento.";
            } else {
                message = "Credenciales incorrectas.";
            }

            return ResponseEntity
                    .status(401)
                    .body(new MessageResponse(message));
        }
    }

    /**
     * Ejecuta los procesos automáticos de actualización de forma segura.
     * Si alguno falla, se loguea el error pero NO afecta el resultado del login.
     */
    private void ejecutarProcesosAutomaticos() {
        try {
            contratoActualizacionService.actualizarContratosVencidos();
        } catch (Exception ignored) {
        }

        try {
            servicioActualizacionService.procesarPagosPendientes();
        } catch (Exception ignored) {
        }

        try {
            alquilerActualizacionService.procesarAlquileresPendientes();
        } catch (Exception ignored) {
        }
    }

    /**
     * Obtiene los permisos de un usuario basado en sus roles
     */
    private Map<String, Boolean> obtenerPermisosUsuario(Long userId) {
        try {
            List<RolNombre> rolesNombre = usuarioService.obtenerRolesDeUsuario(userId);
            return permisosService.obtenerPermisosConsolidados(rolesNombre);
        } catch (Exception e) {
            System.err.println("Error al obtener permisos del usuario: " + e.getMessage());
            return permisosService.obtenerPermisosConsolidados(List.of());
        }
    }

    @PostMapping("/signup")
    @Operation(summary = "Registrar nuevo usuario")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (usuarioService.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: El nombre de usuario ya está en uso!"));
        }

        if (usuarioService.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: El email ya está en uso!"));
        }

        Usuario usuarioGuardado = usuarioService.registrar(
            signUpRequest.getUsername(),
            signUpRequest.getEmail(),
            signUpRequest.getPassword(),
            signUpRequest.getRole()
        );

        List<String> codigosGenerados = List.of();
        try {
            codigosGenerados = codigoSeguridadService.generarCodigos(usuarioGuardado.getId());
            if (!codigosGenerados.isEmpty()) {
                logger.info("Códigos de seguridad generados para nuevo usuario: {}", usuarioGuardado.getUsername());
            }
        } catch (Exception e) {
            logger.error("Error al generar códigos de seguridad para nuevo usuario: {}", e.getMessage());
        }

        if (!codigosGenerados.isEmpty()) {
            return ResponseEntity.ok(new com.alquileres.dto.CodigosSeguridadResponseDTO(
                codigosGenerados,
                "Usuario registrado exitosamente. IMPORTANTE: Entregue estos códigos de seguridad al usuario de forma segura. " +
                "No podrá volver a verlos una vez que cierre esta ventana."
            ));
        }

        return ResponseEntity.ok(new MessageResponse("Usuario registrado exitosamente!"));
    }

    @PostMapping("/recuperar-contrasena")
    @Operation(summary = "Solicitar recuperación de contraseña",
               description = "Solicita el envío de un email de recuperación. El procesamiento se realiza de forma asíncrona. " +
                           "Siempre devuelve el mismo mensaje por razones de seguridad, independientemente de si el email existe o no.")
    public ResponseEntity<?> recuperarContrasena(@Valid @RequestBody RecuperarContrasenaDTO dto) {
        // Procesar de forma asíncrona (no bloquea la respuesta)
        passwordResetService.solicitarRecuperacionContrasena(dto.getEmail());

        // Siempre devolver el mismo mensaje, sin importar si el email existe o no
        // Esto evita que se pueda determinar qué emails están registrados en el sistema
        return ResponseEntity.ok(new MessageResponse(
            "Si el email existe en nuestro sistema, recibirás un correo con instrucciones para recuperar tu contraseña."
        ));
    }

    @PostMapping("/resetear-contrasena")
    @Operation(summary = "Resetear contraseña con token")
    public ResponseEntity<?> resetearContrasena(@Valid @RequestBody ResetearContrasenaDTO dto) {
        try {
            passwordResetService.resetearContrasena(dto.getToken(), dto.getNuevaContrasena(), dto.getConfirmarContrasena());
            return ResponseEntity.ok(new MessageResponse("Contraseña actualizada exitosamente!"));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/validar-token-reseteo/{token}")
    @Operation(summary = "Validar token de recuperación")
    public ResponseEntity<?> validarTokenReset(@PathVariable String token) {
        boolean valido = passwordResetService.validarToken(token);
        return ResponseEntity.ok(new MessageResponse(valido ? "Token válido" : "Token inválido o expirado"));
    }

    @PostMapping("/test-email")
    @Operation(summary = "Enviar email de prueba")
    public ResponseEntity<?> enviarEmailPrueba(@RequestParam String destinatario) {
        try {
            resendEmailService.enviarEmailPrueba(destinatario);
            return ResponseEntity.ok(new MessageResponse("Email de prueba enviado a: " + destinatario));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error al enviar email: " + e.getMessage()));
        }
    }

    @GetMapping("/diagnostico-email")
    @Operation(summary = "Diagnosticar conexión SMTP")
    public ResponseEntity<?> diagnosticoEmail() {
        try {
            return ResponseEntity.ok(new MessageResponse("Conexión SMTP funcionando correctamente"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(new MessageResponse("Error de conexión: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión")
    public ResponseEntity<?> logoutUser(HttpServletRequest request, HttpServletResponse response) {
        try {
            ResponseCookie rCookie = ResponseCookie.from(jwtCookieName, "")
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("None")
                    .path("/")
                    .maxAge(0)  // ⭐ Cambiar a 0 para borrar la cookie
                    .build();

            // ⭐ AGREGAR LA COOKIE AL RESPONSE PARA BORRARLA
            response.addHeader("Set-Cookie", rCookie.toString());

            // Obtener el token de la cookie para invalidarlo en la blacklist
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (jwtCookieName.equals(cookie.getName())) {
                        String jwt = cookie.getValue();
                        if (jwt != null && !jwt.isEmpty()) {
                            jwtUtils.invalidateToken(jwt);
                        }
                        break;
                    }
                }
            }

            // Limpiar el contexto de seguridad
            SecurityContextHolder.clearContext();

            return ResponseEntity.ok(new MessageResponse("Sesión cerrada exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al cerrar sesión: " + e.getMessage()));
        }
    }

    @GetMapping("/me")
    @Operation(summary = "Obtener información del usuario autenticado")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        try {
            // Obtener el token de la cookie
            String jwt = null;
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (jwtCookieName.equals(cookie.getName())) {
                        jwt = cookie.getValue();
                        break;
                    }
                }
            }

            if (jwt == null || jwt.isEmpty()) {
                return ResponseEntity.status(401)
                        .body(new MessageResponse("No autenticado"));
            }

            // Validar el token
            if (!jwtUtils.validateJwtToken(jwt)) {
                return ResponseEntity.status(401)
                        .body(new MessageResponse("Token inválido o expirado"));
            }

            // Obtener el usuario del token
            String username = jwtUtils.getUserNameFromJwtToken(jwt);
            UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(username);

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            // Obtener permisos actualizados
            Map<String, Boolean> permisos = obtenerPermisosUsuario(userDetails.getId());

            // Devolver datos del usuario sin el token
            return ResponseEntity.ok(new JwtResponse(null,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles,
                    permisos));

        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(new MessageResponse("Error al obtener información del usuario: " + e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refrescar token JWT")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Obtener el token de la cookie
            String jwt = null;
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (jwtCookieName.equals(cookie.getName())) {
                        jwt = cookie.getValue();
                        break;
                    }
                }
            }

            if (jwt == null || jwt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Token no proporcionado"));
            }

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

            // Configurar nueva cookie con el nuevo JWT
            ResponseCookie rCookie = ResponseCookie.from(jwtCookieName, newJwt)
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("None")
                    .path("/")
                    .maxAge(cookieMaxAge)
                    .build();

            // ⭐ AGREGAR LA COOKIE AL RESPONSE
            response.addHeader("Set-Cookie", rCookie.toString());

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            // Obtener permisos actualizados
            Map<String, Boolean> permisos = obtenerPermisosUsuario(userDetails.getId());

            return ResponseEntity.ok(new JwtResponse(null, // No enviar token en el body
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



