package api.Service;


import api.DTO.CreacionCartaDTO;
import api.DTO.RespuestaCartaDTO;
import api.Entity.Carta;
import api.Exception.CartaNoEncontradaException;
import api.Repository.CartaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartaService {

  private final CartaRepository cartaRepository;
  private final CartaMapper cartaMapper;

  public CartaService(CartaRepository cartaRepository, CartaMapper cartaMapper) {
    this.cartaRepository = cartaRepository;
      this.cartaMapper = cartaMapper;
  }

  public Carta guardarCarta(CreacionCartaDTO carta) {
    Carta cartaAGuardar = new Carta(carta.nombre(), carta.idJuego());
    return cartaRepository.guardarCarta(cartaAGuardar);
  }

  public List<RespuestaCartaDTO> getCartasByJuego(String juegoId) {
    return cartaRepository.getCartasByJuego(juegoId).stream().map(cartaMapper::toRespuestaCartaDTO).collect(Collectors.toList());
  }

  public RespuestaCartaDTO obtenerCarta(String id) {
    return cartaMapper.toRespuestaCartaDTO(cartaRepository.obtenerCarta(id));
  }

  public Carta getCartaNoDTO(String id) {
    return cartaRepository.obtenerCarta(id);
  }

  public List<RespuestaCartaDTO> obtenerCartas() {
    return cartaRepository.obtenerCartas().stream().map(cartaMapper::toRespuestaCartaDTO).collect(Collectors.toList());
  }

  public boolean existeCartaNombreYJuego(String nombreCarta, String idJuego){
    return cartaRepository.existeCartaNombreYJuego(nombreCarta, idJuego);
  }

  public void borrarCarta(String id) {
    cartaRepository.borrarCarta(id);
  }

  public void existeCarta(String id){
    if (!cartaRepository.existeCarta(id)){
      throw new CartaNoEncontradaException(id);
    }
  }
}
