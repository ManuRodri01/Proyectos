package api.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "solcitudesCreacionCarta")
public class SolicitudCreacionCarta {
    private String id;
    private String idJuego;
    private String nombreCarta;
    private EstadoSolicitud estado;
    private String idSolicitante;
    private LocalDate fechaSolicitud;
    private LocalDate fechaCierre;
}
