package com.gestiontarea.demo.application.service;

import java.util.Optional;

import com.gestiontarea.demo.application.port.in.AutenticarUsuarioUseCase;
import com.gestiontarea.demo.application.port.out.PasswordHasherPort;
import com.gestiontarea.demo.application.port.out.TokenGeneratorPort;
import com.gestiontarea.demo.application.port.out.UsuarioRepositoryPort;
import com.gestiontarea.demo.domain.models.Usuario;


public class ServicioAutenticacion implements AutenticarUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordHasherPort passwordHasher;
    private final TokenGeneratorPort tokenGenerator;

    public ServicioAutenticacion(UsuarioRepositoryPort usuarioRepository,
                                  PasswordHasherPort passwordHasher,
                                  TokenGeneratorPort tokenGenerator) {
        this.usuarioRepository = usuarioRepository;
        this.passwordHasher = passwordHasher;
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public String login(String email, String passwordPlano) {
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorEmail(email);

        // TODO: reemplaza esto por una excepcion de dominio propia
        // (ej. CredencialesInvalidasException) que luego el @ControllerAdvice
        // traduzca a un 401. Lanzar RuntimeException generico no es buena practica.
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Credenciales invalidas");
        }

        Usuario usuario = usuarioOpt.get();

        if (!usuario.isActivo()) {
            throw new RuntimeException("Usuario inactivo");
        }

        if (!passwordHasher.verificar(passwordPlano, usuario.getPasswordHash())) {
            throw new RuntimeException("Credenciales invalidas");
        }

        return tokenGenerator.generar(usuario);
    }
    
}
