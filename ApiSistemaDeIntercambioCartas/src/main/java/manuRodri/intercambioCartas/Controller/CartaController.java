package manuRodri.intercambioCartas.Controller;

import manuRodri.intercambioCartas.DTO.CreacionCartaDTO;
import manuRodri.intercambioCartas.Entity.Carta;
import manuRodri.intercambioCartas.Service.CartaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "Cartas")
public class CartaController {

  private final CartaService cartaService;

  public CartaController(CartaService cartaService) {
    this.cartaService = cartaService;
  }

  @Operation(summary = "Retorna un listado de todas las cartas")
  @ApiResponse(responseCode = "200", description = "Cartas encontradas", content = @Content)
  @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content)
  @GetMapping("/cartas")
  public List<Carta> getCartas() {
    return cartaService.obtenerCartas();
  }

  @Operation(summary = "Crea una carta")
  @ApiResponse(responseCode = "201", description = "Carta creada", content = @Content)
  @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content)
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/cartas")
  public Carta createCarta(@RequestBody CreacionCartaDTO carta) {
    return cartaService.guardarCarta(carta);
  }

  @Operation(summary = "Retorna una carta especifica segun su ID")
  @ApiResponse(responseCode = "200", description = "Carta encontrada", content = @Content)
  @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content)
  @ApiResponse(responseCode = "404", description = "Carta no encontrada", content = @Content)
  @GetMapping("/cartas/{carta-id}")
  public Carta getCarta(@PathVariable("carta-id") int cartaId) {
    return cartaService.obtenerCarta(cartaId);
  }

  @Operation(summary = "Elimina una carta especifica segun su ID")
  @ApiResponse(responseCode = "200", description = "Carta eliminada", content = @Content)
  @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content)
  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/cartas/{carta-id}")
  public void deleteCarta(@PathVariable("carta-id") int cartaId) {
    cartaService.borrarCarta(cartaId);
  }

}
