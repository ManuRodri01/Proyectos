package api.Repository;

import api.DTO.EstadisticasDia;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class EstadisticaRepository {
	private final MongoTemplate mongoTemplate;

	public EstadisticaRepository(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	public EstadisticasDia guardarEstadistica(EstadisticasDia estadisticasDiarias){
		mongoTemplate.save(estadisticasDiarias);
		return estadisticasDiarias;
	}

	public List<EstadisticasDia> obtenerEstadisticas(LocalDate desde, LocalDate hasta){
		Date dateDesde = Date.from(desde.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date dateHasta = Date.from(hasta.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		Query query = new Query(Criteria.where("fecha").gte(dateDesde).lt(dateHasta));
		return mongoTemplate.find(query, EstadisticasDia.class);
	}

	public boolean existeEstadisticaByFecha(LocalDate dia){
		Date dateDesde = Date.from(dia.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date dateHasta = Date.from(dia.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		Query query = new Query(Criteria.where("fecha").gte(dateDesde).lt(dateHasta));
		return mongoTemplate.exists(query, EstadisticasDia.class);
	}
}
