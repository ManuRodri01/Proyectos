package api.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "publicaciones")
public class Publicacion {
    @Id
    private String id;
    private String publicadorId;
    private String titulo;
    private String cartaId;
    private String juegoId;
    private String descripcion;
    private List<String> intereses;
    private List<String> imagenes;
    private Float precio;
    private EstadoPublicacion estadoPublicacion;
    private boolean estaBorrada;
    private EstadoConservacion estadoConservacion;
    private LocalDateTime fechaPublicacion;
    private LocalDateTime fechaCierre;


    public Publicacion(LocalDateTime fechaPublicacion, EstadoConservacion estadoConservacion, List<String> imagenes, String cartaId) {
        this.fechaPublicacion = fechaPublicacion;
        this.estadoConservacion = estadoConservacion;
        this.imagenes = imagenes;
        this.cartaId = cartaId;
    }

    public Publicacion(String titulo, String publicadorId, String cartaId, String descripcion, List<String> intereses, List<String> imagenes, Float precio, EstadoConservacion estadoConservacion, LocalDateTime fechaPublicacion, LocalDateTime fechaCierre) {
        this.titulo = titulo;
        this.publicadorId = publicadorId;
        this.cartaId = cartaId;
        this.descripcion = descripcion;
        this.intereses = intereses;
        this.imagenes = imagenes;
        this.precio = precio;
        this.estadoConservacion = estadoConservacion;
        this.fechaPublicacion = fechaPublicacion;
        this.fechaCierre = fechaCierre;
    }

    public Publicacion(String cartaId, String titulo, List<String> intereses, Float precio, EstadoConservacion estadoConservacion, LocalDateTime fechaPublicacion) {
        this.cartaId = cartaId;
        this.titulo = titulo;
        this.intereses = intereses;
        this.precio = precio;
        this.estadoConservacion = estadoConservacion;
        this.fechaPublicacion = fechaPublicacion;
    }
}

