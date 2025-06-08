package api.Entity;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "ofertas")
public class Oferta {
    private String id;
    private String publicacionId;
    private String publicadorId;
    private String ofertanteId;
    private List<String> cartas;
    private Float valor;
    private EstadoOferta estadoOferta;
    private boolean estaBorrada;
    private LocalDate fecha;
    private LocalDate fechaCierre;

    public Oferta(String publicacionId, EstadoOferta estadoOferta, LocalDate fecha) {
        this.publicacionId = publicacionId;
        this.estadoOferta = estadoOferta;
        this.fecha = fecha;
    }

    public Oferta(EstadoOferta estadoOferta, String publicacionId, LocalDate fecha, String ofertanteId) {
        this.estadoOferta = estadoOferta;
        this.publicacionId = publicacionId;
        this.fecha = fecha;
        this.ofertanteId = ofertanteId;
    }
}
