package api.DTO;


import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "estadisticas_dia")
public class EstadisticasDia {
	private String id;
	private LocalDate fecha;
	private long visitas;
	private long ofertasRechazadas;
	private long ofertasEnEspera;
	private long ofertasAceptadas;
	private long publicacionCreadas;
	private long usuarioCreados;
}
