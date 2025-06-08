package api.Repository;


import api.Entity.EstadoOferta;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import api.Entity.Oferta;
import api.Exception.OfertaNoEncontradaException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;


@Repository
public class OfertaRepository {

	private final MongoTemplate mongoTemplate;

	public OfertaRepository(MongoTemplate mongoTemplate){

        this.mongoTemplate = mongoTemplate;
    }

	public boolean existeOferta(String id) {
		Query query = new Query(new Criteria().andOperator(
				Criteria.where("estaBorrada").is(false),
				Criteria.where("id").is(id)
		));
		return mongoTemplate.exists(query, Oferta.class);
	}

	public void clear(){
		mongoTemplate.remove(new Query(), Oferta.class);
	}

	public Oferta guardarOferta(Oferta oferta){
		oferta.setEstaBorrada(false);
		mongoTemplate.save(oferta);
		return oferta;
	}

	public Oferta getOferta(String id){
		if(!existeOferta(id)){
			throw new OfertaNoEncontradaException(id);
		}
		Query query = new Query(Criteria.where("id").is(id));
		return mongoTemplate.findOne(query, Oferta.class);
	}

	public Oferta borrarOferta(String id){
		if(!existeOferta(id)){
			throw new OfertaNoEncontradaException(id);
		}
		Query query = new Query(Criteria.where("id").is(id));
		Oferta oferta = mongoTemplate.findOne(query, Oferta.class);
		oferta.setEstaBorrada(true);
		mongoTemplate.save(oferta);
		return oferta;
	}

	public List<Oferta> getOfertasAPublicacion(String publicacionId){
		Query query = new Query(new Criteria().andOperator(
				Criteria.where("publicacionId").is(publicacionId),
				Criteria.where("estaBorrada").is(false)
		));
		return mongoTemplate.find(query, Oferta.class);
	}

	public List<Oferta> getOfertasHechasByPersona(String idPersona) {
		Query query = new Query(new Criteria().andOperator(
				Criteria.where("ofertanteId").is(idPersona),
				Criteria.where("estaBorrada").is(false)
		));
		return mongoTemplate.find(query, Oferta.class);
	}

    public List<Oferta> getOfertasRecibidasByPersona(String idPersona) {
		Query query = new Query(new Criteria().andOperator(
				Criteria.where("publicadorId").is(idPersona),
				Criteria.where("estaBorrada").is(false)
		));
        return mongoTemplate.find(query, Oferta.class);
    }

	public List<Oferta> getOfertasByPublicacion(String publicacionId){
		return getOfertasAPublicacion(publicacionId);
	}

	public List<Oferta> getOfertasRechazadasByFecha(LocalDate fecha){
		EstadoOferta estadoOferta = EstadoOferta.RECHAZADA;
		Date desde = Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date hasta = Date.from(fecha.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		Query query = new Query(new Criteria().andOperator(
				Criteria.where("fechaCierre").gte(desde).lt(hasta),
				Criteria.where("estadoOferta").is(estadoOferta),
				Criteria.where("estaBorrada").is(false)
		));
		return mongoTemplate.find(query, Oferta.class);
	}

	public List<Oferta> getOfertasAceptadasByFecha(LocalDate fecha){
		EstadoOferta estadoOferta = EstadoOferta.ACEPTADA;
		Date desde = Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date hasta = Date.from(fecha.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		Query query = new Query(new Criteria().andOperator(
				Criteria.where("fechaCierre").gte(desde).lt(hasta),
				Criteria.where("estadoOferta").is(estadoOferta),
				Criteria.where("estaBorrada").is(false)
		));
		return mongoTemplate.find(query, Oferta.class);
	}

	public List<Oferta> getOfertasCreadasByFecha(LocalDate fecha){
		Date desde = Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date hasta = Date.from(fecha.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		Query query = new Query(new Criteria().andOperator(
				Criteria.where("fecha").gte(desde).lt(hasta),
				Criteria.where("estaBorrada").is(false)
		));
		return mongoTemplate.find(query, Oferta.class);
	}
}
