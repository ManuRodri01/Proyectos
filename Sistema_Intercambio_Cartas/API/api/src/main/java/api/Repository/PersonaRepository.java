package api.Repository;


import api.Entity.Persona;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public class PersonaRepository {

    private final MongoTemplate mongoTemplate;


    public PersonaRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void clear(){
        mongoTemplate.remove(new Query(), Persona.class);
    }

    public boolean existePersonaById(String id){
        Query query = new Query(Criteria.where("id").is(id));
        return mongoTemplate.exists(query, Persona.class);
    }

    public boolean existePersonaByNombre(String nombre){
        Query query = new Query(Criteria.where("nombre").is(nombre));
        return mongoTemplate.exists(query, Persona.class);
    }

    public Persona guardarPersona (Persona persona){
        mongoTemplate.save(persona);
        return persona;
    }

    public Persona getPersona (String username) {
        return findByNombre(username).orElse(null);
    }

    public Persona getPersonaById (String id) {
        Query query = new Query(Criteria.where("id").is(id));
        return mongoTemplate.findOne(query, Persona.class);
    }

    public Optional<Persona> findByNombre (String nombre) {
        Query query = new Query(Criteria.where("nombre").is(nombre));
        return Optional.ofNullable(mongoTemplate.findOne(query, Persona.class));
    }

    public List<Persona> getPersonasByFechaCreacion(LocalDate fecha){
        Date desde = Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date hasta = Date.from(fecha.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Query query = new Query(Criteria.where("fechaCreacion").gte(desde).lt(hasta));
        return mongoTemplate.find(query, Persona.class);
    }
}
