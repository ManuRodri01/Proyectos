package api.Repository;


import java.util.List;


import api.DTO.CreacionJuegoDTO;
import api.Entity.Juego;
import api.Exception.JuegoNoEncontradoException;
import api.Exception.JuegoYaExistenteException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class JuegoRepository {

    private final MongoTemplate mongoTemplate;

    public JuegoRepository(MongoTemplate mongoTemplate){

        this.mongoTemplate = mongoTemplate;
    }

    public boolean existeJuego(String id){
        Query query = new Query(new Criteria().andOperator(
                Criteria.where("estaBorrado").is(false),
                Criteria.where("id").is(id)
        ));
        return mongoTemplate.exists(query, Juego.class);
    }

    public void clear(){
        mongoTemplate.remove(new Query(), Juego.class);
    }

    public Juego existeJuegoConEseNombre(String nombreJuego) {
        Query query = new Query(Criteria.where("nombre").is(nombreJuego));
        Juego juego = mongoTemplate.findOne(query, Juego.class);
        if(juego != null){
                if(juego.isEstaBorrado()){
                    juego.setEstaBorrado(false);
                    mongoTemplate.save(juego);
                    return juego;
                }
                else{
                    throw new JuegoYaExistenteException(nombreJuego);
                }

        }
        return null;
    }

    public boolean existeJuegoNombre(String nombreJuego) {
        Query query = new Query(Criteria.where("nombre").is(nombreJuego));
        Juego juego = mongoTemplate.findOne(query, Juego.class);
        return juego != null;
    }

    public List<Juego> getTodos(){
        Query query = new Query(Criteria.where("estaBorrado").is(false));
        return mongoTemplate.find(query, Juego.class);
    }

    public Juego findOneByNombre(String nombre){
        Query query = new Query(Criteria.where("nombre").is(nombre));
        Juego juego = mongoTemplate.findOne(query, Juego.class);
        if(juego == null || juego.isEstaBorrado()){
            throw new JuegoNoEncontradoException(nombre);
        }
        return juego;
    }

    public Juego findOneById(String id){
        if(!existeJuego(id)){
            throw new JuegoNoEncontradoException(id);
        }
        Query query = new Query(Criteria.where("id").is(id));
        return mongoTemplate.findOne(query, Juego.class);
    }

    public Juego guardarJuego(CreacionJuegoDTO juego){
        Juego juegoGuardado = existeJuegoConEseNombre(juego.nombre());
        if(juegoGuardado == null){
            juegoGuardado = new Juego();
            juegoGuardado.setNombre(juego.nombre());
            juegoGuardado.setEstaBorrado(false);
            mongoTemplate.save(juegoGuardado);
        }
        return juegoGuardado;
    }

    public List<Juego> borrarJuego(String id){
        if(existeJuego(id)){
            throw new JuegoNoEncontradoException(id);
        }
        Query query = new Query(Criteria.where("id").is(id));
        Juego juego = mongoTemplate.findOne(query, Juego.class);
        juego.setEstaBorrado(true);
        mongoTemplate.save(juego);
        return getTodos();
    }

}
