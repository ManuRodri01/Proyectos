package manuRodri.intercambioCartas.Entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class Publicacion {
    private int id;
    private Persona publicador;
    private Carta carta;
    private String descripcion;
    private List<Carta> intereses;
    private List<String> imagenes;
    private Float precio;
    private estadoConservacion estadoConservacion;
    private LocalDateTime fechaPublicacion;
    private LocalDateTime fechaCierre;


    public Publicacion(LocalDateTime fechaPublicacion, estadoConservacion estadoConservacion, List<String> imagenes, Carta carta) {
        this.fechaPublicacion = fechaPublicacion;
        this.estadoConservacion = estadoConservacion;
        this.imagenes = imagenes;
        this.carta = carta;
    }

    public Publicacion(Persona publicador, Carta carta, String descripcion, List<Carta> intereses, List<String> imagenes, Float precio, estadoConservacion estadoConservacion, LocalDateTime fechaPublicacion, LocalDateTime fechaCierre) {
        this.publicador = publicador;
        this.carta = carta;
        this.descripcion = descripcion;
        this.intereses = intereses;
        this.imagenes = imagenes;
        this.precio = precio;
        this.estadoConservacion = estadoConservacion;
        this.fechaPublicacion = fechaPublicacion;
        this.fechaCierre = fechaCierre;
    }

    public Publicacion(Carta carta, String descripcion, List<Carta> intereses, Float precio, estadoConservacion estadoConservacion, LocalDateTime fechaPublicacion) {
        this.carta = carta;
        this.descripcion = descripcion;
        this.intereses = intereses;
        this.precio = precio;
        this.estadoConservacion = estadoConservacion;
        this.fechaPublicacion = fechaPublicacion;
    }
}

