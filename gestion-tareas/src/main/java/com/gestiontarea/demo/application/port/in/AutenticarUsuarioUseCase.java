package com.gestiontarea.demo.application.port.in;

public interface AutenticarUsuarioUseCase {
    /**
     * @return el token JWT si las credenciales son validas
     * @throws com.gestiontarea.demo.exception
     * para el caso de fallo, en vez de devolver null.
     */
    String login(String email, String passwordPlano);
}
