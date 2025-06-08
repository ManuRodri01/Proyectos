package api.Controller;


import api.DTO.CreacionJuegoDTO;
import api.Entity.Juego;
import api.Service.JuegoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@Tag(name = "Juegos")
public class JuegoController {

    private final JuegoService juegoService;

    public JuegoController(JuegoService juegoService) {
        this.juegoService = juegoService;
    }

    @Operation(summary = "Retorna un listado de todos los juegos")
    @ApiResponse(responseCode = "200", description = "Juegos encontrados", content = @Content)
    @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content)
    @GetMapping("/juegos")
    public List<Juego> getJuegos() {
        return juegoService.getJuegos();
    }

    @Operation(summary = "Crea un juego")
    @ApiResponse(responseCode = "200", description = "Juego creado", content = @Content)
    @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/juegos")
    public Juego createJuego(@RequestBody CreacionJuegoDTO juego) {
        return juegoService.guardarJuego(juego);
    }

    @Operation(summary = "Elimina un juego especifico segun su ID")
    @ApiResponse(responseCode = "200", description = "Juego eliminado", content = @Content)
    @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/juegos/{juego-id}")
    public List<Juego> borrarJuego(@PathVariable("juego-id") String id){
        return juegoService.borrarJuego(id);
    }

    @Operation(summary = "Retorna un juego especifico segun su ID")
    @ApiResponse(responseCode = "200", description = "Juego encontrado", content = @Content)
    @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content)
    @ApiResponse(responseCode = "404", description = "Juego no encontrado", content = @Content)
    @GetMapping("/juegos/{juego-id}")
    public Juego getJuego(@PathVariable("juego-id") String id) {
        return juegoService.getJuego(id);
    }


}
