package com.gestiontarea.demo.exception;

// TODO: usa esta excepcion en ServicioRegistroUsuario cuando el email ya existe
public class EmailYaRegistradoException extends RuntimeException {
     public EmailYaRegistradoException(String mensaje) {
        super(mensaje);
    }
}
