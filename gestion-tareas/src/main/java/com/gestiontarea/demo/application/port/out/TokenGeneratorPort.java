package com.gestiontarea.demo.application.port.out;

import com.gestiontarea.demo.domain.models.Usuario;

public interface TokenGeneratorPort {
    
    String generar(Usuario usuario);

    /**
     * @return el email/subject contenido en el token si es valido
     * TODO: decide si prefieres lanzar una excepcion de dominio en vez de Optional
     */
    String validarYObtenerSubject(String token);
}
