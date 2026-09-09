package com.gestiontarea.demo.application.port.out;

import com.gestiontarea.demo.domain.models.Rol;

public record UsuarioAutenticado(Long id, String email, Rol rol) {}
