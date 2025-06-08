package api.Service;

import api.DTO.EstadisticasDia;
import api.DTO.EstadisticasintervaloDTO;
import api.Repository.EstadisticaRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;


@Service
public class EstadisticaService {
	private final EstadisticaRepository estadisticaRepository;
	private final OfertaService ofertaService;
	private final VisitaService visitaService;
	private final PublicacionService publicacionService;
	private final PersonaService personaService;

	public EstadisticaService(EstadisticaRepository estadisticaRepository,
															 OfertaService ofertaService,
															 PersonaService personaService,
															 PublicacionService publicacionService,
															 VisitaService visitaService){
		this.estadisticaRepository = estadisticaRepository;
		this.ofertaService = ofertaService;
		this.personaService = personaService;
		this.publicacionService = publicacionService;
		this.visitaService = visitaService;
	}

	public EstadisticasDia guardarEstadistica(EstadisticasDia estadisticasDiarias){
		return estadisticaRepository.guardarEstadistica(estadisticasDiarias);
	}

	public void generarEstadisticasDia(LocalDate dia){
		long totalOfertasRechazadas = ofertaService.getOfertasRechazadasByFecha(dia).size();
		long totalOfertasAceptadas = ofertaService.getOfertasAceptadasByFecha(dia).size();
		long totalOfertasCreadas = ofertaService.getOfertasCreadasByFecha(dia).size();
		long totalPersonasCreadas = personaService.getPersonasByFechaCreacion(dia).size();
		long totalPublicacionesCreadas = publicacionService.getPublicacionesCreadasByFecha(dia).size();
		long totalVisitas = visitaService.obtenerVisitasByFecha(dia).size();

		EstadisticasDia estadisticasDia = new EstadisticasDia();
		estadisticasDia.setFecha(dia);
		estadisticasDia.setVisitas(totalVisitas);
		estadisticasDia.setOfertasAceptadas(totalOfertasAceptadas);
		estadisticasDia.setOfertasRechazadas(totalOfertasRechazadas);
		estadisticasDia.setOfertasEnEspera(totalOfertasCreadas);
		estadisticasDia.setUsuarioCreados(totalPersonasCreadas);
		estadisticasDia.setPublicacionCreadas(totalPublicacionesCreadas);

		guardarEstadistica(estadisticasDia);
	}

	public EstadisticasintervaloDTO obtenerEstadistica(LocalDate desde, LocalDate hasta) {
		List<LocalDate> fechas = desde.datesUntil(hasta.plusDays(1)).toList();
		for (int i = 0; i < fechas.size(); i++) {
			if (!existeEstadisticaByFecha(fechas.get(i))) {
				generarEstadisticasDia(fechas.get(i));
			}
		}


		List<EstadisticasDia> estadisticas = estadisticaRepository.obtenerEstadisticas(desde, hasta);


		long totalVisitas = 0;
		long totalOfertasRechazadas = 0;
		long totalOfertasAceptadas = 0;
		long totalOfertasEnEspera = 0;
		long totalPublicacionesCreadas = 0;
		long totalUsuariosCreados = 0;
		for(int i = 0; i< estadisticas.size(); i++){
			EstadisticasDia stat = estadisticas.get(i);

			totalVisitas += stat.getVisitas();
			totalOfertasAceptadas += stat.getOfertasAceptadas();
			totalOfertasEnEspera += stat.getOfertasEnEspera();
			totalOfertasRechazadas += stat.getOfertasRechazadas();
			totalPublicacionesCreadas += stat.getPublicacionCreadas();
			totalUsuariosCreados += stat.getUsuarioCreados();
		}


		return new EstadisticasintervaloDTO(
				desde,
				hasta,
				totalVisitas,
				totalOfertasRechazadas,
				totalOfertasEnEspera,
				totalOfertasAceptadas,
				totalPublicacionesCreadas,
				totalUsuariosCreados);
	}

	public boolean existeEstadisticaByFecha(LocalDate dia){
		return estadisticaRepository.existeEstadisticaByFecha(dia);
	}
}
