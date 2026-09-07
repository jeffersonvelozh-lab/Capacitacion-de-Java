package com.gestiontarea.demo.application.port.in;

public interface AutenticarUsuarioUseCase {
    /**
     * @return el token JWT si las credenciales son validas
     * TODO: define una excepcion de dominio (ej. CredencialesInvalidasException)
     * para el caso de fallo, en vez de devolver null.
     */
    String login(String email, String passwordPlano);
}
