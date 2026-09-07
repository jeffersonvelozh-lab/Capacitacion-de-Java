package com.gestiontarea.demo.exception;

// TODO: usa esta excepcion en ServicioAutenticacion en vez de RuntimeException generico
public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}
