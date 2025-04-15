package manuRodri.intercambioCartas.Repository;

import manuRodri.intercambioCartas.Entity.Carta;
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

  public Carta guardarCarta(Carta carta) {
    carta.setId(idCarta.getAndIncrement());
    tablaCartas.put(carta.getId(), carta);
    return carta;
  }

  public Carta obtenerCarta(int id) {
    return tablaCartas.get(id);
  }

  public List<Carta> obtenerCartas() {
    return new ArrayList<>(tablaCartas.values());
  }

  public void borrarCarta(int id) {
    tablaCartas.remove(id);
  }

  public boolean existeCarta(int id) { return tablaCartas.containsKey(id); }
}
