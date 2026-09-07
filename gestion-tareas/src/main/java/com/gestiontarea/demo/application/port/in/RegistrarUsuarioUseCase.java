package com.gestiontarea.demo.application.port.in;

import com.gestiontarea.demo.domain.models.Usuario;

public interface RegistrarUsuarioUseCase {
    Usuario registrar(String nombre, String email, String passwordPlano);
}
