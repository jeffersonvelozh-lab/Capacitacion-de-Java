package com.gestiontarea.demo.infrastructure.adapter.in.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestiontarea.demo.application.port.in.AutenticarUsuarioUseCase;
import com.gestiontarea.demo.application.port.in.RegistrarUsuarioUseCase;
import com.gestiontarea.demo.domain.models.Usuario;
import com.gestiontarea.demo.infrastructure.adapter.in.rest.dto.LoginRequest;
import com.gestiontarea.demo.infrastructure.adapter.in.rest.dto.LoginResponse;
import com.gestiontarea.demo.infrastructure.adapter.in.rest.dto.RegistroRequest;
import com.gestiontarea.demo.infrastructure.adapter.in.rest.dto.RegistroResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;

    public AuthController(AutenticarUsuarioUseCase autenticarUsuarioUseCase, 
        RegistrarUsuarioUseCase registrarUsuarioUseCase
    ) {
        this.autenticarUsuarioUseCase = autenticarUsuarioUseCase;
        this.registrarUsuarioUseCase = registrarUsuarioUseCase;
    }

    @PostMapping("/login")
    public LoginResponse loguin(@RequestBody @Valid LoginRequest request) {
        String token = autenticarUsuarioUseCase.login(request.email(), request.password());
        return new LoginResponse(token);
    }

    @PostMapping("/regsitro")
    public ResponseEntity<RegistroResponse> registro(@RequestBody @Valid RegistroRequest request){
        Usuario usuarioCreado = registrarUsuarioUseCase.registrar(
            request.nombre(), request.email(), request.password());

        RegistroResponse response = new RegistroResponse(
            usuarioCreado.getId(),
            usuarioCreado.getNombre(),
            usuarioCreado.getEmail()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
