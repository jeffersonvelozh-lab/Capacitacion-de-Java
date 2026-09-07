package com.gestiontarea.demo.domain.models;

/**
 * Modelo de dominio puro. NO lleva anotaciones de Spring ni JPA:
 * esta clase no sabe que existe una base de datos ni un framework web.
 *
 * TODO: define los campos reales (nombre, email, passwordHash, rol, activo...)
 * TODO: agrega comportamiento de dominio si aplica, ej: usuario.esAdmin()
 */

public class Usuario {
    private final Long id;
    private String nombre;
    private String email;
    private String passwordHash;
    private Rol rol;
    private boolean activo;

    public Usuario(Long id, String nombre, String email, String passwordHash, Rol rol, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.activo = activo;
    }

    public boolean esAdmin() {
        return rol == Rol.ADMIN;
    }

    // TODO: getters (y setters solo si realmente los necesitas; preferir inmutabilidad)

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Rol getRol() { return rol; }
    public boolean isActivo() { return activo; }
}
