package com.gestiontarea.demo.infrastructure.adapter.in.rest.dto;

public record RegistroResponse (
    Long id, 
    String nombre,
    String email 
) {}
