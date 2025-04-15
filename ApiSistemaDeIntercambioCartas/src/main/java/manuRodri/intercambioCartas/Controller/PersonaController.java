package manuRodri.intercambioCartas.Controller;

import manuRodri.intercambioCartas.DTO.OfertaPersonaDTO;
import manuRodri.intercambioCartas.Entity.Persona;
import manuRodri.intercambioCartas.DTO.PublicacionesDePersonaDTO;
import manuRodri.intercambioCartas.Service.PersonaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@EnableMethodSecurity(prePostEnabled = true)
@RestController
@Tag(name = "Personas")
public class PersonaController {

    private final PersonaService personaService;

    public PersonaController(PersonaService personaService) {
        this.personaService = personaService;
    }

    @Operation(summary = "Retona los datos de la persona autenticada")
    @ApiResponse(responseCode = "200", description = "Persona encontrada", content = @Content)
    @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content)
    @GetMapping("/personas")
    public Persona getPersona(@AuthenticationPrincipal Persona persona) {
        return personaService.getPersona(persona.getUsername());
    }

    @Operation(summary = "Retona un listado de las publicaciones de la persona autenticada")
    @ApiResponse(responseCode = "200", description = "Publicaciones encontradas", content = @Content)
    @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content)
    @GetMapping("/personas/publicaciones")
    public List<PublicacionesDePersonaDTO> getPublicaciones(@AuthenticationPrincipal Persona persona) {
        return personaService.getPublicacionesDePersona(persona.getUsername());
    }

    @Operation(summary = "Retona un listado de las ofertas realizadas por la persona autenticada")
    @ApiResponse(responseCode = "200", description = "Ofertas encontradas", content = @Content)
    @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content)
    @GetMapping("/personas/ofertas-hechas")
    public List<OfertaPersonaDTO> getOfertasHechas(@AuthenticationPrincipal Persona persona) {
        return personaService.getOfertasHechas(persona.getUsername());
    }

    @Operation(summary = "Retona un listado de las ofertas recibidas por la persona autenticada")
    @ApiResponse(responseCode = "200", description = "Ofertas encontradas", content = @Content)
    @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content)
    @GetMapping("/personas/ofertas-recibidas")
    public List<OfertaPersonaDTO> getOfertasRecibidas(@AuthenticationPrincipal Persona persona) {
        return personaService.getOfertasRecibidas(persona.getUsername());
    }

    @Operation(summary = "Retorna las estadisticas (TO DO)")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/estadisticas")
    public String getEstadisticas() {
        return "Estadisticas";
    }


}
