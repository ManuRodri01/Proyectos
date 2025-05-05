package grupo1.tpTACS_API.Service;


import grupo1.tpTACS_API.DTO.CreacionCartaDTO;
import grupo1.tpTACS_API.DTO.RespuestaCartaDTO;
import grupo1.tpTACS_API.Entity.Carta;
import grupo1.tpTACS_API.Entity.Juego;
import grupo1.tpTACS_API.Exception.CartaNoEncontradaException;
import grupo1.tpTACS_API.Repository.CartaRepository;
import grupo1.tpTACS_API.Repository.JuegoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartaService {

  private final CartaRepository cartaRepository;
  private final JuegoService juegoService;
  private final CartaMapper cartaMapper;

  public CartaService(CartaRepository cartaRepository, JuegoService juegoService, CartaMapper cartaMapper) {
    this.cartaRepository = cartaRepository;
      this.juegoService = juegoService;
      this.cartaMapper = cartaMapper;
  }

  public Carta guardarCarta(CreacionCartaDTO carta) {
    Juego juego = juegoService.getJuego(carta.idJuego());
    Carta cartaAGuardar = new Carta(carta.nombre(), juego);
    return cartaRepository.guardarCarta(cartaAGuardar);
  }

  public List<RespuestaCartaDTO> getCartasByJuego(int juegoId) {
    return cartaRepository.getCartasByJuego(juegoId).stream().map(cartaMapper::toRespuestaCartaDTO).collect(Collectors.toList());
  }

  public RespuestaCartaDTO obtenerCarta(int id) {
    existeCarta(id);
    return cartaMapper.toRespuestaCartaDTO(cartaRepository.obtenerCarta(id));
  }

  public List<RespuestaCartaDTO> obtenerCartas() {
    return cartaRepository.obtenerCartas().stream().map(cartaMapper::toRespuestaCartaDTO).collect(Collectors.toList());
  }

  public void borrarCarta(int id) {
    cartaRepository.borrarCarta(id);
  }

  public void existeCarta(int id){
    if (!cartaRepository.existeCarta(id)){
      throw new CartaNoEncontradaException(id);
    }
  }
}
