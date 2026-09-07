package com.gestiontarea.demo.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record ProyectoRequest(
    @NotBlank String nombre, 
    String descripcion
) {}
