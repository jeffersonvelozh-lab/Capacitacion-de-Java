package com.gestiontarea.demo.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TareaRequest (
    @NotBlank  String titulo, 
    String descripcion, 
    @NotNull  Long proyectoId
){}