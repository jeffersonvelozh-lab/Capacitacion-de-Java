package com.gestiontarea.demo.infrastructure.adapter.in.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestiontarea.demo.application.port.in.AutenticarUsuarioUseCase;
import com.gestiontarea.demo.infrastructure.adapter.in.rest.dto.LoginRequest;
import com.gestiontarea.demo.infrastructure.adapter.in.rest.dto.LoginResponse;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * EJEMPLO COMPLETO: el controller SOLO traduce HTTP <-> caso de uso.
 * No tiene logica de negocio -- eso vive en ServicioAutenticacion.
 * Fijate que depende de la INTERFAZ (AutenticarUsuarioUseCase), no de la
 * clase concreta -- eso es lo que te permite testear el controller con
 * @WebMvcTest mockeando solo el use case, sin levantar la base de datos.
 */
@RestController 
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AutenticarUsuarioUseCase autenticarUsuarioUseCase;

    public AuthController(AutenticarUsuarioUseCase autenticarUsuarioUseCase) {
        this.autenticarUsuarioUseCase = autenticarUsuarioUseCase;
    }

    @PostMapping("/login")
    public LoginResponse loguin(@RequestBody @Valid LoginRequest request) {
        String token = autenticarUsuarioUseCase.login(request.email(), request.password());
        return new LoginResponse(token);
    }
    // TODO: agrega POST /api/auth/registro usando RegistrarUsuarioUseCase
    // (sigue el mismo patron que login())
}
