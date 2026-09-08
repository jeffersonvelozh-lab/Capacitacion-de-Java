package com.gestiontarea.demo.exception;

// usa esta excepcion en ServicioAutenticacion en vez de RuntimeException generico
public class CredencialesInvalidasException extends RuntimeException {

    public CredencialesInvalidasException(){
        super("Email o Contraseña incorrectos");
    }

    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}
