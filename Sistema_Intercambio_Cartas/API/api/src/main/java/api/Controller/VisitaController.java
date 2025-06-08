package api.Controller;

import api.DTO.VisitaDTO;
import api.Entity.Persona;
import api.Service.VisitaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "visita")
public class VisitaController {
	private final VisitaService visitaService;

	public VisitaController(VisitaService visitaService){
		this.visitaService = visitaService;
	}

	@Operation(summary = "Crea una visita")
	@ApiResponse(responseCode = "200", description = "Visita creado", content = @Content)
	@ApiResponse(responseCode = "401", description = "No autorizado", content = @Content)
	@PostMapping("/visita")
	public VisitaDTO crearVisita(@AuthenticationPrincipal Persona persona) {
		return visitaService.guardarVisita(persona);
	}
}
