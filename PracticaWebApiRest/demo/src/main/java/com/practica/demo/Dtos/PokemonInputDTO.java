package com.practica.demo.Dtos;

import java.util.List;

public record PokemonInputDTO(
    String nombre,
    List<String> tipos,
    int nivel,
    PokemonDto.Stats stats
) {} 
