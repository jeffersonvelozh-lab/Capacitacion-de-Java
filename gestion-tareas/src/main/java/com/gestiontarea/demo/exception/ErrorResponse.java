package com.gestiontarea.demo.exception;

import java.time.Instant;

public record ErrorResponse(String mensaje, int status, Instant timestamp) {
     public static ErrorResponse of(String mensaje, int status) {
        return new ErrorResponse(mensaje, status, Instant.now());
    }
}
