package manuRodri.intercambioCartas.Service;

import manuRodri.intercambioCartas.DTO.CreacionCartaDTO;
import manuRodri.intercambioCartas.Entity.Carta;
import manuRodri.intercambioCartas.Entity.Juego;
import manuRodri.intercambioCartas.Exception.CartaNoEncontradaException;
import manuRodri.intercambioCartas.Repository.CartaRepository;
import manuRodri.intercambioCartas.Repository.JuegoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartaService {

  private final CartaRepository cartaRepository;
  private final JuegoRepository juegoRepository;

  public CartaService(CartaRepository cartaRepository, JuegoRepository juegoRepository) {
    this.cartaRepository = cartaRepository;
      this.juegoRepository = juegoRepository;
  }

  public Carta guardarCarta(CreacionCartaDTO carta) {
    Juego juego = juegoRepository.findOneById(carta.idJuego());
    Carta cartaAGuardar = new Carta(carta.nombre(), juego);
    return cartaRepository.guardarCarta(cartaAGuardar);
  }

  public Carta obtenerCarta(int id) {
    existeCarta(id);
    return cartaRepository.obtenerCarta(id);
  }

  public List<Carta> obtenerCartas() {
    return cartaRepository.obtenerCartas();
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
