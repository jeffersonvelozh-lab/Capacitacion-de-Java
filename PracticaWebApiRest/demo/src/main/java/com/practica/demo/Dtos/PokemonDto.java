package com.practica.demo.Dtos;

import java.util.List;
import jakarta.validation.constraints.*;

public record PokemonDto( 
    int id, 
    String nombre, 
    List<String> tipos, 
    int nivel, 
    Stats stats 
) { 
    public record Stats(
        @Min(1) int vida, 
        @Min(1) int ataque, 
        @Min(0) int defensa, 
        @Min(1) int velocidad
    ) {} 
}
