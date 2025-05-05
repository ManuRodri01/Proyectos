package grupo1.tpTACS_API.Repository;


import grupo1.tpTACS_API.Entity.Carta;
import grupo1.tpTACS_API.Entity.Juego;
import grupo1.tpTACS_API.Exception.CartaNoEncontradaException;
import grupo1.tpTACS_API.Exception.CartaYaExistenteException;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class CartaRepository {
  private Map<Integer, Carta> tablaCartas;

  private AtomicInteger idCarta = new AtomicInteger(0);

  public CartaRepository() {
    tablaCartas = new HashMap<>();
  }

  public boolean existeCarta(Carta carta) {
    return tablaCartas.values().stream().anyMatch(cartaLista -> carta.getNombre().equals(cartaLista.getNombre()) && carta.getJuego().getId() == cartaLista.getJuego().getId());
  }

  public Carta guardarCarta(Carta carta) {
    if(existeCarta(carta)) {
      throw new CartaYaExistenteException(carta.getNombre());
    }
    carta.setId(idCarta.getAndIncrement());
    tablaCartas.put(carta.getId(), carta);
    return carta;
  }

  public List<Carta> getCartasByJuego(int juegoId) {
    List<Carta> cartas = new ArrayList<>();
    for (Carta carta : tablaCartas.values()) {
      if(carta.getJuego().getId() == juegoId){
        cartas.add(carta);
      }
    }
    return cartas;
  }

  public Carta obtenerCarta(int id) {
    return tablaCartas.get(id);
  }

  public List<Carta> obtenerCartas() {
    return new ArrayList<>(tablaCartas.values());
  }

  public void borrarCarta(int id) {
    if(tablaCartas.remove(id) == null){
      throw new CartaNoEncontradaException(id);
    }
  }

  public boolean existeCarta(int id) { return tablaCartas.containsKey(id); }
}
