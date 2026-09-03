package com.practica.demo.Exceptions;

import java.time.Instant;

public record ErrorResponse(
    Instant timestamp,
    int status,
    String error,
    String mensaje,
    String path
) {}
