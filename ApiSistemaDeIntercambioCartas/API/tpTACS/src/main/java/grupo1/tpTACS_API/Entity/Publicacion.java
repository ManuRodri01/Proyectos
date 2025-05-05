package grupo1.tpTACS_API.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Publicacion {
    private int id;
    private Persona publicador;
    private String titulo;
    private Carta carta;
    private String descripcion;
    private List<Carta> intereses;
    private List<String> imagenes;
    private Float precio;
    private EstadoConservacion estadoConservacion;
    private LocalDateTime fechaPublicacion;
    private LocalDateTime fechaCierre;


    public Publicacion(LocalDateTime fechaPublicacion, EstadoConservacion estadoConservacion, List<String> imagenes, Carta carta) {
        this.fechaPublicacion = fechaPublicacion;
        this.estadoConservacion = estadoConservacion;
        this.imagenes = imagenes;
        this.carta = carta;
    }

    public Publicacion(String titulo, Persona publicador, Carta carta, String descripcion, List<Carta> intereses, List<String> imagenes, Float precio, EstadoConservacion estadoConservacion, LocalDateTime fechaPublicacion, LocalDateTime fechaCierre) {
        this.titulo = titulo;
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

    public Publicacion(Carta carta, String titulo, List<Carta> intereses, Float precio, EstadoConservacion estadoConservacion, LocalDateTime fechaPublicacion) {
        this.carta = carta;
        this.titulo = titulo;
        this.intereses = intereses;
        this.precio = precio;
        this.estadoConservacion = estadoConservacion;
        this.fechaPublicacion = fechaPublicacion;
    }
}

