package manuRodri.intercambioCartas.Controller;

import manuRodri.intercambioCartas.DTO.CreacionPublicacionDTO;
import manuRodri.intercambioCartas.DTO.FiltroPublicacionesDTO;
import manuRodri.intercambioCartas.Entity.Persona;
import manuRodri.intercambioCartas.Entity.Publicacion;
import manuRodri.intercambioCartas.Service.PublicacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Publicaciones")
public class PublicacionController {
    private final PublicacionService publicacionService;

    public PublicacionController(PublicacionService publicacionService) {
        this.publicacionService = publicacionService;
    }

    @Operation(summary = "Crea una publicacion")
    @ApiResponse(responseCode = "200", description = "Publicacion creada", content = @Content)
    @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content)
    @PostMapping("/publicaciones")
    public CreacionPublicacionDTO createPublicacion(@AuthenticationPrincipal Persona persona, @RequestBody CreacionPublicacionDTO publicacion) {
        return publicacionService.guardarPublicacion(publicacion, persona);
    }

    @Operation(summary = "Retorna un listado de todas las publicaciones")
    @ApiResponse(responseCode = "200", description = "Publicaciones encontradas", content = @Content)
    @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content)
    @GetMapping("/publicaciones")
    public List<Publicacion> getPublicaciones(@RequestBody FiltroPublicacionesDTO filtro) {
        return publicacionService.getPublicaciones(filtro);
    }

    @Operation(summary = "Retorna una publicacion especifica segun su ID")
    @ApiResponse(responseCode = "200", description = "Publicacion encontrada", content = @Content)
    @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "404", description = "Publicacion no encontrada", content = @Content)
    @GetMapping("/publicaciones/{publicacion-id}")
    public Publicacion getPublicacion(@PathVariable("publicacion-id") int id) {
        return publicacionService.getPublicacion(id);
    }


}
