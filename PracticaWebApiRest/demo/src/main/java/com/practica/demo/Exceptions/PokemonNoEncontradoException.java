package com.practica.demo.Exceptions;

public class PokemonNoEncontradoException extends RuntimeException {
    public PokemonNoEncontradoException(int id) {
        super("No se encontró el pokemon con id " + id);
    }
}