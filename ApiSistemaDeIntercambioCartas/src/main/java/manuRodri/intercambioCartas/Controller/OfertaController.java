package manuRodri.intercambioCartas.Controller;

import manuRodri.intercambioCartas.DTO.CreacionOfertaDTO;
import manuRodri.intercambioCartas.Entity.Oferta;
import manuRodri.intercambioCartas.Entity.Persona;

import manuRodri.intercambioCartas.Service.OfertaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/ofertas")
@Tag(name = "Ofertas")
public class OfertaController {

	@Autowired
	private OfertaService ofertaService;

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
	public Oferta getOferta(@PathVariable int id, @AuthenticationPrincipal Persona persona) {
		return ofertaService.getOferta(id, persona);
	}

	@Operation(summary = "Elimina una oferta especifica segun su ID")
	@ApiResponse(responseCode = "200", description = "Oferta eliminada", content = @Content)
	@ApiResponse(responseCode = "401", description = "No autorizado", content = @Content)
	@DeleteMapping("/{id}")
	public Oferta borrarOferta(@PathVariable int id, @AuthenticationPrincipal Persona persona) {
		return ofertaService.borrarOferta(id, persona);
	}

	@Operation(summary = "Permite aceptar una oferta segun su ID")
	@ApiResponse(responseCode = "200", description = "Oferta aceptada", content = @Content)
	@ApiResponse(responseCode = "401", description = "No autorizado", content = @Content)
	@ApiResponse(responseCode = "404", description = "Oferta no encontrada", content = @Content)
	@PostMapping("/{id}/aceptar")
	public Oferta aceptarOferta(@PathVariable int id, @AuthenticationPrincipal Persona persona) {

		return ofertaService.aceptarOferta(id, persona);
	}

	@Operation(summary = "Permite rechazar una oferta segun su ID")
	@ApiResponse(responseCode = "200", description = "Oferta rechazada", content = @Content)
	@ApiResponse(responseCode = "401", description = "No autorizado", content = @Content)
	@ApiResponse(responseCode = "404", description = "Oferta no encontrada", content = @Content)
	@PostMapping("{id}/rechazar")
	public Oferta rechazarOferta(@PathVariable int id, @AuthenticationPrincipal Persona persona) {

		return ofertaService.rechazarOferta(id, persona);
	}


}
