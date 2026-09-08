package com.gestiontarea.demo.application.port.out;

import java.util.Optional;
import com.gestiontarea.demo.domain.models.Usuario;

public interface TokenGeneratorPort {
    
    String generar(Usuario usuario);

    /**
     * Valida el token y extrae el subject (normalmente el email del usario).
     * @return Optional con el subject si el token es valido; Optional.empty()
     * si esta expirdo, malformado, o la firma no es valida. 
     * decide si prefieres lanzar una excepcion de dominio en vez de Optional
     */
    //String validarYObtenerSubject(String token);
    Optional<String> validarYObtenerSubject(String token);
}
