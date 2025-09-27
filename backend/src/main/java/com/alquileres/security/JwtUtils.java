package com.alquileres.security;

import com.alquileres.service.TokenBlacklistService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Value("${app.jwt-secret:mySecretKey}")
    private String jwtSecret;

    @Value("${app.jwt-expiration-ms:86400000}")
    private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        // Primero verificar si el token está en la blacklist
        if (tokenBlacklistService.isTokenBlacklisted(authToken)) {
            System.err.println("JWT token is blacklisted");
            return false;
        }

        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            System.err.println("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.err.println("JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("JWT claims string is empty: " + e.getMessage());
        }

        return false;
    }

    /**
     * Invalida un token agregándolo a la blacklist
     */
    public void invalidateToken(String token) {
        tokenBlacklistService.blacklistToken(token);
    }

    /**
     * Obtiene la fecha de expiración de un token
     */
    public Date getExpirationDateFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    /**
     * Verifica si un token necesita ser refrescado (expira en menos de 5 minutos)
     */
    public boolean shouldRefreshToken(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            Date now = new Date();
            // Refresh si expira en menos de 5 minutos (300000 ms)
            return expiration.getTime() - now.getTime() < 300000;
        } catch (Exception e) {
            return false;
        }
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
