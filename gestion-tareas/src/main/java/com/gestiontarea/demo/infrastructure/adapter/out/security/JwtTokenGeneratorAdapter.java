package com.gestiontarea.demo.infrastructure.adapter.out.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import com.gestiontarea.demo.application.port.out.TokenGeneratorPort;
import com.gestiontarea.demo.domain.models.Usuario;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

/**
 * COMPLETO: implementacion del puerto usando la libreria jjwt.
 * Guarda el email como "subject" y el rol como claim custom -- util para
 * que el filtro de seguridad (JwtAuthenticationFilter) arme el
 * Authentication de Spring sin volver a golpear la base de datos.
 */
@Component
@EnableConfigurationProperties(JwtProperties.class)
public class JwtTokenGeneratorAdapter implements TokenGeneratorPort {

    private final JwtProperties properties;

    public JwtTokenGeneratorAdapter(JwtProperties properties) {
        this.properties = properties;
    }

    private SecretKey obtenerClave() {
        return Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generar(Usuario usuario) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + properties.getExpirationMs());

        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("rol", usuario.getRol().name())
                .claim("userId", usuario.getId())
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(obtenerClave())
                .compact();
    }

    @Override
    public Optional<String> validarYObtenerSubject(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(obtenerClave())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return Optional.of(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
