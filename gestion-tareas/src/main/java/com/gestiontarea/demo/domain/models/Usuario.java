package com.gestiontarea.demo.domain.models;

/**
 * Modelo de dominio puro. NO lleva anotaciones de Spring ni JPA:
 * esta clase no sabe que existe una base de datos ni un framework web.
 *
 *  define los campos reales (nombre, email, passwordHash, rol, activo...)
 *  agrega comportamiento de dominio si aplica, ej: usuario.esAdmin()
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

    // getters (y setters solo si realmente los necesitas; preferir inmutabilidad)

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Rol getRol() { return rol; }
    public boolean isActivo() { return activo; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setRol(Rol rol) { this.rol = rol; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
