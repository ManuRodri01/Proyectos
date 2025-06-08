package api.Controller;

import api.DTO.CreacionPublicacionDTO;
import api.DTO.FiltroPublicacionesDTO;
import api.DTO.PaginacionPublicacionDTO;
import api.DTO.RespuetaPublicacionDTO;
import api.Entity.Persona;
import api.Entity.EstadoConservacion;
import api.Service.PublicacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    public PaginacionPublicacionDTO getPublicaciones(@RequestParam(required = false) String cartaId, @RequestParam(required = false) String juegoId,
                                                     @RequestParam(required = false) String nombrePublicacion, @RequestParam(required = false) EstadoConservacion estadoConservacion,
                                                     @RequestParam(required = false)LocalDateTime desdeFecha, @RequestParam(required = false)LocalDateTime hastaFecha,
                                                     @RequestParam(required = false) Float desdePrecio, @RequestParam(required = false) Float hastaPrecio,
                                                     @RequestParam(required = false) List<String> intereses, @RequestParam(defaultValue = "1") Integer pagina) {
        if(nombrePublicacion != null) {
            nombrePublicacion = nombrePublicacion.replace("-", " ");
        }
        FiltroPublicacionesDTO filtros = new FiltroPublicacionesDTO(cartaId,juegoId,nombrePublicacion,estadoConservacion,desdeFecha,hastaFecha,desdePrecio,hastaPrecio,intereses);
        return publicacionService.getPublicaciones(filtros, pagina);
    }

    @Operation(summary = "Retorna una publicacion especifica segun su ID")
    @ApiResponse(responseCode = "200", description = "Publicacion encontrada", content = @Content)
    @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "404", description = "Publicacion no encontrada", content = @Content)
    @GetMapping("/publicaciones/{publicacion-id}")
    public RespuetaPublicacionDTO getPublicacion(@PathVariable("publicacion-id") String id, @AuthenticationPrincipal Persona persona) {
        return publicacionService.getPublicacion(id, persona);
    }

    @Operation(summary = "Elimina una publicacion especifica segun su ID")
    @ApiResponse(responseCode = "200", description = "Publicacion eliminada", content = @Content)
    @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content)
    @DeleteMapping("/publicaciones/{publicacion-id}")
    public void deletePublicacion(@PathVariable("publicacion-id") String publiId, @AuthenticationPrincipal Persona persona) {
        publicacionService.borrarPublicacion(publiId, persona);
    }



}
