package api.Controller;


import api.DTO.CreacionOfertaDTO;
import api.DTO.EdicionOfertaDTO;
import api.DTO.RespuestaOfertaDTO;
import api.Entity.Oferta;
import api.Entity.Persona;
import api.Service.OfertaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/ofertas")
@Tag(name = "Ofertas")
public class OfertaController {

	private final OfertaService ofertaService;

	public OfertaController(OfertaService ofertaService) {
		this.ofertaService = ofertaService;
	}


	@Operation(summary = "Crea una oferta")
	@ApiResponse(responseCode = "200", description = "Oferta creada", content = @Content)
	@ApiResponse(responseCode = "401", description = "No autorizado", content = @Content)
	@PostMapping()
	public CreacionOfertaDTO createOferta(@AuthenticationPrincipal Persona persona,
										  @RequestBody CreacionOfertaDTO oferta) {
		return ofertaService.guardarOferta(oferta, persona);
	}

	@Operation(summary = "Retorna una oferta especifica segun su ID")
	@ApiResponse(responseCode = "200", description = "Oferta encontrada", content = @Content)
	@ApiResponse(responseCode = "401", description = "No autorizado", content = @Content)
	@ApiResponse(responseCode = "404", description = "Oferta no encontrada", content = @Content)
	@GetMapping("/{id}")
	public RespuestaOfertaDTO getOferta(@PathVariable String id, @AuthenticationPrincipal Persona persona) {
		return ofertaService.getOferta(id, persona);
	}

	@GetMapping("/publicaciones/{id-publicacion}")
	public List<RespuestaOfertaDTO> getOfertasByPublicacion(@PathVariable("id-publicacion") String idPubli, @AuthenticationPrincipal Persona persona) {
		return ofertaService.getOfertaByPublicacion(idPubli, persona);
	}

	@Operation(summary = "Elimina una oferta especifica segun su ID")
	@ApiResponse(responseCode = "200", description = "Oferta eliminada", content = @Content)
	@ApiResponse(responseCode = "401", description = "No autorizado", content = @Content)
	@DeleteMapping("/{id}")
	public Oferta borrarOferta(@PathVariable String id, @AuthenticationPrincipal Persona persona) {
		return ofertaService.borrarOferta(id, persona);
	}

	@Operation(summary = "Permite edtar una oferta segun su ID")
	@ApiResponse(responseCode = "200", description = "Oferta editada", content = @Content)
	@ApiResponse(responseCode = "401", description = "No autorizado", content = @Content)
	@ApiResponse(responseCode = "404", description = "Oferta no encontrada", content = @Content)
	@PatchMapping("/{id}")
	public Oferta editarOferta(@PathVariable String id, @AuthenticationPrincipal Persona persona, @RequestBody EdicionOfertaDTO ofertaDTO) {
		return ofertaService.editarOferta(id, persona, ofertaDTO);
	}

	@Operation(summary = "Permite aceptar una oferta segun su ID")
	@ApiResponse(responseCode = "200", description = "Oferta aceptada", content = @Content)
	@ApiResponse(responseCode = "401", description = "No autorizado", content = @Content)
	@ApiResponse(responseCode = "404", description = "Oferta no encontrada", content = @Content)
	@PostMapping("/{id}/aceptar")
	public Oferta aceptarOferta(@PathVariable String id, @AuthenticationPrincipal Persona persona) {

		return ofertaService.aceptarOferta(id, persona);
	}

	@Operation(summary = "Permite rechazar una oferta segun su ID")
	@ApiResponse(responseCode = "200", description = "Oferta rechazada", content = @Content)
	@ApiResponse(responseCode = "401", description = "No autorizado", content = @Content)
	@ApiResponse(responseCode = "404", description = "Oferta no encontrada", content = @Content)
	@PostMapping("{id}/rechazar")
	public Oferta rechazarOferta(@PathVariable String id, @AuthenticationPrincipal Persona persona) {

		return ofertaService.rechazarOferta(id, persona);
	}


}
