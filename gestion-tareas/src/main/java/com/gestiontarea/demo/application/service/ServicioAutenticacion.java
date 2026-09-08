package com.gestiontarea.demo.application.service;

import org.springframework.stereotype.Service;

import com.gestiontarea.demo.application.port.in.AutenticarUsuarioUseCase;
import com.gestiontarea.demo.application.port.out.PasswordHasherPort;
import com.gestiontarea.demo.application.port.out.TokenGeneratorPort;
import com.gestiontarea.demo.application.port.out.UsuarioRepositoryPort;
import com.gestiontarea.demo.domain.models.Usuario;
import com.gestiontarea.demo.exception.CredencialesInvalidasException;

@Service 
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
        Usuario usuario = usuarioRepository.buscarPorEmail(email)
            .orElseThrow(CredencialesInvalidasException::new);

        if (!passwordHasher.verificar(passwordPlano, usuario.getPasswordHash())){
            throw new CredencialesInvalidasException();
        }

        if (!usuario.isActivo()) {
            throw new CredencialesInvalidasException();
        }
       
        return tokenGenerator.generar(usuario);
    }
    
}
