package com.gestiontarea.demo.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroRequest(
    @NotBlank String nombre, 
    @NotBlank @Email String email,
    @NotBlank @Size(min = 8, message = "La password debe de tener al menos 8 caracteres") String password
) {}
