package com.gestiontarea.demo.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gestiontarea.demo.application.port.out.PasswordHasherPort;
import com.gestiontarea.demo.application.port.out.TokenGeneratorPort;
import com.gestiontarea.demo.application.port.out.UsuarioRepositoryPort;
import com.gestiontarea.demo.application.service.ServicioAutenticacion;
import com.gestiontarea.demo.application.service.ServicioRegistroUsuario;

/**
 * Por que esta clase existe: los servicios en application/service son POJOs
 * puros (a proposito, para no acoplar el dominio a Spring). Alguien tiene
 * que decirle a Spring como construirlos e inyectar sus dependencias --
 * ese "alguien" es esta clase de configuracion explicita.
 *
 * Alternativa mas simple (si prefieres menos ceremonia): poner @Service
 * directamente en las clases de application/service e inyectar por
 * constructor como siempre. Es una decision de cuanto "hexagonal puro"
 * quieres vs pragmatismo -- ambas son validas, hablalo en la siguiente sesion.
 *
 * TODO: agrega aqui los @Bean de ServicioProyecto y ServicioTarea cuando
 * los implementes.
 */
@Configuration
public class UseCaseConfig {

    @Bean
    public ServicioAutenticacion servicioAutenticacion(UsuarioRepositoryPort usuarioRepository,
                                                         PasswordHasherPort passwordHasher,
                                                         TokenGeneratorPort tokenGenerator) {
        return new ServicioAutenticacion(usuarioRepository, passwordHasher, tokenGenerator);
    }

    @Bean
    public ServicioRegistroUsuario servicioRegistroUsuario(UsuarioRepositoryPort usuarioRepository,
                                                             PasswordHasherPort passwordHasher) {
        return new ServicioRegistroUsuario(usuarioRepository, passwordHasher, null);
    }
}
