package com.gestiontarea.demo.infrastructure.adapter.out.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.gestiontarea.demo.application.port.out.PasswordHasherPort;

/**
 * COMPLETO: implementacion del puerto usando BCrypt de Spring Security.
 * Si mañana quisieras cambiar a Argon2, solo tocas esta clase -- el dominio
 * y application/ ni se enteran.
 */
@Component
public class BCryptPasswordHasherAdapter implements PasswordHasherPort {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String hashear(String passwordPlano) {
        return encoder.encode(passwordPlano);
    }

    @Override
    public boolean verificar(String passwordPlano, String passwordHash) {
        return encoder.matches(passwordPlano, passwordHash);
    }
}
