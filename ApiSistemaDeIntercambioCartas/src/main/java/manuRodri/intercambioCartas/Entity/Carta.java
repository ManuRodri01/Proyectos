package manuRodri.intercambioCartas.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Carta {
    private int id;
    private String nombre;
    private Juego juego;


    public Carta(int id, String nombre, Juego juego) {
        this.id = id;
        this.nombre = nombre;
        this.juego = juego;
    }

    public Carta(String nombre, Juego juego) {
        this.nombre = nombre;
        this.juego = juego;
    }

}
