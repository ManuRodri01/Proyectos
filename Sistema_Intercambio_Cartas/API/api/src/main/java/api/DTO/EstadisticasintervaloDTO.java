package api.DTO;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;


public record EstadisticasintervaloDTO (
	 LocalDate fechaDesde,
	 LocalDate fechaHasta,
	 long visitas,
	 long ofertasRechazadas,
	 long ofertasEnEspera,
	 long ofertasAceptadas,
	 long publicacionCreadas,
	 long usuarioCreados
	){}
