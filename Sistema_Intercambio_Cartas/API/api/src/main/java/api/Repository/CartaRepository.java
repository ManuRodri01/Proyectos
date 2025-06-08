package api.Repository;


import api.Entity.Carta;
import api.Exception.CartaNoEncontradaException;
import api.Exception.CartaYaExistenteException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CartaRepository {

  private final MongoTemplate mongoTemplate;

  public CartaRepository(MongoTemplate mongoTemplate) {
      this.mongoTemplate = mongoTemplate;
  }

  public Carta existeCartaConEseNombreYJuego(Carta carta) {
    Query query = new Query();
    query.addCriteria(new Criteria().andOperator(
            Criteria.where("nombre").is(carta.getNombre()),
            Criteria.where("juegoId").is(carta.getJuegoId())
    ));
    Carta cartaExistente = mongoTemplate.findOne(query, Carta.class);
    if(cartaExistente != null) {
        if(cartaExistente.isEstaBorrada()) {
          cartaExistente.setEstaBorrada(false);
          mongoTemplate.save(cartaExistente);
          return cartaExistente;
        }
        else{
          throw new CartaYaExistenteException(cartaExistente.getNombre());
        }

    }
    return null;
  }

  public boolean existeCartaNombreYJuego(String nombreCarta, String idJuego){
    Query query = new Query(new Criteria().andOperator(
            Criteria.where("nombre").is(nombreCarta),
            Criteria.where("juegoId").is(idJuego)
    ));
    return mongoTemplate.exists(query, Carta.class);
  }

  public boolean existeCarta(String cartaId) {
    Query query = new Query(new Criteria().andOperator(
            Criteria.where("id").is(cartaId),
            Criteria.where("estaBorrada").is(false)
    ));
    return mongoTemplate.exists(query, Carta.class);
  }

  public void clear(){
    mongoTemplate.remove(new Query(), Carta.class);
  }

  public Carta guardarCarta(Carta carta) {
    Carta cartaExistente = existeCartaConEseNombreYJuego(carta);
    if(cartaExistente == null) {
      carta.setEstaBorrada(false);
      mongoTemplate.save(carta);
      return carta;
    }
    return cartaExistente;
  }

  public List<Carta> getCartasByJuego(String juegoId) {
    Query query = new Query(new Criteria().andOperator(
            Criteria.where("juegoId").is(juegoId),
            Criteria.where("estaBorrada").is(false)
    ));
    return mongoTemplate.find(query, Carta.class);
  }

  public Carta obtenerCarta(String id) {
    if(!existeCarta(id)) {
      throw new CartaNoEncontradaException(id);
    }
    Query query = new Query(Criteria.where("id").is(id));
    return mongoTemplate.findOne(query, Carta.class);
  }

  public List<Carta> obtenerCartas() {
    Query query = new Query(Criteria.where("estaBorrada").is(false));
    return mongoTemplate.find(query, Carta.class);
  }

  public void borrarCarta(String id) {
    Carta carta = obtenerCarta(id);
    carta.setEstaBorrada(true);
    mongoTemplate.save(carta);
  }
}
