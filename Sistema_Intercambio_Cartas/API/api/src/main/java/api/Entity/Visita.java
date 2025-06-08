package api.Entity;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "visitas")
@NoArgsConstructor
public class Visita {
	@Id
	private String id;
	private Persona persona;
	private LocalDate fecha;
	private String url;

	public Visita(Persona persona, String url){
		this.persona = persona;
		fecha = LocalDate.now();
	}
}
