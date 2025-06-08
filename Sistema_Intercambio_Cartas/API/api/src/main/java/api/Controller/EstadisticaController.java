package api.Controller;

import api.DTO.EstadisticasintervaloDTO;
import api.Service.EstadisticaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Estadisticas")
public class EstadisticaController {
	private final EstadisticaService estadisticaService;

	public EstadisticaController(EstadisticaService estadisticaService){
		this.estadisticaService = estadisticaService;
	}

	@GetMapping("/estadisticas")
	public EstadisticasintervaloDTO obtenerEstadisticas(@RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
																											@RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta){
		return estadisticaService.obtenerEstadistica(desde, hasta);
	}

}
