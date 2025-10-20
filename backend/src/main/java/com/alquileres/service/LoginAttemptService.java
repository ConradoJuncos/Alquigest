package com.alquileres.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private static final Logger logger = LoggerFactory.getLogger(LoginAttemptService.class);

    private static final int MAX_ATTEMPTS_BEFORE_SHORT_DELAY = 2; // Al 3er intento
    private static final int SHORT_DELAY_SECONDS = 5;
    private static final int LONG_DELAY_SECONDS = 30;

    // Mapa para almacenar los intentos fallidos por usuario
    private final Map<String, LoginAttemptInfo> loginAttempts = new ConcurrentHashMap<>();

    /**
     * Clase interna para almacenar información de intentos de login
     */
    private static class LoginAttemptInfo {
        int failedAttempts;
        LocalDateTime lastAttemptTime;

        LoginAttemptInfo() {
            this.failedAttempts = 0;
            this.lastAttemptTime = LocalDateTime.now();
        }
    }

    /**
     * Registra un intento de login fallido para un usuario
     */
    public void loginFailed(String username) {
        LoginAttemptInfo info = loginAttempts.getOrDefault(username, new LoginAttemptInfo());
        info.failedAttempts++;
        info.lastAttemptTime = LocalDateTime.now();
        loginAttempts.put(username, info);

        logger.warn("Intento de login fallido para usuario '{}'. Total de intentos: {}",
                    username, info.failedAttempts);
    }

    /**
     * Registra un login exitoso y limpia los intentos fallidos
     */
    public void loginSucceeded(String username) {
        loginAttempts.remove(username);
        logger.info("Login exitoso para usuario '{}'. Intentos fallidos reiniciados.", username);
    }

    /**
     * Verifica si el usuario debe esperar antes de intentar de nuevo
     * y aplica el delay correspondiente
     */
    public void checkAndApplyDelay(String username) throws InterruptedException {
        LoginAttemptInfo info = loginAttempts.get(username);

        if (info == null) {
            return; // No hay intentos previos
        }

        int attempts = info.failedAttempts;

        if (attempts >= MAX_ATTEMPTS_BEFORE_SHORT_DELAY) {
            int delaySeconds;

            if (attempts == MAX_ATTEMPTS_BEFORE_SHORT_DELAY) {
                // Tercer intento: delay de 5 segundos
                delaySeconds = SHORT_DELAY_SECONDS;
                logger.warn("Usuario '{}' ha alcanzado {} intentos fallidos. Aplicando delay de {} segundos.",
                           username, attempts + 1, delaySeconds);
            } else {
                // Cuarto intento en adelante: delay de 30 segundos
                delaySeconds = LONG_DELAY_SECONDS;
                logger.warn("Usuario '{}' ha alcanzado {} intentos fallidos. Aplicando delay de {} segundos.",
                           username, attempts + 1, delaySeconds);
            }

            // Aplicar el delay
            Thread.sleep(delaySeconds * 1000L);
        }
    }

    /**
     * Obtiene el número de intentos fallidos de un usuario
     */
    public int getFailedAttempts(String username) {
        LoginAttemptInfo info = loginAttempts.get(username);
        return info != null ? info.failedAttempts : 0;
    }

    /**
     * Limpia los intentos fallidos de un usuario manualmente
     */
    public void resetAttempts(String username) {
        loginAttempts.remove(username);
        logger.info("Intentos fallidos reiniciados manualmente para usuario '{}'", username);
    }

    /**
     * Limpia intentos antiguos (más de 24 horas)
     * Este método podría ser llamado periódicamente por un scheduler
     */
    public void cleanupOldAttempts() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(24);
        loginAttempts.entrySet().removeIf(entry ->
            entry.getValue().lastAttemptTime.isBefore(threshold)
        );
        logger.debug("Limpieza de intentos antiguos completada");
    }
}

