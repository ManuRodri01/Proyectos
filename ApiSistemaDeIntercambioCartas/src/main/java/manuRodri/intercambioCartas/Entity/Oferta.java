package manuRodri.intercambioCartas.Entity;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Oferta {
    private int id;
    private Publicacion publicacion;
    private Persona ofertante;
    private List<Carta> cartas;
    private Float valor;
    private estadoOferta estadoOferta;
    private LocalDate fecha;


    public boolean tieneCartas(List<Carta> listaCartas){
        return cartas.containsAll(listaCartas);
    }
    public Oferta(){}
    public Oferta(Publicacion publicacion, estadoOferta estadoOferta, LocalDate fecha) {
        this.publicacion = publicacion;
        this.estadoOferta = estadoOferta;
        this.fecha = fecha;
    }

    public Oferta(estadoOferta estadoOferta, Publicacion publicacion, LocalDate fecha, Persona ofertante) {
        this.estadoOferta = estadoOferta;
        this.publicacion = publicacion;
        this.fecha = fecha;
        this.ofertante = ofertante;
    }
}
