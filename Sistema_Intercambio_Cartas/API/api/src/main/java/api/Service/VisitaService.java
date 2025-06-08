package api.Service;

import api.DTO.VisitaDTO;
import api.Entity.Persona;
import api.Entity.Visita;
import api.Repository.VisitaRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class VisitaService {
	private final VisitaRepository visitaRepository;

	public VisitaService(VisitaRepository visitaRepository){
		this.visitaRepository = visitaRepository;
	}

	public VisitaDTO guardarVisita(Persona persona){
		Visita visita = new Visita();
		visita.setFecha(LocalDate.now());
		visita.setPersona(persona);
		visitaRepository.guardarVisita(visita);

		return new VisitaDTO(persona.getId(),LocalDate.now());
	}

	public List<Visita> obtenerVisitasByFecha(LocalDate fecha){
		return visitaRepository.getVisitasByFecha(fecha);
	}
}
