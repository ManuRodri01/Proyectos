package api.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "solcitudesCreacionJuego")
public class SolicitudCreacionJuego {
    private String id;
    private String nombreJuego;
    private EstadoSolicitud estado;
    private String idSolicitante;
    private LocalDate fechaSolicitud;
    private LocalDate fechaCierre;
}
