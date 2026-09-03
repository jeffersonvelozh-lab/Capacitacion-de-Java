package com.practica.demo.Dtos;

import java.util.List;
import jakarta.validation.constraints.*;


public record PokemonInputDTO(
    @NotBlank(message = "El nombre no puede estar vacío")
    String nombre,

    @NotEmpty(message = "Debe tener al menos un tipo")
    List<String> tipos,

    @Min(value = 1, message = "El nivel minimo es de 1")
    @Max(value = 100,message = "El nivel maximo es de 100")
    int nivel,

    @NotNull(message = "Las estadisticas son obligatorias")
    PokemonDto.Stats stats
) {} 
