package api.Repository;

import api.Entity.Visita;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class VisitaRepository {
	private final MongoTemplate mongoTemplate;

	public VisitaRepository(MongoTemplate mongoTemplate){
		this.mongoTemplate = mongoTemplate;
	}

	public Visita guardarVisita(Visita visita){
		mongoTemplate.save(visita);
		return visita;
	}

	public List<Visita> getVisitasByFecha(LocalDate fecha) {
		Date desde = Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date hasta = Date.from(fecha.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		Query query = new Query(Criteria.where("fecha").gte(desde).lt(hasta));
		return mongoTemplate.find(query, Visita.class);
	}
}
