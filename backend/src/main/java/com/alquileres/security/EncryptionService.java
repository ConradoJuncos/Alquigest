package com.alquileres.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Servicio de encriptación AES para datos sensibles
 * Permite encriptar y desencriptar valores de manera reversible
 */
@Component
public class EncryptionService {

    @Value("${encryption.key}")
    private String encryptionKey;

    private static final String ALGORITHM = "AES";

    /**
     * Encripta un valor usando AES
     *
     * @param valor El valor a encriptar
     * @return El valor encriptado en Base64
     * @throws Exception Si ocurre un error durante la encriptación
     */
    public String encriptar(String valor) throws Exception {
        if (valor == null || valor.isEmpty()) {
            return null;
        }
        SecretKeySpec key = obtenerClave();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encriptado = cipher.doFinal(valor.getBytes());
        return Base64.getEncoder().encodeToString(encriptado);
    }

    /**
     * Desencripta un valor usando AES
     *
     * @param valorEncriptado El valor encriptado en Base64
     * @return El valor desencriptado
     * @throws Exception Si ocurre un error durante la desencriptación
     */
    public String desencriptar(String valorEncriptado) throws Exception {
        if (valorEncriptado == null || valorEncriptado.isEmpty()) {
            return null;
        }
        SecretKeySpec key = obtenerClave();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] desencriptado = cipher.doFinal(Base64.getDecoder().decode(valorEncriptado));
        return new String(desencriptado);
    }

    /**
     * Obtiene la clave secreta desde la configuración
     *
     * @return La clave AES como SecretKeySpec
     */
    private SecretKeySpec obtenerClave() {
        byte[] keyBytes = Base64.getDecoder().decode(encryptionKey);
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, ALGORITHM);
    }
}

