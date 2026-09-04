package com.practica.demo.Entities;

import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "pokemones")
public class Pokemon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    @ElementCollection
    @CollectionTable(name = "pokemon_tipos", joinColumns = @JoinColumn(name = "pokemon_id"))
    @Column(name = "tipo")
    private List<String> tipos;

    @Column(nullable = false)
    private int nivel;

    @Embedded
    private Stats stats;

    public Pokemon() {}

    public Pokemon(String nombre, List<String> tipos, int nivel, Stats stats) {
        this.nombre = nombre;
        this.tipos = tipos;
        this.nivel = nivel;
        this.stats = stats;
    }

    public Integer getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public List<String> getTipos() { return tipos; }
    public void setTipos(List<String> tipos) { this.tipos = tipos; }
    public int getNivel() { return nivel; }
    public void setNivel(int nivel) { this.nivel = nivel; }
    public Stats getStats() { return stats; }
    public void setStats(Stats stats) { this.stats = stats; }

    @Embeddable
    public static class Stats {
        private int vida;
        private int ataque;
        private int defensa;
        private int velocidad;

        public Stats() {}

        public Stats(int vida, int ataque, int defensa, int velocidad) {
            this.vida = vida;
            this.ataque = ataque;
            this.defensa = defensa;
            this.velocidad = velocidad;
        }

        public int getVida() { return vida; }
        public int getAtaque() { return ataque; }
        public int getDefensa() { return defensa; }
        public int getVelocidad() { return velocidad; }
    }
}