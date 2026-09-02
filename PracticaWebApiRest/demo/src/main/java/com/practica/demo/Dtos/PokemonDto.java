package com.practica.demo.Dtos;

import java.util.List;

public record PokemonDto( 
    int id, 
    String nombre, 
    List<String> tipos, 
    int nivel, 
    Stats stats 
) { 
    public record Stats(
        int vida, 
        int ataque, 
        int defensa, 
        int velocidad
    ) {} 
}
