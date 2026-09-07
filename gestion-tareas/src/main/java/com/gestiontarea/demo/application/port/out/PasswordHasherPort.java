package com.gestiontarea.demo.application.port.out;

public interface PasswordHasherPort {
    
    String hashear(String passwordPlano);

    boolean verificar(String passwordPlano, String passwordHash);
}
