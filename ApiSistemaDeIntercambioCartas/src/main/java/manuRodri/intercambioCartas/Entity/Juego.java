package manuRodri.intercambioCartas.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Juego {

    private int id;
    private String nombre;

    public Juego(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Juego() {
    }
}
